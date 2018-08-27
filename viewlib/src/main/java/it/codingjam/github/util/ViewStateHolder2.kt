package it.codingjam.github.util


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.*

typealias StateAction<T> = (T) -> T

interface UiSignal

data class ErrorSignal(val t: Throwable?, val message: String) : UiSignal {
    constructor(t: Throwable) : this(t, t.message ?: "Error ${t.javaClass.name}")
}

data class NavigationSignal<P>(val destination: Any, val params: P) : UiSignal

class ViewStateHolder2<T : Any>(
        private val coroutines: Coroutines,
        initialState: T,
        private val liveData: MutableLiveData<T> = MutableLiveData()
) {

    init {
        liveData.value = initialState
    }

    private val delegate = MutableLiveData<List<UiSignal>>()

    private var list: MutableList<UiSignal> = ArrayList()

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            liveData.observe(owner, Observer { observer(it!!) })

    fun observeSignals(owner: LifecycleOwner, executor: (UiSignal) -> Unit) =
            delegate.observe(owner, Observer { _ ->
                list.forEach { executor(it) }
                list = ArrayList()
            })

    fun updateSynchronousStateAction(action: StateAction<T>) {
        liveData.value = action(liveData.value!!)
    }

    fun executeOnUi(action: UiSignal) {
        list.add(action)
        delegate.value = list
    }

    fun updateChannelCreator(f: (T) -> ReceiveChannel<Action<T>>) {
        coroutines {
            f(liveData.value!!).consumeEach { action ->
                coroutines.onUi {
                    manageAction(action)
                }
            }
        }
    }

    fun update(f: suspend (T) -> Action<T>) {
        coroutines {
            val action = f(liveData.value!!)
            coroutines.onUi {
                manageAction(action)
            }
        }
    }

    fun updateStateAction(f: suspend (T) -> StateAction<T>) {
        coroutines {
            val action = f(liveData.value!!)
            coroutines.onUi {
                liveData.value = action(liveData.value!!)
            }
        }
    }

    fun updateChannel(f: ReceiveChannel<Action<T>>) {
        coroutines {
            f.consumeEach { action ->
                coroutines.onUi {
                    manageAction(action)
                }
            }
        }
    }

    operator fun invoke() = liveData.value!!

    private suspend fun manageAction(action: Action<T>) {
        (action as Pair<StateAction<T>?, UiSignal?>).let { pair ->
            pair.first?.let {
                liveData.value = it(liveData.value!!)
            }
            pair.second?.let {
                coroutines.onUi {
                    executeOnUi(it)
                }
            }
        }
    }
}


typealias Action<S> = Any

class StateUpdater<S>(private val scope: ProducerScope<Any>) {
    suspend fun send(action: StateAction<S>) = scope.send(action to null)

    suspend fun sendSignal(signal: UiSignal) = scope.send(null to signal)

    suspend fun sendAll(channel: ReceiveChannel<Any>) {
        for (e in channel) {
            scope.send(e)
        }
    }
}

fun <S> execute(block: suspend StateUpdater<S>.() -> Unit): ReceiveChannel<Any> {
    return produce {
        StateUpdater<S>(this).block()
    }
}