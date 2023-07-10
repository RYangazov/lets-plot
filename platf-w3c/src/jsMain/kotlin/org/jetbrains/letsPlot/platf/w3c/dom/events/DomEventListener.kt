/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.events

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

class DomEventListener<EventT : Event>(private val handler: (EventT) -> Boolean) : EventListener {
    override fun handleEvent(event: Event) {
        @Suppress("UNCHECKED_CAST")
        handler(event as EventT)
    }
}
