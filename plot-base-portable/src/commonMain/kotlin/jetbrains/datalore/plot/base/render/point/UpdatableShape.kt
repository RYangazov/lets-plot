/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point

import org.jetbrains.letsPlot.commons.values.Color

interface UpdatableShape {
    fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double)
}
