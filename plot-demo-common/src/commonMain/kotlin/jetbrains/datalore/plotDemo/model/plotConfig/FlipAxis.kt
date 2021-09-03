/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class FlipAxis {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            pointsAndSmooth(),
            pointsOnly(),
            smoothOnly()
        )
    }

    private fun pointsAndSmooth(): MutableMap<String, Any> {
        val spec = """
{
 'mapping': {'x': 'x', 'y': 'y'},
 'kind': 'plot',
 'layers': [{
   'geom': 'point',
   'color': 'black',
   'alpha': 0.6,
   'size': 5},
  {'geom': 'smooth'
   }]
 }
         """.trimIndent()
        val map = parsePlotSpec(spec)
        map["data"] = data
        return map
    }

    private fun pointsOnly(): MutableMap<String, Any> {
        val spec = """
{
 'mapping': {'x': 'x', 'y': 'y'},
 'kind': 'plot',
 'layers': [{
   'geom': 'point',
   'color': 'black',
   'alpha': 0.6,
   'size': 5}
   ]
 }
         """.trimIndent()

        val map = parsePlotSpec(spec)
        map["data"] = data
        return map
    }

    private fun smoothOnly(): MutableMap<String, Any> {
        val spec = """
{
 'mapping': {'x': 'x', 'y': 'y'},
 'kind': 'plot',
 'layers': [
  {'geom': 'smooth'
   }]
 }
         """.trimIndent()

        val map = parsePlotSpec(spec)
        map["data"] = data
        return map
    }

    companion object {
        private val x = listOf(
            -0.85682293, -2.3911234, -2.42744314, -1.94456221, -3.08116168,
            -2.82149096, -2.75909911, -0.5976029, -2.28422114, -2.87574481,
            -2.44764864, 0.62140045, -2.78634844, -0.77798494, -1.84775972,
            -0.68368036, -1.16449909, -2.54485003, -2.2036209, -1.68242049
        )
        private val y = listOf(
            -1.52369374, 0.10800142, -0.04456882, 0.24980294, 0.07656286,
            1.18430493, 0.60070862, -1.3039589, 0.54056665, 0.4756451,
            1.23440038, -2.64721071, 0.86629033, -1.01436946, -0.30687369,
            -1.23137015, -0.41737117, 0.54053481, -0.71151953, -1.37503288
        )
        private val data = mapOf(
            "x" to x.map { it - 5 },
            "y" to y.map { it / 3 }
        )
//        private val data = mapOf(
//            "x" to listOf(0.0, 0.0),
//            "y" to listOf(0.0, 0.4)
//        )
    }
}