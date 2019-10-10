package jetbrains.datalore.visualization.plotDemo.model

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector

class EmptyGeomContext : GeomContext {
    override val targetCollector: GeomTargetCollector =
        NullGeomTargetCollector()

    override fun getResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun getUnitResolution(aes: Aes<Double>): Double {
        throw IllegalStateException("Not available in an empty geom context")
    }

    override fun withTargetCollector(targetCollector: GeomTargetCollector): GeomContext {
        throw IllegalStateException("Not available in an empty geom context")
    }
}