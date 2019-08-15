package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGraphicsElement
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox

internal class TooltipMeter(
    private val tooltipLayer: SvgNode
) {
    fun measure(text: List<String>, fontSize: Double): DoubleVector {
        val tt = TooltipBox()
        tt.rootGroup.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        tooltipLayer.children().add(tt.rootGroup)
        tt.update(TooltipUpdater.IGNORED_COLOR, text, fontSize)

        return tt.contentSize.also { tooltipLayer.children().remove(tt.rootGroup) }
    }
}