/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.ImageGeom
import jetbrains.datalore.vis.demoUtils.PlotSpecsViewerDemoWindowBatik

fun main() {
    with(ImageGeom()) {
        PlotSpecsViewerDemoWindowBatik(
            "image_geom",
            plotSpecList()
        ).open()
    }
}
