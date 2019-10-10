package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.HitIndex
import jetbrains.datalore.plot.builder.interact.TestUtil.PathPoint
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class LayerTargetLocatorPathHoverXTest : jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase() {

    override val strategy: LookupStrategy
        get() = LookupStrategy.HOVER

    @Test
    fun hoverX_WhenCloserToLeft() {
        assertThat(
                findTargets(rightFrom(p1,
                    jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.THIS_POINT_DISTANCE
                ))
        ).first().has(HitIndex.equalTo(p1.hitIndex))
    }


    @Test
    fun hoverX_WhenCloserToRight() {
        assertThat(
                findTargets(rightFrom(p1,
                    jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.NEXT_POINT_DISTANCE
                ))
        ).first().has(HitIndex.equalTo(p2.hitIndex))
    }

    @Test
    fun hoverX_WhenInTheMiddle_ShouldSelectSecondPoint() {
        assertThat(
                findTargets(rightFrom(p1,
                    jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.MIDDLE_POINTS_DISTANCE
                ))
        ).first().has(HitIndex.equalTo(p1.hitIndex))
    }


    @Test
    fun hoverX_WhenOutOfPath_ShouldFindNothing() {
        assertThat(
                findTargets(leftFrom(p0,
                    jetbrains.datalore.plot.builder.interact.loc.TargetLocatorPathXTestBase.Companion.NEXT_POINT_DISTANCE
                ))
        ).isEmpty()
    }


    private fun leftFrom(p: PathPoint, distance: Double): DoubleVector {
        return DoubleVector(p.x - distance, p.y)
    }

    private fun rightFrom(p: PathPoint, distance: Double): DoubleVector {
        return DoubleVector(p.x + distance, p.y)
    }


    private fun findTargets(p: DoubleVector): List<GeomTarget> {
        return jetbrains.datalore.plot.builder.interact.TestUtil.findTargets(locator, p)
    }
}
