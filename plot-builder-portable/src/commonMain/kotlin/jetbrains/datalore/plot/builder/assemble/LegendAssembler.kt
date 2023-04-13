/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.assemble.LegendAssemblerUtil.mapToAesthetics
import jetbrains.datalore.plot.builder.guide.*
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme.LegendTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class LegendAssembler(
    private val legendTitle: String,
    private val guideOptionsMap: Map<Aes<*>, GuideOptions>,
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val theme: LegendTheme
) {

    private val legendLayers = ArrayList<LegendLayer>()

    fun addLayer(
        keyFactory: LegendKeyElementFactory,
        aesList: List<Aes<*>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
//        scaleByAes: Map<Aes<*>, Scale>,
//        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
        ctx: PlotContext,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>
    ) {

        legendLayers.add(
            LegendLayer(
                keyFactory,
                aesList,
                constantByAes,
                aestheticsDefaults,
//                scaleByAes,
                scaleMappers,
//                transformedDomainByAes
                ctx,
                colorByAes,
                fillByAes
            )
        )
    }

    fun createLegend(): LegendBoxInfo {
        val legendBreaksByLabel = LinkedHashMap<String, LegendBreak>()
        for (legendLayer in legendLayers) {
            val keyElementFactory = legendLayer.keyElementFactory
            val dataPoints = legendLayer.keyAesthetics.dataPoints().iterator()
            for (label in legendLayer.keyLabels) {
                legendBreaksByLabel.getOrPut(label) { LegendBreak(wrap(label)) }
                    .addLayer(dataPoints.next(), keyElementFactory)
            }
        }

        val legendBreaks = ArrayList<LegendBreak>()
        for (legendBreak in legendBreaksByLabel.values) {
            if (legendBreak.isEmpty) {
                continue
            }
            legendBreaks.add(legendBreak)
        }


        if (legendBreaks.isEmpty()) {
            return LegendBoxInfo.EMPTY
        }

        // legend options
        val legendOptionsList = ArrayList<LegendOptions>()
        for (legendLayer in legendLayers) {
            val aesList = legendLayer.aesList
            for (aes in aesList) {
                if (guideOptionsMap[aes] is LegendOptions) {
                    legendOptionsList.add(guideOptionsMap[aes] as LegendOptions)
                }
            }
        }

        val spec =
            createLegendSpec(
                legendTitle, legendBreaks, theme,
                LegendOptions.combine(
                    legendOptionsList
                )
            )

        return object : LegendBoxInfo(spec.size) {
            override fun createLegendBox(): LegendBox {
                val c = LegendComponent(spec)
                c.debug = DEBUG_DRAWING
                return c
            }
        }
    }


    private class LegendLayer(
        internal val keyElementFactory: LegendKeyElementFactory,
        internal val aesList: List<Aes<*>>,
        constantByAes: Map<Aes<*>, Any>,
        aestheticsDefaults: AestheticsDefaults,
//        scaleMap: Map<Aes<*>, Scale>,
        scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
//        transformedDomainByAes: Map<Aes<*>, DoubleSpan>
        ctx: PlotContext,
        colorByAes: Aes<Color>,
        fillByAes: Aes<Color>
    ) {

        internal val keyAesthetics: Aesthetics
        internal val keyLabels: List<String>

        init {
            val aesValuesByLabel = LinkedHashMap<String, MutableMap<Aes<*>, Any>>()
            for (aes in aesList) {
//                var scale = scaleMap[aes]
                var scale = ctx.getScale(aes)
                if (!scale.hasBreaks()) {
//                    scale = ScaleBreaksUtil.withBreaks(scale, transformedDomainByAes.getValue(aes), 5)
                    scale = ScaleBreaksUtil.withBreaks(scale, ctx.overallTransformedDomain(aes), 5)
                }
                check(scale.hasBreaks()) { "No breaks were defined for scale $aes" }

                val scaleBreaks = scale.getScaleBreaks()
                val aesValues = scaleBreaks.transformedValues.map {
                    scaleMappers.getValue(aes)(it) as Any // Don't expect nulls.
                }
                val labels = scaleBreaks.labels
                for ((label, aesValue) in labels.zip(aesValues)) {
                    aesValuesByLabel.getOrPut(label) { HashMap() }[aes] = aesValue
                }
            }

            // build 'key' aesthetics
            keyAesthetics = mapToAesthetics(
                aesValuesByLabel.values,
                constantByAes,
                aestheticsDefaults,
                colorByAes,
                fillByAes
            )
            keyLabels = ArrayList(aesValuesByLabel.keys)
        }
    }

    companion object {
        private const val DEBUG_DRAWING = jetbrains.datalore.plot.FeatureSwitch.LEGEND_DEBUG_DRAWING
        fun wrap(text: String): String {
            if (text.contains("\n") || text.length <= Defaults.Common.Legend.STRING_MAX_LENGTH) return text
            var i: Int = 0
            var substringIndex: Int = 0
            var tempIndex: Int = 0
            var tempString: String = String()
            var resultString: String = String()
            var stringSplitter: String = ""
            while (i < Defaults.Common.Legend.STRINGS_MAX_COUNT && substringIndex < text.length - 1) {
                //Let's take a part of the string for analysis on the presence of spaces
                tempString = text.substring(
                    substringIndex,
                    min(text.length, substringIndex + Defaults.Common.Legend.STRING_MAX_LENGTH)
                )
                tempIndex = if (tempString.lastIndexOf(" ") < 0) min(
                    Defaults.Common.Legend.STRING_MAX_LENGTH,
                    tempString.length
                ) else tempString.lastIndexOf(" ") + 1
                resultString += stringSplitter + tempString.substring(0, tempIndex)
                stringSplitter = "\n"
                substringIndex += tempIndex
                i++
                if (i == Defaults.Common.Legend.STRINGS_MAX_COUNT
                    && tempString.length >= Defaults.Common.Legend.STRING_MAX_LENGTH
                )
                    resultString = resultString.dropLast(2) + ".."
            }
            return resultString
        }

        fun createLegendSpec(
            title: String,
            breaks: List<LegendBreak>,
            theme: LegendTheme,
            options: LegendOptions = LegendOptions()
        ): LegendComponentSpec {

            val legendDirection = LegendAssemblerUtil.legendDirection(theme)

            // key size
            fun pretty(v: DoubleVector): DoubleVector {
                val margin = 1.0
                return DoubleVector(
                    floor(v.x / 2) * 2 + 1.0 + margin,
                    floor(v.y / 2) * 2 + 1.0 + margin
                )
            }

            val themeKeySize = DoubleVector(theme.keySize(), theme.keySize())
            val keySizes = breaks
                .map { br -> themeKeySize.max(pretty(br.minimumKeySize)) }
                .let { sizes ->
                    // Use max height for horizontal and max width for vertical legend for better (central) alignment
                    if (legendDirection == LegendDirection.HORIZONTAL) {
                        val maxKeyHeight = sizes.maxOf(DoubleVector::y)
                        sizes.map { DoubleVector(it.x, maxKeyHeight) }
                    } else {
                        val maxKeyWidth = sizes.maxOf(DoubleVector::x)
                        sizes.map { DoubleVector(maxKeyWidth, it.y) }
                    }
                }

            // row, col count
            val breakCount = breaks.size
            val colCount: Int
            val rowCount: Int
            if (options.isByRow) {
                colCount = when {
                    options.hasColCount() -> min(options.colCount, breakCount)
                    options.hasRowCount() -> ceil(breakCount / options.rowCount.toDouble()).toInt()
                    legendDirection === LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                rowCount = ceil(breakCount / colCount.toDouble()).toInt()
            } else {
                // by column
                rowCount = when {
                    options.hasRowCount() -> min(options.rowCount, breakCount)
                    options.hasColCount() -> ceil(breakCount / options.colCount.toDouble()).toInt()
                    legendDirection !== LegendDirection.HORIZONTAL -> breakCount
                    else -> 1
                }
                colCount = ceil(breakCount / rowCount.toDouble()).toInt()
            }

            val layout: LegendComponentLayout
            @Suppress("LiftReturnOrAssignment")
            if (legendDirection === LegendDirection.HORIZONTAL) {
                if (options.hasRowCount() || options.hasColCount() && options.colCount < breakCount) {
                    layout = LegendComponentLayout.horizontalMultiRow(
                        title,
                        breaks,
                        keySizes,
                        theme
                    )
                } else {
                    layout = LegendComponentLayout.horizontal(title, breaks, keySizes, theme)
                }
            } else {
                layout = LegendComponentLayout.vertical(title, breaks, keySizes, theme)
            }

            layout.colCount = colCount
            layout.rowCount = rowCount
            layout.isFillByRow = options.isByRow

            return LegendComponentSpec(
                title,
                breaks,
                theme,
                layout,
                reverse = false
            )
        }
    }
}
