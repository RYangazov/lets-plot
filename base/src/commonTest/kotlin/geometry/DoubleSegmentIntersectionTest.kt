package jetbrains.datalore.base.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class DoubleSegmentIntersectionTest {
    @Test
    fun simpleIntersection() {
        val s1 = DoubleSegment(DoubleVector(0.0, 1.0), DoubleVector(0.0, -1.0))
        val s2 = DoubleSegment(DoubleVector(1.0, 0.0), DoubleVector(-1.0, 0.0))
        assertEquals(DoubleVector(0.0, 0.0), s1.intersection(s2))
    }

    @Test
    fun noIntersectionBecauseParallel() {
        val s1 = DoubleSegment(DoubleVector(0.0, 1.0), DoubleVector(0.0, -1.0))
        val s2 = DoubleSegment(DoubleVector(1.0, 1.0), DoubleVector(1.0, -1.0))
        assertNull(s1.intersection(s2))
    }

    @Test
    fun noIntersectBecauseOutOfSegments() {
        val s1 = DoubleSegment(DoubleVector(10.0, 0.0), DoubleVector(9.0, 0.0))
        val s2 = DoubleSegment(DoubleVector(0.0, 10.0), DoubleVector(0.0, 9.0))
        assertNull(s1.intersection(s2))
    }

    @Test
    fun perpendicularWithoutIntersection() {
        val s1 = DoubleSegment(DoubleVector(100.0, 100.0), DoubleVector(140.0, 100.0))
        val s2 = DoubleSegment(DoubleVector(120.0, 120.0), DoubleVector(120.0, 320.0))
        assertNull(s1.intersection(s2))
    }
}