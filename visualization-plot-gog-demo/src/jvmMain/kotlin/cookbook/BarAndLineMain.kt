package jetbrains.datalore.visualization.gogDemo.cookbook

import jetbrains.datalore.visualization.gogDemo.SwingDemoUtil
import jetbrains.datalore.visualization.gogDemo.model.cookbook.BarAndLine

class BarAndLineMain : BarAndLine() {

    private fun show() {
        val plotSpecList = listOf(
                defaultBarDiscreteX(),
                barDiscreteXFill(),
                barDiscreteXFillMappedInGeom(),
                barDiscreteXFillAndBlackOutline(),
                barDiscreteXTitleAxisLabelsNarrowWidth()
        )

        SwingDemoUtil.show(viewSize, plotSpecList as List<MutableMap<String, Any>>)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            BarAndLineMain().show()
        }
    }
}
