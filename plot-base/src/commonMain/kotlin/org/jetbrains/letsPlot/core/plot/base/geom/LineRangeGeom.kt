/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extend
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class LineRangeGeom(private val isVertical: Boolean = true) : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)

        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LINE_RANGE, ctx)
        val xAes = if (isVertical) Aes.X else Aes.Y
        val minAes = if (isVertical) Aes.YMIN else Aes.XMIN
        val maxAes = if (isVertical) Aes.YMAX else Aes.XMAX

        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, minAes, maxAes)

        for (p in dataPoints) {
            val x = p[xAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!
            // line
            val start = DoubleVector(x, ymin).let { if (isVertical) it else it.flip() }
            val end = DoubleVector(x, ymax).let { if (isVertical) it else it.flip() }
            val line = helper.createLine(start, end, p)
            if (line != null) {
                root.add(line)
            }
            // tooltip
            val hintRect = DoubleRectangle(DoubleVector(x, ymin), DoubleVector.ZERO).let {
                when (isVertical) {
                    true -> it
                    false -> it.flip()
                }
            }
            buildHints(
                hintRect,
                p,
                ctx,
                geomHelper,
                colorsByDataPoint,
                isVerticalGeom = isVertical
            )
        }
    }
    private fun buildHints(
        rect: DoubleRectangle?,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>,
        isVerticalGeom: Boolean
    ) {
        val isVerticallyOriented = if (isVerticalGeom) !ctx.flipped else ctx.flipped

        val clientRect = geomHelper.toClient(rect!!, p)
        val objectRadius = clientRect?.run {
            if (isVerticallyOriented) {
                width / 2.0
            } else {
                height / 2.0
            }
        }!!
        val widthExpand = AesScaling.strokeWidth(p)
        val targetRect = extend(clientRect, isVerticalGeom, widthExpand, widthExpand)

        val aes = if (isVerticalGeom) Aes.X else Aes.Y
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p[aes]!!)
            .defaultKind(
                if (isVerticallyOriented) {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                }
            )

        val minAes = if (isVerticalGeom) Aes.YMIN else Aes.XMIN
        val maxAes = if (isVerticalGeom) Aes.YMAX else Aes.XMAX
        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(minAes))
            .addHint(hint.create(maxAes))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            targetRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p),
                fillColor = HintColorUtil.colorWithAlpha(p)
            ),
            tooltipKind = if (isVerticallyOriented) {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            }
        )
    }
    companion object {
        const val HANDLES_GROUPS = false
    }
}
