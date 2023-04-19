/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.scale

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.base.datetime.Duration
import jetbrains.datalore.base.datetime.Month
import jetbrains.datalore.base.datetime.tz.TimeZone
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.random.Random

class DateTimeAnnotation {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            plot(Duration.DAY),
            colorMapping(),
        )
    }

    companion object {
        fun plot(period: Duration): MutableMap<String, Any> {
            val instant = TimeZone.UTC.toInstant(DateTime(Date(1, Month.FEBRUARY, 2003)))

            val rnd = Random(0)

            val n = 30
            val time = (0..n).map { instant.timeSinceEpoch + it * period.duration }
            val values = (0..n).map { rnd.nextDouble(0.0, 20.0) }

            val spec = """
                {
                  'kind': 'plot',
                  'data': {
                     'time':   [ ${time.joinToString()} ],
                     'values': [ ${values.joinToString()} ]
                  },
                  'data_meta' : {
                    'series_annotations': [ 
                      {
                        'column': 'time',
                        'type': 'datetime'
                      }
                    ]
                  },
                  'mapping': {
                    'x': 'time',
                    'y': 'values'
                  },
                  'layers': [
                    {
                      'geom': 'line'
                    }
                  ]
                }""".trimIndent()

            return parsePlotSpec(spec)
        }

        fun colorMapping(): MutableMap<String, Any> {
            val spec = """
                {
                    "kind": "plot",
                     "ggtitle": { "text": "Color datetime mapping" },
                    "data": {
                        "val": [1.0, 2.0, 3.0, 4.0, 5.0], 
                        "days": [1609459200000, 1614038400000, 1617408000000, 1620086400000, 1633392000000]
                    }, 
                    "layers": [
                        { "geom": "bar", "mapping": { "x": "days", "color": "days" } }
                    ], 
                    "data_meta": { 
                        "series_annotations": [{"column": "days", "type": "datetime"}]
                    }
                }
            """.trimIndent()

            return parsePlotSpec(spec)
        }
    }
}
