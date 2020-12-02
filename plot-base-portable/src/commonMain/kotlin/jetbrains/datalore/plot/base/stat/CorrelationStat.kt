/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.stat.CorrelationUtil.correlationMatrix
import jetbrains.datalore.plot.base.stat.math3.correlationPearson
import kotlin.math.abs

class CorrelationStat(
    val correlationMethod: Method,
    val type: Type,
    val fillDiagonal: Boolean
) : BaseStat(DEF_MAPPING) {

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        require(correlationMethod == Method.PEARSON) {
            "Unsupported correlation method: $correlationMethod (only Pearson is currently available)"
        }


        val cm = correlationMatrix(data, type, fillDiagonal, ::correlationPearson)
        val values = cm.getNumeric(Stats.CORR)
        val abs: List<Double?> = values.map { it?.let(::abs) }

        return cm.builder().putNumeric(Stats.CORR_ABS, abs).build()
    }

    override fun consumes(): List<Aes<*>> {
        return listOf()
    }

    enum class Method {
        PEARSON,
        SPEARMAN,
        KENDALL
    }

    enum class Type {
        FULL,
        UPPER,
        LOWER
    }

    companion object {

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.COLOR to Stats.CORR,
            Aes.FILL to Stats.CORR,
            Aes.LABEL to Stats.CORR
        )

        val DEF_CORRELATION_METHOD = Method.PEARSON
        val DEF_TYPE = Type.FULL
        const val DEF_FILL_DIAGONAL = true
    }
}