package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.CollectionListener
import jetbrains.datalore.base.observable.collections.DataloreIndexOutOfBoundsException
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

abstract class AbstractObservableList<ItemT> : AbstractMutableList<ItemT>(), ObservableList<ItemT> {
    private var myListeners: Listeners<CollectionListener<ItemT>>? = null

    protected open fun checkAdd(index: Int, item: ItemT) {
        if (index < 0 || index > size) {
            throw DataloreIndexOutOfBoundsException("Add: index=$index, size=$size")
        }
    }

    protected open fun checkSet(index: Int, oldItem: ItemT, newItem: ItemT) {
        if (index < 0 || index >= size) {
            throw DataloreIndexOutOfBoundsException("Set: index=$index, size=$size")
        }
    }

    protected open fun checkRemove(index: Int, item: ItemT) {
        if (index < 0 || index >= size) {
            throw DataloreIndexOutOfBoundsException("Remove: index=$index, size=$size")
        }
    }

    override fun add(index: Int, element: ItemT) {
        checkAdd(index, element)
        beforeItemAdded(index, element)
        var success = false
        try {
            doAdd(index, element)
            success = true
            onItemAdd(index, element)
            if (myListeners != null) {
                val event = CollectionItemEvent(null, element, index, CollectionItemEvent.EventType.ADD)
                myListeners!!.fire(object : ListenerCaller<CollectionListener<ItemT>> {
                    override fun call(l: CollectionListener<ItemT>) {
                        l.onItemAdded(event)
                    }
                })
            }
        } finally {
            afterItemAdded(index, element, success)
        }
    }

    protected abstract fun doAdd(index: Int, item: ItemT)

    protected open fun beforeItemAdded(index: Int, item: ItemT) {}

    protected fun onItemAdd(index: Int, item: ItemT) {}

    protected fun afterItemAdded(index: Int, item: ItemT, success: Boolean) {}

    override operator fun set(index: Int, element: ItemT): ItemT {
        val old = get(index)
        checkSet(index, old, element)
        beforeItemSet(index, old, element)
        var success = false
        try {
            doSet(index, element)
            success = true
            onItemSet(index, old, element)
            if (myListeners != null) {
                val event = CollectionItemEvent(old, element, index, CollectionItemEvent.EventType.SET)
                myListeners!!.fire(object : ListenerCaller<CollectionListener<ItemT>> {
                    override fun call(l: CollectionListener<ItemT>) {
                        l.onItemSet(event)
                    }
                })
            }
        } finally {
            afterItemSet(index, old, element, success)
        }
        return old
    }

    protected open fun doSet(index: Int, item: ItemT) {
        doRemove(index)
        doAdd(index, item)
    }

    protected open fun beforeItemSet(index: Int, oldItem: ItemT, newItem: ItemT) {}

    protected fun onItemSet(index: Int, oldItem: ItemT, newItem: ItemT) {}

    protected fun afterItemSet(index: Int, oldItem: ItemT, newItem: ItemT, success: Boolean) {}

    override fun removeAt(index: Int): ItemT {
        val item = get(index)
        checkRemove(index, item)
        beforeItemRemoved(index, item)
        var success = false
        try {
            doRemove(index)
            success = true
            onItemRemove(index, item)
            if (myListeners != null) {
                val event = CollectionItemEvent(item, null, index, CollectionItemEvent.EventType.REMOVE)
                myListeners!!.fire(object : ListenerCaller<CollectionListener<ItemT>> {
                    override fun call(l: CollectionListener<ItemT>) {
                        l.onItemRemoved(event)
                    }
                })
            }
        } finally {
            afterItemRemoved(index, item, success)
        }
        return item
    }

    protected abstract fun doRemove(index: Int)

    protected open fun beforeItemRemoved(index: Int, item: ItemT) {}

    protected fun onItemRemove(index: Int, item: ItemT) {}

    protected fun afterItemRemoved(index: Int, item: ItemT, success: Boolean) {}

    override fun addListener(l: CollectionListener<ItemT>): Registration {
        if (myListeners == null) {
            myListeners = object : Listeners<CollectionListener<ItemT>>() {
                override fun beforeFirstAdded() {
                    onListenersAdded()
                }

                override fun afterLastRemoved() {
                    myListeners = null
                    onListenersRemoved()
                }
            }
        }

        return myListeners!!.add(l)
    }

    override fun addHandler(handler: EventHandler<in CollectionItemEvent<ItemT>>): Registration {
        val listener = object : CollectionListener<ItemT> {
            override fun onItemAdded(event: CollectionItemEvent<ItemT>) {
                handler.onEvent(event)
            }

            override fun onItemSet(event: CollectionItemEvent<ItemT>) {
                handler.onEvent(event)
            }

            override fun onItemRemoved(event: CollectionItemEvent<ItemT>) {
                handler.onEvent(event)
            }
        }
        return addListener(listener)
    }

    protected open fun onListenersAdded() {}

    protected open fun onListenersRemoved() {}
}