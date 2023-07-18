/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.core.plot.builder.PlotContainer
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgRoot
import jetbrains.datalore.vis.demoUtils.swing.PlotObjectsDemoWindowBase
import org.jetbrains.letsPlot.platf.batik.plot.util.BatikMapperComponent
import java.awt.Dimension
import javax.swing.JComponent

class PlotObjectsDemoWindowBatik(
    title: String,
    plotList: List<PlotSvgComponent>,
    maxCol: Int = 2,
    plotSize: Dimension = Dimension(500, 350)
) : PlotObjectsDemoWindowBase(
    title,
    plotList = plotList,
    maxCol = maxCol,
    plotSize = plotSize
) {

    override fun createPlotComponent(plot: PlotSvgComponent, plotSize: Dimension): JComponent {
        val plotContainer = PlotContainer(
            PlotSvgRoot(
                plot,
                liveMapCursorServiceConfig = null,
                DoubleVector.ZERO
            )
        )

        val component = BatikMapperComponent(plotContainer.svg, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
        (component as DisposingHub).registerDisposable(plotContainer)
        return component
    }
}