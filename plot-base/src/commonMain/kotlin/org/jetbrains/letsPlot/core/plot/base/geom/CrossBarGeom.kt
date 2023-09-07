/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil.orthogonal
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class CrossBarGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)
    var fattenMidline: Double = 2.5

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(
                flipHelper.getEffectiveAes(Aes.YMIN.orthogonal()),
                flipHelper.getEffectiveAes(Aes.YMAX.orthogonal()),
            )
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        BoxHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            rectFactory = clientRectByDataPoint(ctx, geomHelper, flipHelper, isHintRect = false)
        )
        buildMidlines(root, aesthetics, ctx, geomHelper, flipHelper, fatten = fattenMidline)
        // tooltip
        flipHelper.buildHints(
            listOf(flipHelper.getEffectiveAes(Aes.YMIN), flipHelper.getEffectiveAes(Aes.YMAX)),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, flipHelper, isHintRect = true),
            { HintColorUtil.colorWithAlpha(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = BoxHelper.legendFactory(false)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            flipHelper: FlippableGeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val xAes = flipHelper.getEffectiveAes(Aes.X)
                val yAes = flipHelper.getEffectiveAes(Aes.Y)
                val minAes = flipHelper.getEffectiveAes(Aes.YMIN)
                val maxAes = flipHelper.getEffectiveAes(Aes.YMAX)
                val widthAes = Aes.WIDTH
                 val rect = if (!isHintRect &&
                    p.defined(xAes) &&
                    p.defined(minAes) &&
                    p.defined(maxAes) &&
                    p.defined(widthAes)
                ) {
                    val x = p[xAes]!!
                    val ymin = p[minAes]!!
                    val ymax = p[maxAes]!!
                    val width = p[widthAes]!! * ctx.getResolution(xAes)
                    val origin = DoubleVector(x - width / 2, ymin)
                    val dimensions = DoubleVector(width, ymax - ymin)
                    DoubleRectangle(origin, dimensions)
                } else if (isHintRect &&
                    p.defined(xAes) &&
                    p.defined(yAes) &&
                    p.defined(widthAes)
                ) {
                    val x = p[xAes]!!
                    val y = p[yAes]!!
                    val width = p[widthAes]!! * ctx.getResolution(xAes)
                    val origin = DoubleVector(x - width / 2, y)
                    val dimensions = DoubleVector(width, 0.0)
                    DoubleRectangle(origin, dimensions)
                } else {
                    null
                }
                rect?.let { geomHelper.toClient(flipHelper.flip(it), p) }
            }
        }

        fun buildMidlines(
            root: SvgRoot,
            aesthetics: Aesthetics,
            ctx: GeomContext,
            geomHelper: GeomHelper,
            flipHelper: FlippableGeomHelper,
            fatten: Double
        ) {
            val elementHelper = geomHelper.createSvgElementHelper()
            val xAes = flipHelper.getEffectiveAes(Aes.X)
            val yAes = flipHelper.getEffectiveAes(Aes.Y)
            for (p in GeomUtil.withDefined(
                aesthetics.dataPoints(),
                xAes,
                yAes,
                Aes.WIDTH
            )) {
                val x = p[xAes]!!
                val middle = p[yAes]!!
                val width = p[Aes.WIDTH]!! * ctx.getResolution(xAes)

                val line = elementHelper.createLine(
                    flipHelper.flip(DoubleVector(x - width / 2, middle)),
                    flipHelper.flip(DoubleVector(x + width / 2, middle)),
                    p
                )!!

                // adjust thickness
                val thickness = line.strokeWidth().get()!!
                line.strokeWidth().set(thickness * fatten)

                root.add(line)
            }
        }
    }
}
