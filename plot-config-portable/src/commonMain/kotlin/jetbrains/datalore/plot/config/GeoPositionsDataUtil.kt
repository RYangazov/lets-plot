/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.map.GeoPositionField
import jetbrains.datalore.plot.config.Option.Geom.Choropleth

object GeoPositionsDataUtil {
    // Fixed columns in dataframe supplied by 'geo-coding'
    const val GEOCODING_OSM_ID_COLUMN = "id"
    const val GEOCODING_REQUEST_COLUMN = "request"

    // Auto-generated by GeoPositionMappingChange
    const val MAP_JOIN_ID_COLUMN = "key"
    // Provided by regions object
    const val MAP_REGION_COLUMN = "region"

    const val MAP_OSM_ID_COLUMN = "__geoid__"
    const val MAP_GEOMETRY_COLUMN = "__geometry__"

    // Columns can be used as request
    val GEO_POSITIONS_KEYS = listOf(
        MAP_REGION_COLUMN,
        MAP_JOIN_ID_COLUMN
    )

    private val GEOMS_SUPPORT = mapOf(
            GeomKind.MAP to GeoDataSupport.boundary(),
            GeomKind.POLYGON to GeoDataSupport.boundary(),
            GeomKind.POINT to GeoDataSupport.centroid(),
            GeomKind.RECT to GeoDataSupport.limit(),
            GeomKind.PATH to GeoDataSupport.centroid()
    )

    fun isGeomSupported(geomKind: GeomKind): Boolean {
        return GEOMS_SUPPORT.containsKey(geomKind)
    }

    fun getGeoDataKind(geomKind: GeomKind): GeoDataKind {
        return GEOMS_SUPPORT[geomKind]!!.geoDataKind
    }

    internal fun hasGeoPositionsData(layerConfig: LayerConfig): Boolean {
        return layerConfig.has(Choropleth.GEO_POSITIONS)
    }

    internal fun getGeoPositionsData(layerConfig: LayerConfig): DataFrame {
        val mapOptions = layerConfig.getMap(Choropleth.GEO_POSITIONS)
        return ConfigUtil.createDataFrame(mapOptions)
    }

    internal fun initDataAndMappingForGeoPositions(
        geomKind: GeomKind, layerData: DataFrame, mapOptions: DataFrame, mappingOptions: Map<*, *>): Pair<DataFrame, Map<Aes<*>, Variable>> {
        @Suppress("NAME_SHADOWING")
        var layerData = layerData

        val leftMapId = mappingOptions[Option.Mapping.MAP_ID]
        checkState(leftMapId != null || mappingOptions.isEmpty(), "'map_id' aesthetic is required to show data on map")

        return when {
            leftMapId != null -> {
                val rightMapId = getGeoPositionsIdVar(mapOptions).name
                layerData = ConfigUtil.rightJoin(
                    layerData,
                    leftMapId.toString(),
                    mapOptions,
                    rightMapId
                )

                val aesMapping = HashMap(
                    ConfigUtil.createAesMapping(
                        layerData,
                        mappingOptions
                    )
                )
                aesMapping.putAll(
                    generateMappings(
                        geomKind,
                        layerData
                    )
                )
                Pair(layerData, aesMapping)
            }
            else -> // just show a blank map
                Pair(mapOptions,
                    generateMappings(geomKind, mapOptions)
                )
        }
    }

    private fun generateMappings(geomKind: GeomKind, layerData: DataFrame): Map<Aes<*>, Variable> {
        return if (isGeomSupported(geomKind)) {
            GEOMS_SUPPORT[geomKind]!!.generateMapping(layerData)
        } else {
            emptyMap()
        }
    }

    private fun getGeoPositionsIdVar(mapOptions: DataFrame): Variable {
        val variable = findFirstVariable(mapOptions, GEO_POSITIONS_KEYS)
        if (variable != null) {
            return variable
        }

        throw IllegalArgumentException(
            geoPositionsColumnNotFoundError(
                "region id",
                GEO_POSITIONS_KEYS
            )
        )
    }

    private fun findMapping(aes: Aes<*>, names: List<String>, dataFrame: DataFrame): Map<Aes<*>, Variable> {
        val variable = findFirstVariable(dataFrame, names)
                ?: throw IllegalArgumentException(
                    geoPositionsColumnNotFoundError(
                        aes.name + "-column",
                        names
                    )
                )
        return mapOf(aes to variable)
    }

    private fun findFirstVariable(data: DataFrame, names: Iterable<String>): Variable? {
        val variableMap = DataFrameUtil.variables(data)
        for (name in names) {
            if (variableMap.containsKey(name)) {
                return variableMap[name]
            }
        }
        return null
    }

    private fun geoPositionsColumnNotFoundError(what: String, names: List<String>): String {
        return "Can't draw map: " + what + " not found. Geo position data must contain column " +
                names.joinToString(" or ") { s -> "'$s'" }
    }

    enum class GeoDataKind {
        CENTROID,
        LIMIT,
        BOUNDARY
    }

    internal class GeoDataSupport(val geoDataKind: GeoDataKind, private val mappingsGenerator: (DataFrame) -> Map<Aes<*>, Variable>) {

        fun generateMapping(df: DataFrame): Map<Aes<*>, Variable> {
            return mappingsGenerator(df)
        }

        companion object {
            fun boundary(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.BOUNDARY, ::createPointMapping)
            }

            fun centroid(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.CENTROID, ::createPointMapping)
            }

            fun limit(): GeoDataSupport {
                return GeoDataSupport(GeoDataKind.LIMIT, ::createRectMapping)
            }

            private fun createRectMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
                val mapping = HashMap<Aes<*>, Variable>()
                mapping.putAll(
                    findMapping(
                        Aes.XMIN,
                        listOf(GeoPositionField.RECT_XMIN),
                        dataFrame
                    )
                )
                mapping.putAll(
                    findMapping(
                        Aes.XMAX,
                        listOf(GeoPositionField.RECT_XMAX),
                        dataFrame
                    )
                )
                mapping.putAll(
                    findMapping(
                        Aes.YMIN,
                        listOf(GeoPositionField.RECT_YMIN),
                        dataFrame
                    )
                )
                mapping.putAll(
                    findMapping(
                        Aes.YMAX,
                        listOf(GeoPositionField.RECT_YMAX),
                        dataFrame
                    )
                )
                return mapping
            }

            private fun createPointMapping(dataFrame: DataFrame): Map<Aes<*>, Variable> {
                val mapping = HashMap<Aes<*>, Variable>()
                mapping.putAll(
                    findMapping(
                        Aes.X,
                        listOf(
                            GeoPositionField.POINT_X,
                            "x",
                            GeoPositionField.POINT_X2
                        ),
                        dataFrame
                    )
                )
                mapping.putAll(
                    findMapping(
                        Aes.Y,
                        listOf(GeoPositionField.POINT_Y, "y"),
                        dataFrame
                    )
                )

                return mapping
            }
        }
    }
}
