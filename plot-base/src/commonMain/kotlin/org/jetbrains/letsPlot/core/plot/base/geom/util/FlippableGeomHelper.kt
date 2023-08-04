/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class FlippableGeomHelper(
    private val aesthetics: Aesthetics,
    private val pos: PositionAdjustment,
    private val coord: CoordinateSystem,
    private val ctx: GeomContext,
    internal val isVertical: Boolean
) {

    fun getFlippedAes(aes: Aes<Double>): Aes<Double> {
        return getFlippedAes(aes, isVertical)
    }
    fun buildHints(
        hintAesList: List<Aes<Double>>,
        clientRectFactory: (DataPointAesthetics) -> DoubleRectangle?,
        fillColorMapper: (DataPointAesthetics) -> Color? = { null },
        colorMarkerMapper: (DataPointAesthetics) -> List<Color> = HintColorUtil.createColorMarkerMapper(null, ctx),
        defaultTooltipKind: TipLayoutHint.Kind? = null
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val isVerticallyOriented = when {
            isVertical -> !ctx.flipped
            else -> ctx.flipped
        }

        for (p in aesthetics.dataPoints()) {
            val clientRect = clientRectFactory(p) ?: continue

            val objectRadius = with(clientRect) {
                when {
                    isVerticallyOriented -> width / 2.0
                    else -> height / 2.0
                }
            }

            val hintFactory = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultCoord(p[getFlippedAes(Aes.X)]!!)
                .defaultKind(
                    if (isVerticallyOriented) {
                        TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                    } else {
                        TipLayoutHint.Kind.ROTATED_TOOLTIP
                    }
                )

            val hints = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }
                .hints

            ctx.targetCollector.addRectangle(
                p.index(),
                clientRect,
                GeomTargetCollector.TooltipParams(
                    tipLayoutHints = hints,
                    fillColor = fillColorMapper(p),
                    markerColors = colorMarkerMapper(p)
                ),
                tooltipKind = defaultTooltipKind ?: if (isVerticallyOriented) {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                }
            )
        }
    }

    companion object {
        fun getFlippedAes(aes: Aes<Double>, isVertical: Boolean): Aes<Double> {
            if (!isVertical) {
                when (aes) {
                    Aes.X -> return Aes.Y
                    Aes.Y -> return Aes.X
                    Aes.YMIN -> return Aes.XMIN
                    Aes.YMAX -> return Aes.XMAX
                    Aes.WIDTH -> return Aes.HEIGHT
                    else -> error("Could not find aesthetic ${aes.name}")
                }
            } else {
                return aes
            }
        }

        fun DoubleVector.flip(isVertical: Boolean): DoubleVector {
            return when {
                isVertical -> this
                else -> this.flip()
            }
        }

        fun DoubleRectangle.flip(isVertical: Boolean): DoubleRectangle {
            return when {
                isVertical -> this
                else -> this.flip()
            }
        }

        fun DoubleSegment.flip(isVertical: Boolean): DoubleSegment {
            return when {
                isVertical -> this
                else -> DoubleSegment(this.start.flip(), this.end.flip())
            }
        }
    }
}