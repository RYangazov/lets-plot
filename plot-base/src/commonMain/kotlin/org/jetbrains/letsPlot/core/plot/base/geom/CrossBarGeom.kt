/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class CrossBarGeom : GeomBase() {
    var fattenMidline: Double = 2.5

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

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
            rectFactory = clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        BoxHelper.buildMidlines(
            root = root,
            aesthetics = aesthetics,
            middleAesthetic = Aes.Y,
            ctx = ctx,
            geomHelper = geomHelper,
            fatten = fattenMidline
        )
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            { HintColorUtil.colorWithAlpha(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = BoxHelper.legendFactory(false)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val rect = if (!isHintRect &&
                    p.defined(Aes.X) &&
                    p.defined(Aes.YMIN) &&
                    p.defined(Aes.YMAX) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val ymin = p.ymin()!!
                    val ymax = p.ymax()!!
                    val width = p.width()!! * ctx.getResolution(Aes.X)

                    val origin = DoubleVector(x - width / 2, ymin)
                    val dimensions = DoubleVector(width, ymax - ymin)
                    DoubleRectangle(origin, dimensions)
                } else if (isHintRect &&
                    p.defined(Aes.X) &&
                    p.defined(Aes.Y) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val y = p.y()!!
                    val width = p.width()!! * ctx.getResolution(Aes.X)

                    val origin = DoubleVector(x - width / 2, y)
                    val dimensions = DoubleVector(width, 0.0)
                    DoubleRectangle(origin, dimensions)
                } else {
                    null
                }

                rect?.let { geomHelper.toClient(it, p) }
            }
        }
    }
}
