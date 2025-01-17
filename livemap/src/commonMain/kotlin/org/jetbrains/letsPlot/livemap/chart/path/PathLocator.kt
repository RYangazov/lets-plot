/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.chart.HoverObject
import org.jetbrains.letsPlot.livemap.chart.IndexComponent
import org.jetbrains.letsPlot.livemap.chart.Locator
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import kotlin.math.pow

object PathLocator : Locator {
    override fun search(coord: Vec<org.jetbrains.letsPlot.livemap.Client>, target: EcsEntity, renderHelper: RenderHelper): HoverObject? {
        if (!target.contains(WorldGeometryComponent::class)) {
            return null
        }

        if (isCoordinateInPath(
                renderHelper.posToWorld(coord),
                renderHelper.dimToWorld(target.get<ChartElementComponent>().strokeWidth / 2),
                target.get<WorldGeometryComponent>().geometry.multiLineString
            )
        ) {
            return HoverObject(
                layerIndex = target.get<IndexComponent>().layerIndex,
                index = target.get<IndexComponent>().index,
                distance = 0.0,
                this
            )
        }
        return null
    }

    // Special logic is not yet determined.
    override fun reduce(hoverObjects: Collection<HoverObject>): HoverObject? = hoverObjects.firstOrNull()

    private fun isCoordinateInPath(
        coord: Vec<org.jetbrains.letsPlot.livemap.World>,
        strokeRadius: Scalar<org.jetbrains.letsPlot.livemap.World>,
        multiLineString: MultiLineString<org.jetbrains.letsPlot.livemap.World>
    ): Boolean {
        for (lineString in multiLineString) {
            val bbox = lineString.bbox ?: continue

            if (!bbox.inflate(strokeRadius).contains(coord)) {
                continue
            }
            if (pathContainsCoordinate(coord, lineString, strokeRadius.value)) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> pathContainsCoordinate(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        strokeWidth: Double
    ): Boolean {
        for (i in 0 until path.size - 1) {
            if (calculateSquareDistanceToPathSegment(coord, path, i) <= strokeWidth.pow(2.0)) {
                return true
            }
        }
        return false
    }

    private fun <TypeT> calculateSquareDistanceToPathSegment(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        segmentNum: Int
    ): Double {
        val next = segmentNum + 1
        val dx: Double = path[next].x - path[segmentNum].x
        val dy: Double = path[next].y - path[segmentNum].y
        val scalar: Double = dx * (coord.x - path[segmentNum].x) + dy * (coord.y - path[segmentNum].y)
        if (scalar <= 0) {
            return calculateSquareDistanceToPathPoint(coord, path, segmentNum)
        }
        val segmentSquareLength = dx * dx + dy * dy
        val baseSquareLength = scalar * scalar / segmentSquareLength
        return if (baseSquareLength >= segmentSquareLength) {
            calculateSquareDistanceToPathPoint(coord, path, next)
        } else calculateSquareDistanceToPathPoint(coord, path, segmentNum) - baseSquareLength
    }
    private fun <TypeT> calculateSquareDistanceToPathPoint(
        coord: Vec<TypeT>,
        path: List<Vec<TypeT>>,
        pointNum: Int
    ): Double {
        val dx: Double = coord.x - path[pointNum].x
        val dy: Double = coord.y - path[pointNum].y
        return dx * dx + dy * dy
    }
}