/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.basemap.BasemapLayerComponent

class LayersRenderingSystem internal constructor(
    componentManager: EcsComponentManager,
    private val myLayerManager: LayerManager,
) : AbstractSystem<LiveMapContext>(componentManager) {
    private var myDirtyLayers: List<Int> = emptyList()

    val dirtyLayers: List<Int>
        get() = myDirtyLayers

    var updated: Boolean = true
        private set

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        updated = false
        if (context.camera.panFrameDistance != null) {

            val dirtyLayerEntities = getEntities<DirtyCanvasLayerComponent>()
                .toList()

            if (context.camera.panFrameDistance == Client.ZERO_VEC && dirtyLayerEntities.isEmpty()) {
                return
            }

            // always update basemap - it prevents glitch with frozen tiles at wrong pos
            val basemapLayers = getEntities<BasemapLayerComponent>()
            val layersToPan = (basemapLayers + dirtyLayerEntities).toSet()

            val dirtyLayers = layersToPan.map { it.get<CanvasLayerComponent>().canvasLayer }
            myDirtyLayers = layersToPan.map(EcsEntity::id)
            layersToPan.forEach {
                if (it.get<CanvasLayerComponent>().canvasLayer.panningPolicy == PanningPolicy.REPAINT) {
                    it.untag<DirtyCanvasLayerComponent>()
                }
            }

            myLayerManager.pan(context.camera.panDistance!!, dirtyLayers)
            updated = true

        } else {
            val dirtyEntities = getEntities<DirtyCanvasLayerComponent>().toList()
            myDirtyLayers = dirtyEntities.map(EcsEntity::id)
            updated = dirtyEntities.isNotEmpty()

            dirtyEntities.forEach {
                it.untag<DirtyCanvasLayerComponent>()
            }

            myLayerManager.repaint(
                dirtyEntities.map { it.get<CanvasLayerComponent>().canvasLayer }
            )
        }
    }
}
