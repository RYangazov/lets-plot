/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.annotation

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.annotations.AnnotationLineSpec
import org.jetbrains.letsPlot.core.plot.base.annotations.Annotations
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.builder.tooltip.LinesContentSpecification.Companion.LineSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.MappingField
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.ValueSource
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class AnnotationLine(
    pattern: String,
    fields: List<ValueSource>
) : LineSpec(label = null, pattern, fields), AnnotationLineSpec {

    constructor(other: LineSpec) : this(other.pattern, other.fields.map(ValueSource::copy))

    private val myLineFormatter = StringFormat.forNArgs(pattern, fields.size, "fields")

    fun initDataContext(data: DataFrame, mappedDataAccess: MappedDataAccess) {
        fields.forEach { it.initDataContext(data, mappedDataAccess) }
    }

    override fun getAnnotationText(index: Int): String? {
        val dataValues = fields.map { dataValue ->
            dataValue.getAnnotationText(index) ?: return null
        }
        return myLineFormatter.format(dataValues.map { it })
    }

    companion object {
        private val DEFAULT_STYLE = ThemeTextStyle(
            family = FontFamily.SERIF,
            face = FontFace.NORMAL,
            size = 10.0,
            color = Color.BLACK
        )

        fun createAnnotations(
            spec: AnnotationSpecification,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            themeTextStyle: ThemeTextStyle?
        ): Annotations? {
            if (spec.linePatterns.isEmpty()) {
                return null
            }
            val mappedLines = spec.linePatterns.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingField>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedLines.forEach { it.initDataContext(dataFrame, dataAccess) }
            return Annotations(
                mappedLines,
                textStyle = TextStyle(
                    themeTextStyle?.family?.name ?: DEFAULT_STYLE.family.name,
                    themeTextStyle?.face ?: DEFAULT_STYLE.face,
                    spec.textSize ?: themeTextStyle?.size ?: DEFAULT_STYLE.size,
                    themeTextStyle?.color ?: DEFAULT_STYLE.color
                )
            )
        }
    }
}