/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil.orthogonal
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.max

class LineRangeGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(
                flipHelper.getEffectiveAes(Aes.X.orthogonal()),
                flipHelper.getEffectiveAes(Aes.YMIN.orthogonal()),
                flipHelper.getEffectiveAes(Aes.YMAX.orthogonal())
            )
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val xAes = flipHelper.getEffectiveAes(Aes.X)
        val minAes = flipHelper.getEffectiveAes(Aes.YMIN)
        val maxAes = flipHelper.getEffectiveAes(Aes.YMAX)
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, minAes, maxAes)

        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LINE_RANGE, ctx)
        for (p in dataPoints) {
            val x = p[xAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!
            // line
            val start = flipHelper.flip(DoubleVector(x, ymin))
            val end = flipHelper.flip(DoubleVector(x, ymax))
            helper.createLine(start, end, p)?.let { root.add(it) }
        }
        // tooltip
        flipHelper.buildHints(
            listOf(minAes, maxAes),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, flipHelper),
            { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
    }

    companion object {
        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            flipHelper: FlippableGeomHelper
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val xAes = flipHelper.getEffectiveAes(Aes.X)
                val minAes = flipHelper.getEffectiveAes(Aes.YMIN)
                val maxAes = flipHelper.getEffectiveAes(Aes.YMAX)
                if (p.defined(xAes) &&
                    p.defined(minAes) &&
                    p.defined(maxAes)
                ) {
                    val x = p[xAes]!!
                    val ymin = p[minAes]!!
                    val ymax = p[maxAes]!!
                    val height = ymax - ymin

                    val rect = geomHelper.toClient(
                        flipHelper.flip(
                            DoubleRectangle(DoubleVector(x, ymax - height / 2.0), DoubleVector.ZERO)
                        ),
                        p
                    )!!
                    val width = max(AesScaling.strokeWidth(p), MIN_TOOLTIP_RECTANGLE_WIDTH)
                    val isNeedToFlip = when {
                        flipHelper.isVertical -> ctx.flipped
                        else -> !ctx.flipped
                    }
                    GeomUtil.extendWidth(rect, width, isNeedToFlip)
                } else {
                    null
                }
            }
        }

        const val HANDLES_GROUPS = false
        const val MIN_TOOLTIP_RECTANGLE_WIDTH = 2.0
    }
}
