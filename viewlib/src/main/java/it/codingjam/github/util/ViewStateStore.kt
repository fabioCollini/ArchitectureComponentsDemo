package it.codingjam.github.util


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import kotlinx.coroutines.experimental.channels.*
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

interface Action<out T>

class StateAction<T>(private val f: T.() -> T) : Action<T> {
    operator fun invoke(t: T) = t.f()
}

interface Signal : Action<Nothing>

data class ErrorSignal(val error: Throwable?, val message: String) : Signal {
    constructor(t: Throwable) : this(t, t.message ?: "Error ${t.javaClass.name}")
}

data class NavigationSignal<P>(val destination: Any, val params: P) : Signal

class ViewStateStore<T : Any>(
        private val coroutines: Coroutines,
        initialState: T,
        private val liveData: MutableLiveData<T> = MutableLiveData()
) {

    init {
        liveData.value = initialState
    }

    private val delegate = MutableLiveData<List<Signal>>()

    private var list: MutableList<Signal> = ArrayList()

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            liveData.observe(owner, Observer { observer(it!!) })

    fun observeSignals(owner: LifecycleOwner, executor: (Signal) -> Unit) =
            delegate.observe(owner, Observer { _ ->
                list.forEach { executor(it) }
                list = ArrayList()
            })

    @MainThread
    fun dispatchState(state: T) {
        liveData.value = state
    }

    @MainThread
    fun dispatchState(action: StateAction<T>) {
        liveData.value = action(invoke())
    }

    fun dispatchSignal(action: Signal) {
        list.add(action)
        delegate.value = list
    }

    private fun dispatch(action: Action<T>) {
        if (action is StateAction<T>) {
            liveData.value = action(invoke())
        } else if (action is Signal) {
            list.add(action)
            delegate.value = list
        }
    }

    fun dispatchStateAsync(f: suspend () -> StateAction<T>) {
        coroutines {
            val action = f()
            coroutines.onUi {
                dispatchState(action)
            }
        }
    }

    fun dispatchSignalAsync(f: suspend () -> Signal) {
        coroutines {
            val signal = f()
            coroutines.onUi {
                dispatch(signal)
            }
        }
    }

    fun dispatchActions(f: ReceiveChannel<Action<T>>) {
        coroutines {
            f.consumeEach { action ->
                coroutines.onUi {
                    dispatch(action)
                }
            }
        }
    }

    operator fun invoke() = liveData.value!!

    fun cancel() = coroutines.cancel()
}


class StateUpdater<S>(private val scope: ProducerScope<Action<S>>) {
    suspend fun send(action: S.() -> S) = scope.send(StateAction(action))

    suspend fun sendSignal(signal: Signal) = scope.send(signal)
}

fun <S> execute(block: suspend StateUpdater<S>.() -> Unit): ReceiveChannel<Action<S>> {
    return produce {
        StateUpdater(this).block()
    }
}

suspend fun <S> ProducerScope<Action<S>>.send(action: S.() -> S) = send(StateAction(action))

fun <P, R> ReceiveChannel<Action<P>>.convert(f: R.(StateAction<P>) -> R): ReceiveChannel<Action<R>> {
    return map { originalAction ->
        convert(originalAction, f)
    }
}

fun <P, R> convert(originalAction: Action<P>, f: R.(StateAction<P>) -> R): Action<R> {
    return if (originalAction is Signal) {
        originalAction
    } else {
        val stateAction = originalAction as StateAction<P>
        StateAction<R> { f(stateAction) }
    }
}

inline fun <P, R> StateAction<P>.convert(crossinline f: R.(StateAction<P>) -> R): StateAction<R> {
    return StateAction {
        f(this@convert)
    }
}

suspend inline fun <T : Any> ReceiveChannel<Action<T>>.states(initialState: T): List<Any> {
    return fold(emptyList()) { states, action ->
        val element: Any = if (action is StateAction)
            action(states.lastOrNull() as T? ?: initialState)
        else
            action
        states + element
    }
}

inline fun <reified S : Any> states(
        initialState: S,
        crossinline f: (S) -> ReceiveChannel<Action<S>>
): List<S> = runBlocking {
    f(initialState)
            .states(initialState)
            .filterIsInstance<S>()
}

inline fun <reified S : Any> signals(
        initialState: S,
        crossinline f: (S) -> ReceiveChannel<Action<S>>
): List<Signal> = runBlocking {
    f(initialState)
            .states(initialState)
            .filterIsInstance<Signal>()
}

