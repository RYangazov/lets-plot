/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color

/**
 * Plotting area, drawn underneath plot.
 */
interface PanelTheme {
    fun showRect(): Boolean
    fun rectColor(): Color
    fun rectFill(): Color
    fun rectStrokeWidth(): Double

    fun showBorder(): Boolean
    fun borderColor(): Color
    fun borderWidth(): Double

    fun gridX(flipAxis: Boolean = false): PanelGridTheme
    fun gridY(flipAxis: Boolean = false): PanelGridTheme
}