/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.WithGuideBreaks

internal class GuideMapperWithGuideBreaks<DomainT, TargetT>(
    mapper: ScaleMapper<TargetT>,
    override val breaks: List<DomainT>,
    override val formatter: (DomainT) -> String
) : WithGuideBreaks<DomainT>, GuideMapper<TargetT>(
    mapper,
    isContinuous = false
)
