/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.subPlots

import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.builder.FigureSvgRoot

class CompositeFigureSvgComponent(
    val elements: List<FigureSvgRoot>,
) : SvgComponent() {

    override fun buildComponent() {
        // ToDo: add title, subtitle, caption
    }

    override fun clear() {
        super.clear()
    }
}