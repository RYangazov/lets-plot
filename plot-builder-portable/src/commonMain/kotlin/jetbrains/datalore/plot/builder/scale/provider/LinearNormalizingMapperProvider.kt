/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers

open class LinearNormalizingMapperProvider(
    private val outputRange: ClosedRange<Double>,
    naValue: Double
) : MapperProviderBase<Double>(naValue) {

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Double> {
        return GuideMappers.discreteToContinuous(discreteTransform, outputRange, naValue)
    }

    override fun createContinuousMapper(domain: ClosedRange<Double>, trans: ContinuousTransform): GuideMapper<Double> {
        val dataRange = MapperUtil.rangeWithLimitsAfterTransform2(domain, trans)
        return GuideMappers.continuousToContinuous(dataRange, outputRange, naValue)
    }
}
