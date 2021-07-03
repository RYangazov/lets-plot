/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import kotlin.math.pow
import kotlin.random.Random

class IntDev {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            issue292(),
            issue301()
        )
    }

    private fun issue292(): MutableMap<String, Any> {
        // NPE on negative value in data and scale_xxx(trans='log10')

        // ggplot(data, aes(x='x', y='y')) + geom_point(aes(color='v')) + scale_color_gradient(trans='log10')

        val spec = """
            {
             'kind': 'plot',
             'mapping': {'x': 'x', 'y': 'y'},
             'scales': [{'aesthetic': 'color',
               'trans': 'log10',
               'scale_mapper_kind': 'color_gradient'}],
             'layers': [
             {'geom': 'point',
               'mapping': {'color': 'v'}
                }]}
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to listOf(0, 1, 2, 3, 4),
            "y" to listOf(0, 1, 4, 9, 12),
            "v" to listOf(-1, 0, 0.01, 1, 81),
        )
        return plotSpec
    }

    private fun issue301(): MutableMap<String, Any> {
        // Need to skip "bad" values during scale transformation #301

        //        np.random.seed(42)
        //
        //        n = 1000
        //        x = np.linspace(-10, 10, n)
        //        y = x ** 2 + np.random.normal(size=n)
        //        z = 2.0 ** x + np.random.normal(size=n)
        //        data = {'x': x, 'y': y, 'z': z}
        //
        //        p = (ggplot(data, aes(x='x', y='y')) +
        //                geom_point(aes(color='z'), tooltips=layer_tooltips().format("^color", ".3f")) +
        //                # scale_y_log10() + scale_color_gradient(low='red', high='green', trans='log10'))
        //        scale_y_log10() + scale_color_gradient(low='red', high='green', trans='log10', limits=[0.01, None]))
        //
        //        # scale_y_log10() + scale_color_gradient(low='red', high='green'))

        val spec = """
        {             
         'mapping': {'x': 'x', 'y': 'y'},
         'kind': 'plot',
         'scales': [
           {'aesthetic': 'y',
           'trans': 'log10'
           },
          {'aesthetic': 'color',
           'limits': [0.01],
           'trans': 'log10',
           'low': 'red',
           'high': 'green',
           'scale_mapper_kind': 'color_gradient'}
           ],
         'layers': [
          {'geom': 'point',
           'mapping': {'color': 'z'},
           'tooltips': {'tooltip_formats': [{'field': '^color', 'format': '.3f'}]}
           }]
         }
         """.trimIndent()

        val n = 1000
        val step = 20.0 / n
        //        x = np.linspace(-10, 10, n)
        //        y = x ** 2 + np.random.normal(size=n)
        //        z = 2.0 ** x + np.random.normal(size=n)
        val x = generateSequence(-10.0) { it + step }.takeWhile { it <= 10 }.toList()
        val y = x.map { it.pow(2.0) + Random.nextDouble() * 10 }
        val z = x.map { 2.0.pow(it) + Random.nextDouble() * 10 }

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to x,
            "y" to y,
            "z" to z
        )
        return plotSpec
    }
}