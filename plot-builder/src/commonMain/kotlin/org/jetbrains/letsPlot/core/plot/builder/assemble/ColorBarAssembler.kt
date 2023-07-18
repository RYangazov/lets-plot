/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.FeatureSwitch
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.ScaleBreaksUtil
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.ColorBarComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.ColorBarComponentLayout
import org.jetbrains.letsPlot.core.plot.builder.guide.ColorBarComponentSpec
import org.jetbrains.letsPlot.core.plot.builder.guide.ColorBarComponentSpec.Companion.DEF_NUM_BIN
import org.jetbrains.letsPlot.core.plot.builder.guide.LegendBox
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil
import kotlin.math.max

class ColorBarAssembler(
    private val legendTitle: String,
    private val transformedDomain: DoubleSpan,
    private val scale: Scale,
    private val scaleMapper: ScaleMapper<Color>,
    private val theme: LegendTheme
) {

    private var colorBarOptions: ColorBarOptions? = null

    fun createColorBar(): LegendBoxInfo {
        var scale = scale
        if (!scale.hasBreaks()) {
            scale = ScaleBreaksUtil.withBreaks(scale, transformedDomain, 5)
        }

        val scaleBreaks = scale.getScaleBreaks()
        if (scaleBreaks.isEmpty) {
            return LegendBoxInfo.EMPTY
        }

        val spec = createColorBarSpec(
            legendTitle,
            transformedDomain,
            scaleBreaks,
            scaleMapper,
            theme,
            colorBarOptions
        )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = ColorBarComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }

    internal fun setOptions(options: ColorBarOptions?) {
        colorBarOptions = options
    }

    companion object {
        private const val DEBUG_DRAWING = FeatureSwitch.LEGEND_DEBUG_DRAWING

        fun createColorBarSpec(
            title: String,
            transformedDomain: DoubleSpan,
            breaks: ScaleBreaks,
            scaleMapper: ScaleMapper<Color>,
            theme: LegendTheme,
            options: ColorBarOptions? = null
        ): ColorBarComponentSpec {

            val legendDirection = LegendAssemblerUtil.legendDirection(theme)
            val horizontal: Boolean = legendDirection == LegendDirection.HORIZONTAL

            val width = options?.width
            val height = options?.height
            var barSize = ColorBarComponentSpec.barAbsoluteSize(horizontal, theme)
            if (width != null) {
                barSize = DoubleVector(width, barSize.y)
            } else if (horizontal) {
                val labelMaxWidth = breaks.labels.maxOf { label ->
                    PlotLayoutUtil.textDimensions(label, PlotLabelSpecFactory.legendItem(theme)).x
                }
                barSize = DoubleVector(max(barSize.x, labelMaxWidth * (breaks.size + 1)), barSize.y)
            }
            if (height != null) {
                barSize = DoubleVector(barSize.x, height)
            } else if (!horizontal) {
                val labelMaxHeight = breaks.labels.maxOf { label ->
                    PlotLayoutUtil.textDimensions(label, PlotLabelSpecFactory.legendItem(theme)).y
                }
                barSize = DoubleVector(barSize.x, max(barSize.y, labelMaxHeight * (breaks.size + 1)))
            }

            val reverse = !horizontal

            val layout = when {
                horizontal -> ColorBarComponentLayout.horizontal(
                    title,
                    transformedDomain,
                    breaks,
                    barSize,
                    reverse,
                    theme
                )

                else -> ColorBarComponentLayout.vertical(title, transformedDomain, breaks, barSize, reverse, theme)
            }

            return ColorBarComponentSpec(
                title,
                transformedDomain,
                breaks,
                scaleMapper,
                binCount = options?.binCount ?: DEF_NUM_BIN,
                theme,
                layout,
                reverse
            )
        }
    }
}
