package jetbrains.datalore.plotDemo.plotAssembler

import jetbrains.datalore.vis.demoUtils.browser.BrowserDemoUtil

private const val DEMO_PROJECT = "plot-demo"
private const val CALL_FUN = "jetbrains.datalore.plotDemo.plotAssembler.linearRegressionDemo"
private val LIBS = BrowserDemoUtil.KOTLIN_LIBS +
        BrowserDemoUtil.BASE_MAPPER_LIBS +
        BrowserDemoUtil.PLOT_LIBS +
        BrowserDemoUtil.DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(
            DEMO_PROJECT,
            CALL_FUN,
            LIBS, "Linear regression plot"
        )
    }
}
