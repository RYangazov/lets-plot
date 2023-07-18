/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec

internal abstract class AxisLabelsLayout protected constructor(
    val orientation: Orientation,
    val axisDomain: DoubleSpan,
    val theme: AxisTheme
) {
    protected val labelSpec: LabelSpec = PlotLabelSpecFactory.axisTick(theme)

    protected val isHorizontal: Boolean
        get() = orientation.isHorizontal

    abstract fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
    ): AxisLabelsLayoutInfo

    internal fun mapToAxis(
        breaks: List<Double>,
        axisMapper: (Double?) -> Double?
    ): List<Double> {

        return BreakLabelsLayoutUtil.mapToAxis(
            breaks,
            axisDomain,
            axisMapper
        )
    }

    internal fun applyLabelMargins(bounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.applyLabelMargins(
            bounds,
            if (theme.showTickMarks()) theme.tickMarkLength() else 0.0,
            theme.tickLabelMargins(),
            orientation
        )
    }

    internal fun alignToLabelMargin(bounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.alignToLabelMargin(
            bounds,
            if (theme.showTickMarks()) theme.tickMarkLength() else 0.0,
            theme.tickLabelMargins(),
            orientation
        )
    }

    companion object {
        const val INITIAL_TICK_LABEL = "0000" // Typical tick label to estimate number of breaks (chosen by eye)
        const val MIN_TICK_LABEL_DISTANCE = 20.0  // px

        fun horizontalFlexBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return HorizontalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaksProvider,
                theme
            )
        }

        fun horizontalFixedBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            geomAreaInsets: Insets,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            return HorizontalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaks,
                geomAreaInsets,
                theme
            )
        }

        fun verticalFlexBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(!orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return VerticalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaksProvider,
                theme
            )
        }

        fun verticalFixedBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(!orientation.isHorizontal) { orientation.toString() }
            return VerticalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaks,
                theme
            )
        }
    }
}
