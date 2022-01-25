/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Front-end.
 */
internal object PlotConfigScales {

    internal fun createScales(
        layerConfigs: List<LayerConfig>,
        transformByAes: Map<Aes<*>, Transform>,
        scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>,
    ): TypedScaleMap {

        val dataByVarBinding = PlotConfigUtil.associateVarBindingsWithData(
            layerConfigs,
            excludeStatVariables = false
        )

        val variablesByMappedAes = PlotConfigUtil.associateAesWithMappedVariables(
            PlotConfigUtil.getVarBindings(
                layerConfigs,
                excludeStatVariables = false
            )
        )

        // All aes used in bindings.
        val aesSet: Set<Aes<*>> = dataByVarBinding.keys.map { it.aes }.toSet()

        // Compute domains for 'continuous' data
        // but exclude all 'positional' aes.
        //
        // Domains for X, Y axis are computed later.
        //      See: PlotAssemblerUtil.computePlotDryRunXYRanges()

        val continuousDomainByAesRaw = HashMap<Aes<*>, ClosedRange<Double>?>()

        // Continuous domains from 'data'.
        for ((varBinding, data) in dataByVarBinding) {
            val aes = varBinding.aes
            val variable = varBinding.variable
            val transform = transformByAes.getValue(aes)

            if (transform is ContinuousTransform && !Aes.isPositionalXY(aes)) {
                continuousDomainByAesRaw[aes] = SeriesUtil.span(
                    continuousDomainByAesRaw[aes], PlotConfigUtil.computeContinuousDomain(data, variable, transform)
                )
            }
        }

        // make sure all continuous domains are 'applicable range' (not emprty and not null)
        val continuousDomainByAes = continuousDomainByAesRaw.mapValues {
            val aes = it.key
            val transform: ContinuousTransform = transformByAes.getValue(aes) as ContinuousTransform
            Transforms.ensureApplicableDomain(it.value, transform)
        }

        // Create scales for all aes.
        val scaleByAes = HashMap<Aes<*>, Scale<*>>()
        for (aes in aesSet + setOf(Aes.X, Aes.Y)) {
            val defaultName = PlotConfigUtil.defaultScaleName(aes, variablesByMappedAes)
            val scaleProvider = scaleProviderByAes.getValue(aes)

            @Suppress("MoveVariableDeclarationIntoWhen")
            val transform = transformByAes.getValue(aes)

            val scale = when (transform) {
                is DiscreteTransform -> scaleProvider.createScale(defaultName, transform)
                else -> {
                    transform as ContinuousTransform
                    if (continuousDomainByAes.containsKey(aes)) {
                        val continuousDomain = continuousDomainByAes.getValue(aes)
                        scaleProvider.createScale(defaultName, transform, continuousDomain)
                    } else {
                        // Positional aes & continuous domain.
                        // The domain doesn't matter - it will be computed later (see: PlotAssemblerUtil.computePlotDryRunXYRanges())
                        scaleProvider.createScale(defaultName, transform, ClosedRange.singleton(0.0))
                    }
                }
            }

            scaleByAes[aes] = scale
        }

        return TypedScaleMap(scaleByAes)
    }
}