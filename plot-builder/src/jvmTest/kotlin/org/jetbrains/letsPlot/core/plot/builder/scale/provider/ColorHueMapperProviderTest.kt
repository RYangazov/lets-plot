/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorHueMapperProviderTest {
    companion object {
        // all colors are in HSL model with S=100, L=50
        private val HSL_COLOR_0 = Color(255, 0, 0)
        private val HSL_COLOR_60 = Color(255, 255, 0)
        private val HSL_COLOR_90 = Color(128, 255, 0)
        private val HSL_COLOR_120 = Color(0, 255, 0)
        private val HSL_COLOR_135 = Color(0, 255, 64)
        private val HSL_COLOR_180 = Color(0, 255, 255)
        private val HSL_COLOR_240 = Color(0, 0, 255)
        private val HSL_COLOR_300 = Color(255, 0, 255)
        private val HSL_COLOR_360 = Color(255, 0, 0)
    }

    private fun createContinuousMapper(
        rangeLength: Double,
        hueRange: DoubleSpan,
        chroma: Double = 100.0,
        luminance: Double = 50.0,
        startHue: Double = 0.0,
        reversed: Boolean = false,
        naValue: Color = Color.GRAY
    ): GuideMapper<Color> {
        return createMapperProvider(
            hueRange = hueRange,
            chroma = chroma,
            luminance = luminance,
            startHue = startHue,
            reversed = reversed,
            naValue = naValue
        ).createContinuousMapper(DoubleSpan(0.0, rangeLength), Transforms.IDENTITY)
    }

    private fun createDiscreteMapper(
        n: Int,
        hueRange: DoubleSpan,
        chroma: Double = 100.0,
        luminance: Double = 50.0,
        startHue: Double = 0.0,
        reversed: Boolean = false,
        naValue: Color = Color.GRAY
    ): ScaleMapper<Color> {
        val discreteTransform = DiscreteTransform((0 until n).map(Int::toString), emptyList())
        return ColorHueMapperProvider(hueRange, chroma, luminance, startHue, reversed, naValue)
            .createDiscreteMapper(discreteTransform)
    }

    private fun createMapperProvider(
        hueRange: DoubleSpan,
        chroma: Double = 100.0,
        luminance: Double = 50.0,
        startHue: Double = 0.0,
        reversed: Boolean = false,
        naValue: Color = Color.GRAY
    ): ColorHueMapperProvider {
        return ColorHueMapperProvider(
            hueRange = hueRange,
            chroma = chroma,
            luminance = luminance,
            startHue = startHue,
            reversed = reversed,
            naValue = naValue
        )
    }

    @Test
    fun `regression for issue206 with HSL model`() {
        val mapper = createContinuousMapper(rangeLength = 1.0, hueRange = DoubleSpan(90.0, 180.0))

        assertEquals(HSL_COLOR_90, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_135, mapper.invoke(0.5))
        assertEquals(HSL_COLOR_180, mapper.invoke(1.0))
    }

    @Test
    fun `discrete - full hue wheel`() {
        val mapper = createDiscreteMapper(n = 6, hueRange = DoubleSpan(0.0, 360.0))

        assertEquals(HSL_COLOR_0, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_60, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_120, mapper.invoke(2.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(3.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(4.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(5.0))
    }

    @Test
    fun `discrete - hue_range over 360 deg`() {
        val mapper = createDiscreteMapper(n = 6, hueRange = DoubleSpan(120.0, 480.0))

        assertEquals(HSL_COLOR_120, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(2.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(3.0))
        assertEquals(HSL_COLOR_0, mapper.invoke(4.0))
        assertEquals(HSL_COLOR_60, mapper.invoke(5.0))
    }

    @Test
    fun `discrete - h_start works as offset`() {
        val mapper = createDiscreteMapper(n = 3, hueRange = DoubleSpan(120.0, 240.0), startHue = 60.0)

        assertEquals(HSL_COLOR_180, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(2.0))
    }

    @Test
    fun `discrete - reversed hue_range over 360 deg`() {
        val mapper = createDiscreteMapper(n = 6, hueRange = DoubleSpan(120.0, 480.0), reversed = true)

        assertEquals(HSL_COLOR_60, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_0, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(2.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(3.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(4.0))
        assertEquals(HSL_COLOR_120, mapper.invoke(5.0))
    }

    @Test
    fun `continuous - full hue wheel mapper`() {
        val mapper = createContinuousMapper(rangeLength = 360.0, hueRange = DoubleSpan(0.0, 360.0))

        assertEquals(HSL_COLOR_0, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_60, mapper.invoke(60.0))
        assertEquals(HSL_COLOR_120, mapper.invoke(120.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(180.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(240.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(300.0))
        assertEquals(HSL_COLOR_360, mapper.invoke(360.0))
    }

    @Test
    fun `continuous - h_start works as offset`() {
        val mapper = createContinuousMapper(rangeLength = 2.0, hueRange = DoubleSpan(120.0, 240.0), startHue = 60.0)

        assertEquals(HSL_COLOR_180, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(2.0))
    }

    @Test
    fun `continuous - hue_range over 360 deg`() {


        val mapper = createContinuousMapper(rangeLength = 6.0, hueRange = DoubleSpan(120.0, 480.0))

        assertEquals(HSL_COLOR_120, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(2.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(3.0))
        assertEquals(HSL_COLOR_0, mapper.invoke(4.0))
        assertEquals(HSL_COLOR_60, mapper.invoke(5.0))
        assertEquals(HSL_COLOR_120, mapper.invoke(6.0))
    }

    @Test
    fun `continuous - reversed hue_range over 360 deg`() {
        val mapper = createContinuousMapper(rangeLength = 6.0, hueRange = DoubleSpan(120.0, 480.0), reversed = true)

        assertEquals(HSL_COLOR_120, mapper.invoke(0.0))
        assertEquals(HSL_COLOR_60, mapper.invoke(1.0))
        assertEquals(HSL_COLOR_0, mapper.invoke(2.0))
        assertEquals(HSL_COLOR_300, mapper.invoke(3.0))
        assertEquals(HSL_COLOR_240, mapper.invoke(4.0))
        assertEquals(HSL_COLOR_180, mapper.invoke(5.0))
        assertEquals(HSL_COLOR_120, mapper.invoke(6.0))
    }
}
