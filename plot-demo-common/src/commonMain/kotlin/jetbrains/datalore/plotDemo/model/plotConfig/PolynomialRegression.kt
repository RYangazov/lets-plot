/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import demoAndTestShared.parsePlotSpec

@Suppress("DuplicatedCode")

class PolynomialRegression {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            sinDemo(),
            mpgDemo()
        )
    }

    private fun sinDemo(): MutableMap<String, Any> {
        val spec = """
                {
                  "data": {
                    "x": [
                      -6.283185307179586,
                      -6.031857894892402,
                      -5.780530482605219,
                      -5.529203070318035,
                      -5.277875658030851,
                      -5.026548245743667,
                      -4.775220833456483,
                      -4.5238934211693,
                      -4.272566008882116,
                      -4.021238596594932,
                      -3.769911184307748,
                      -3.5185837720205644,
                      -3.2672563597333806,
                      -3.0159289474461968,
                      -2.764601535159013,
                      -2.513274122871829,
                      -2.2619467105846454,
                      -2.0106192982974616,
                      -1.7592918860102778,
                      -1.507964473723094,
                      -1.2566370614359101,
                      -1.0053096491487263,
                      -0.7539822368615425,
                      -0.5026548245743587,
                      -0.2513274122871749,
                      8.881784197001252e-15,
                      0.2513274122871927,
                      0.5026548245743765,
                      0.7539822368615603,
                      1.005309649148744,
                      1.256637061435928,
                      1.5079644737231117,
                      1.7592918860102955,
                      2.0106192982974793,
                      2.261946710584663,
                      2.513274122871847,
                      2.7646015351590307,
                      3.0159289474462145,
                      3.2672563597333983,
                      3.518583772020582,
                      3.769911184307766,
                      4.02123859659495,
                      4.272566008882134,
                      4.523893421169317,
                      4.775220833456501,
                      5.026548245743685,
                      5.277875658030869,
                      5.529203070318053,
                      5.780530482605236,
                      6.03185789489242
                    ],
                    "y": [
                      -7.393454106127859,
                      -4.645374910807927,
                      -7.3081020736725755,
                      -7.175525332276818,
                      -5.068038680111858,
                      -4.781230578075984,
                      -5.172502388837723,
                      -3.6198539158304874,
                      -2.388477666666948,
                      -3.0587768152659085,
                      -2.733494567500402,
                      -2.4342874734598547,
                      -1.433281942240699,
                      -4.600701985796842,
                      -1.5498740254575962,
                      -3.253318865432475,
                      -4.669256385132555,
                      -3.387587544940649,
                      -3.6905716853872224,
                      -2.494306214432506,
                      -1.3616516454951284,
                      -3.153207915031632,
                      -2.28159585512251,
                      -1.679144719488178,
                      0.11605070876571577,
                      0.47498858999482474,
                      -1.0990640738810764,
                      2.201849076102238,
                      1.9162359930160435,
                      2.592429525532008,
                      2.914125441273576,
                      2.5962399893337076,
                      2.619869775192352,
                      2.0534089740842343,
                      3.2135752090128697,
                      3.4894809841799956,
                      1.6030164861182241,
                      3.5067830066930057,
                      4.5794590486509446,
                      3.559846490186055,
                      4.10204574176777,
                      3.448549006120953,
                      3.7672291753605176,
                      3.000658193559272,
                      3.9983301512676954,
                      4.5562359965022585,
                      3.7731386967660243,
                      4.643405547593345,
                      5.100533229361776,
                      6.108317061451999
                    ]
                  },
                  "mapping": {
                    "x": "x",
                    "y": "y"
                  },
                  "data_meta": {},
                  "kind": "plot",
                  "scales": [],
                  "layers": [
                    {
                      "geom": "point",
                      "stat": null,
                      "data": null,
                      "mapping": {
                        "x": null,
                        "y": null
                      },
                      "position": null,
                      "show_legend": null,
                      "data_meta": {},
                      "sampling": null,
                      "animation": null,
                      "map_join": null,
                      "shape": 21,
                      "fill": "yellow",
                      "color": "#8c564b"
                    },
                    {
                      "geom": "smooth",
                      "stat": null,
                      "data": null,
                      "mapping": {
                        "x": null,
                        "y": null
                      },
                      "position": null,
                      "show_legend": null,
                      "data_meta": {},
                      "sampling": null,
                      "method": "lm",
                      "deg": 5,
                      "size": 1.5,
                      "color": "#d62728"
                    }
                  ],
                  'scales': [
                              {
                                'name': 'log10(y)',
                                'aesthetic': 'y',
                                'trans': 'identity'
                              }
                            ]
                } 
        """.trimMargin()


        return parsePlotSpec(spec)
    }

    private fun mpgDemo(): MutableMap<String, Any> {
        val spec = """
                        {
                          "data": {
                            "displ": [
                              1.8,
                              1.8,
                              2.0,
                              2.0,
                              2.8,
                              2.8,
                              3.1,
                              1.8,
                              1.8,
                              2.0,
                              2.0,
                              2.8,
                              2.8,
                              3.1,
                              3.1,
                              2.8,
                              3.1,
                              4.2,
                              5.3,
                              5.3,
                              5.3,
                              5.7,
                              6.0,
                              5.7,
                              5.7,
                              6.2,
                              6.2,
                              7.0,
                              5.3,
                              5.3,
                              5.7,
                              6.5,
                              2.4,
                              2.4,
                              3.1,
                              3.5,
                              3.6,
                              2.4,
                              3.0,
                              3.3,
                              3.3,
                              3.3,
                              3.3,
                              3.3,
                              3.8,
                              3.8,
                              3.8,
                              4.0,
                              3.7,
                              3.7,
                              3.9,
                              3.9,
                              4.7,
                              4.7,
                              4.7,
                              5.2,
                              5.2,
                              3.9,
                              4.7,
                              4.7,
                              4.7,
                              5.2,
                              5.7,
                              5.9,
                              4.7,
                              4.7,
                              4.7,
                              4.7,
                              4.7,
                              4.7,
                              5.2,
                              5.2,
                              5.7,
                              5.9,
                              4.6,
                              5.4,
                              5.4,
                              4.0,
                              4.0,
                              4.0,
                              4.0,
                              4.6,
                              5.0,
                              4.2,
                              4.2,
                              4.6,
                              4.6,
                              4.6,
                              5.4,
                              5.4,
                              3.8,
                              3.8,
                              4.0,
                              4.0,
                              4.6,
                              4.6,
                              4.6,
                              4.6,
                              5.4,
                              1.6,
                              1.6,
                              1.6,
                              1.6,
                              1.6,
                              1.8,
                              1.8,
                              1.8,
                              2.0,
                              2.4,
                              2.4,
                              2.4,
                              2.4,
                              2.5,
                              2.5,
                              3.3,
                              2.0,
                              2.0,
                              2.0,
                              2.0,
                              2.7,
                              2.7,
                              2.7,
                              3.0,
                              3.7,
                              4.0,
                              4.7,
                              4.7,
                              4.7,
                              5.7,
                              6.1,
                              4.0,
                              4.2,
                              4.4,
                              4.6,
                              5.4,
                              5.4,
                              5.4,
                              4.0,
                              4.0,
                              4.6,
                              5.0,
                              2.4,
                              2.4,
                              2.5,
                              2.5,
                              3.5,
                              3.5,
                              3.0,
                              3.0,
                              3.5,
                              3.3,
                              3.3,
                              4.0,
                              5.6,
                              3.1,
                              3.8,
                              3.8,
                              3.8,
                              5.3,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.2,
                              2.2,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.5,
                              2.7,
                              2.7,
                              3.4,
                              3.4,
                              4.0,
                              4.7,
                              2.2,
                              2.2,
                              2.4,
                              2.4,
                              3.0,
                              3.0,
                              3.5,
                              2.2,
                              2.2,
                              2.4,
                              2.4,
                              3.0,
                              3.0,
                              3.3,
                              1.8,
                              1.8,
                              1.8,
                              1.8,
                              1.8,
                              4.7,
                              5.7,
                              2.7,
                              2.7,
                              2.7,
                              3.4,
                              3.4,
                              4.0,
                              4.0,
                              2.0,
                              2.0,
                              2.0,
                              2.0,
                              2.8,
                              1.9,
                              2.0,
                              2.0,
                              2.0,
                              2.0,
                              2.5,
                              2.5,
                              2.8,
                              2.8,
                              1.9,
                              1.9,
                              2.0,
                              2.0,
                              2.5,
                              2.5,
                              1.8,
                              1.8,
                              2.0,
                              2.0,
                              2.8,
                              2.8,
                              3.6
                            ],
                            "hwy": [
                              29,
                              29,
                              31,
                              30,
                              26,
                              26,
                              27,
                              26,
                              25,
                              28,
                              27,
                              25,
                              25,
                              25,
                              25,
                              24,
                              25,
                              23,
                              20,
                              15,
                              20,
                              17,
                              17,
                              26,
                              23,
                              26,
                              25,
                              24,
                              19,
                              14,
                              15,
                              17,
                              27,
                              30,
                              26,
                              29,
                              26,
                              24,
                              24,
                              22,
                              22,
                              24,
                              24,
                              17,
                              22,
                              21,
                              23,
                              23,
                              19,
                              18,
                              17,
                              17,
                              19,
                              19,
                              12,
                              17,
                              15,
                              17,
                              17,
                              12,
                              17,
                              16,
                              18,
                              15,
                              16,
                              12,
                              17,
                              17,
                              16,
                              12,
                              15,
                              16,
                              17,
                              15,
                              17,
                              17,
                              18,
                              17,
                              19,
                              17,
                              19,
                              19,
                              17,
                              17,
                              17,
                              16,
                              16,
                              17,
                              15,
                              17,
                              26,
                              25,
                              26,
                              24,
                              21,
                              22,
                              23,
                              22,
                              20,
                              33,
                              32,
                              32,
                              29,
                              32,
                              34,
                              36,
                              36,
                              29,
                              26,
                              27,
                              30,
                              31,
                              26,
                              26,
                              28,
                              26,
                              29,
                              28,
                              27,
                              24,
                              24,
                              24,
                              22,
                              19,
                              20,
                              17,
                              12,
                              19,
                              18,
                              14,
                              15,
                              18,
                              18,
                              15,
                              17,
                              16,
                              18,
                              17,
                              19,
                              19,
                              17,
                              29,
                              27,
                              31,
                              32,
                              27,
                              26,
                              26,
                              25,
                              25,
                              17,
                              17,
                              20,
                              18,
                              26,
                              26,
                              27,
                              28,
                              25,
                              25,
                              24,
                              27,
                              25,
                              26,
                              23,
                              26,
                              26,
                              26,
                              26,
                              25,
                              27,
                              25,
                              27,
                              20,
                              20,
                              19,
                              17,
                              20,
                              17,
                              29,
                              27,
                              31,
                              31,
                              26,
                              26,
                              28,
                              27,
                              29,
                              31,
                              31,
                              26,
                              26,
                              27,
                              30,
                              33,
                              35,
                              37,
                              35,
                              15,
                              18,
                              20,
                              20,
                              22,
                              17,
                              19,
                              18,
                              20,
                              29,
                              26,
                              29,
                              29,
                              24,
                              44,
                              29,
                              26,
                              29,
                              29,
                              29,
                              29,
                              23,
                              24,
                              44,
                              41,
                              29,
                              26,
                              28,
                              29,
                              29,
                              29,
                              28,
                              29,
                              26,
                              26,
                              26
                            ]
                          },
                          "mapping": {
                            "x": "displ",
                            "y": "hwy"
                          },
                          "data_meta": {},
                          "kind": "plot",
                  'scales': [
                              {
                                'name': 'log10(y)',
                                'aesthetic': 'y',
                                'trans': 'identity'
                              }
                            ],
                          "layers": [
                            {
                              "geom": "point",
                              "stat": null,
                              "data": null,
                              "mapping": {
                                "x": null,
                                "y": null
                              },
                              "position": null,
                              "show_legend": null,
                              "data_meta": {},
                              "sampling": null,
                              "animation": null,
                              "map_join": null
                            },
                            {
                              "geom": "smooth",
                              "stat": null,
                              "data": null,
                              "mapping": {
                                "x": null,
                                "y": null
                              },
                              "position": null,
                              "show_legend": null,
                              "data_meta": {},
                              "sampling": null,
                              "deg": 2
                            }
                          ]
                        }
        """.trimMargin()

        return parsePlotSpec(spec)
    }
}