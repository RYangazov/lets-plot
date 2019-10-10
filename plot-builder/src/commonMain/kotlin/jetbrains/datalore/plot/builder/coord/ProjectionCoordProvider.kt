package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Projection
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ProjectionCoordProvider private constructor(private val myProjectionX: Projection?, private val myProjectionY: Projection?)// square grid
    : jetbrains.datalore.plot.builder.coord.FixedRatioCoordProvider(1.0) {

    override fun buildAxisScaleX(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {
        return if (myProjectionX != null) {
            jetbrains.datalore.plot.builder.coord.ProjectionCoordProvider.Companion.buildAxisScaleWithProjection(
                myProjectionX,
                scaleProto,
                domain,
                axisLength,
                breaks
            )
        } else super.buildAxisScaleX(scaleProto, domain, axisLength, breaks)
    }

    override fun buildAxisScaleY(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {
        return if (myProjectionY != null) {
            jetbrains.datalore.plot.builder.coord.ProjectionCoordProvider.Companion.buildAxisScaleWithProjection(
                myProjectionY,
                scaleProto,
                domain,
                axisLength,
                breaks
            )
        } else super.buildAxisScaleY(scaleProto, domain, axisLength, breaks)
    }

    companion object {
        fun withProjectionY(projectionY: Projection): jetbrains.datalore.plot.builder.coord.CoordProvider {
            return jetbrains.datalore.plot.builder.coord.ProjectionCoordProvider(null, projectionY)
        }

        private fun buildAxisScaleWithProjection(projection: Projection, scaleProto: Scale<Double>,
                                                 domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {

            val validDomain = projection.toValidDomain(domain)
            val validDomainProjected = ClosedRange.closed(
                    projection.apply(validDomain.lowerEndpoint())!!,
                    projection.apply(validDomain.upperEndpoint())!!
            )

            val projectionInverse = Mappers.linear(validDomainProjected, validDomain)

            val linearMapper =
                jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.axisMapper(domain, axisLength)
            val scaleMapper = jetbrains.datalore.plot.builder.coord.ProjectionCoordProvider.Companion.twistScaleMapper(
                projection,
                projectionInverse,
                linearMapper
            )
            val validBreaks = jetbrains.datalore.plot.builder.coord.ProjectionCoordProvider.Companion.validateBreaks(
                validDomain,
                breaks
            )
            return jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.buildAxisScaleDefault(
                scaleProto,
                scaleMapper,
                validBreaks
            )
        }

        private fun validateBreaks(validDomain: ClosedRange<Double>, breaks: GuideBreaks): GuideBreaks {
            val validIndices = ArrayList<Int>()
            var i = 0
            for (v in breaks.domainValues) {
                if (v is Double && validDomain.contains(v)) {
                    validIndices.add(i)
                }
                i++
            }

            if (validIndices.size == breaks.domainValues.size) {
                return breaks
            }

            val validDomainValues = SeriesUtil.pickAtIndices(breaks.domainValues, validIndices)
            val validLabels = SeriesUtil.pickAtIndices(breaks.labels, validIndices)
            val validTransformedValues = SeriesUtil.pickAtIndices(breaks.transformedValues, validIndices)
            return GuideBreaks(validDomainValues, validTransformedValues, validLabels)
        }

        private fun twistScaleMapper(
            projection: Projection, projectionInverse: (Double) -> Double,
            scaleMapper: (Double?) -> Double?): (Double?) -> Double? {
            return { v ->
                val projected = projection.apply(v!!)
                val unProjected = projectionInverse(projected)
                scaleMapper(unProjected)
            }
        }
    }
}
