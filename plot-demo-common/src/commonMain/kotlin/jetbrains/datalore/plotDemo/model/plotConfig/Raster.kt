/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plotDemo.model.SharedPieces.rasterData_Blue
import jetbrains.datalore.plotDemo.model.SharedPieces.rasterData_RGB

@Suppress("DuplicatedCode")
class Raster {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            rasterPlotSpecs(rasterData_Blue(), scaleFillIdentity = false),
            rasterPlotSpecs(rasterData_RGB(), scaleFillIdentity = true)
        )
    }

    private fun rasterPlotSpecs(data: Map<*, *>, scaleFillIdentity: Boolean): MutableMap<String, Any> {
        val spec = """
            |{"kind": "plot",
            | "layers": [
            |             {
            |                 "geom": "raster",
            |                 "mapping": {
            |                              "x": "x",
            |                              "y": "y",
            |                              "fill": "fill",
            |                              "alpha": "alpha"
            |                            }
            |             }
            |         ]
            |}
            """.trimMargin()
        val plotSpec = parsePlotSpec(spec)
        plotSpec["data"] = data
        if (scaleFillIdentity) {
            plotSpec["scales"] = listOf(
                mapOf(
                    "aesthetic" to "fill",
                    "scale_mapper_kind" to "identity"
                )
            )
        }
        return plotSpec
    }
}