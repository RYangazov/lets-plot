/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.ContinuousTransform

abstract class DiscreteOnlyMapperProvider<T> : MapperProvider<T> {
    override fun createContinuousMapper(domain: ClosedRange<Double>, trans: ContinuousTransform): GuideMapper<T> {
        throw IllegalStateException("[${this::class.simpleName}] Can't create mapper for continuous domain $domain")
    }
}
