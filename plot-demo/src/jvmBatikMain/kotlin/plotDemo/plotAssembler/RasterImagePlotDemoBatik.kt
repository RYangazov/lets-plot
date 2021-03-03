/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.plotDemo.model.plotAssembler.RasterImagePlotDemo
import jetbrains.datalore.vis.demoUtils.PlotObjectsViewerDemoWindowBatik

fun main() {
    with(RasterImagePlotDemo()) {
        PlotObjectsViewerDemoWindowBatik(
            "Raster image plot",
            plotList = createPlots()
        ).open()
    }
}
