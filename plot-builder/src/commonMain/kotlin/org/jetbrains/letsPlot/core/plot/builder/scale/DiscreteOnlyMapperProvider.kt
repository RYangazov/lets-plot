/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform

abstract class DiscreteOnlyMapperProvider<T> : MapperProvider<T> {
    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<T> {
        throw IllegalStateException("[${this::class.simpleName}] Can't create mapper for continuous domain $domain")
    }
}
