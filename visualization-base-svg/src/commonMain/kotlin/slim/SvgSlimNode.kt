package jetbrains.datalore.visualization.base.svg.slim

interface SvgSlimNode {
    val elementName: String
    val attributes: Iterable<Attr>
    val children: Iterable<SvgSlimNode>

    interface Attr {
        val key: String
        val value: String
    }
}
