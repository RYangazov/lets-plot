/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import jetbrains.livemap.mapengine.viewport.CellKey

internal interface TileDataFetcher {
    fun fetch(cellKey: CellKey): Async<List<TileLayer>>
}