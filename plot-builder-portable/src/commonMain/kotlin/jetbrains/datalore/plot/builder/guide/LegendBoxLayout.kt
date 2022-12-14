/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.textDimensions
import jetbrains.datalore.plot.builder.theme.LegendTheme

abstract class LegendBoxLayout(
    private val title: String,
    legendDirection: LegendDirection,
    protected val theme: LegendTheme
) {
    // legend keys/colorbar + labels.
    abstract val graphSize: DoubleVector

    val isHorizontal = legendDirection === LegendDirection.HORIZONTAL

    private val titleBounds: DoubleRectangle
        get() {
            return DoubleRectangle(DoubleVector.ZERO, titleSize)
        }

    val graphOrigin: DoubleVector
        get() = when {
            isHorizontal -> with(titleSize) {
                DoubleVector(
                    x,
                    if (y > graphSize.y) (y - graphSize.y) / 2 else 0.0
                )
            }
            else -> DoubleVector(
                0.0,
                // make some space between title and the rest of the content.
                titleSize.y + PlotLabelSpecFactory.legendTitle(theme).height() / 2
            )
        }

    val size: DoubleVector
        get() {
            val graphBounds = DoubleRectangle(graphOrigin, graphSize)
            val titleAndContent = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                .union(titleBounds)
                .union(graphBounds)
            return titleAndContent.dimension
        }

    internal val titleSize: DoubleVector
        get() {
            return when {
                title.isBlank() || !theme.showTitle() -> DoubleVector.ZERO
                else -> textDimensions(title, PlotLabelSpecFactory.legendTitle(theme))
            }
        }
}
