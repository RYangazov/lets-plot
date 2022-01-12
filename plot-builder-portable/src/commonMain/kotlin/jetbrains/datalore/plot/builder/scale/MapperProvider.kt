/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform

interface MapperProvider<T> {
    /**
     * Create mapper with discrete input (domain)
     */
    fun createDiscreteMapper(discreteTransform: DiscreteTransform): GuideMapper<T>

    /**
     * Create mapper with continuous (numeric) input (domain)
     */
    fun createContinuousMapper2(
        domain: ClosedRange<Double>,
        trans: ContinuousTransform
    ): GuideMapper<T>
}
