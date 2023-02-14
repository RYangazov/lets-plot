/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.data.Iris
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class LoessRegression {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            autoMpg(),
            sinPlot()
        )
    }

    private fun autoMpg(): MutableMap<String, Any> {
        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {'text': 'Loess Regression'},
              'mapping': {
                'x': '${AutoMpg.horsepower.name}',
                'y': '${AutoMpg.mpg.name}'
              },
              'layers': [
                {
                  'geom': 'point'
                },
                {
                  'geom': 'smooth',
                  'method': 'loess'
                }
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = AutoMpg.df
        return plotSpec
    }

    private fun sinPlot(): MutableMap<String, Any> {
        // data
        val dx = 0.01
        val valuesX = generateSequence(0.0) { it + dx }.takeWhile { it < 3 * PI }.toList()
        val valuesY = valuesX.map(::sin).map { it + Random.nextDouble(-0.6, 0.6) }
        val sinusData = mapOf(
            "x" to valuesX,
            "y" to valuesY
        )

        val spec = """
            {
              'kind': 'plot',
              'ggtitle': {'text': 'loess span=0.5(blue) and span=0.3(green)'},
              'mapping': {
                'x': 'x',
                'y': 'y'
              },
              'layers': [
                {
                  'geom': 'point',
                  'shape' : 21,
                  'fill' : "#ffffbf",
                  'color' : "light-gray"
                },
                {
                  'geom': 'smooth',
                  'method': 'loess',
                  'color' : "blue"
                },
                {
                  'geom': 'smooth',
                  'method': 'loess',
                  'color': "green",
                  'span': 0.3
                }  
              ]
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = sinusData
        return plotSpec
    }

}