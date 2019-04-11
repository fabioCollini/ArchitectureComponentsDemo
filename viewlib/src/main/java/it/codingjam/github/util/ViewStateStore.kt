package it.codingjam.github.util


import android.os.AsyncTask
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.coroutines.CoroutineContext

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
        initialState: T,
        dispatcher: CoroutineDispatcher
) : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext = job + dispatcher

    private val stateLiveData = MutableLiveData<T>().apply {
        value = initialState
    }

    private val signalsLiveData = MutableLiveData<List<Signal>>()

    private var list: MutableList<Signal> = ArrayList()

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            stateLiveData.observe(owner, Observer { observer(it!!) })

    fun observeSignals(owner: LifecycleOwner, executor: (Signal) -> Unit) =
            signalsLiveData.observe(owner, Observer { _ ->
                list.forEach { executor(it) }
                list = ArrayList()
            })

    @MainThread
    fun dispatchState(state: T) {
        stateLiveData.value = state
    }

    fun dispatchSignal(action: Signal) {
        list.add(action)
        signalsLiveData.value = list
    }

    private fun dispatch(action: Action<T>) {
        if (action is StateAction<T>) {
            stateLiveData.value = action(invoke())
        } else if (action is Signal) {
            list.add(action)
            signalsLiveData.value = list
        }
    }

    fun dispatchAction(f: suspend () -> Action<T>) {
        launch {
            val action = f()
            withContext(Dispatchers.Main) {
                dispatch(action)
            }
        }
    }

    fun dispatchActions(f: suspend (T) -> Flow<Action<T>>) {
        launch {
            f(invoke())
                    .flowOn(Dispatchers.IO)
                    .collect { action ->
                        withContext(Dispatchers.Main) {
                            dispatch(action)
                        }
                    }
        }
    }

    operator fun invoke() = stateLiveData.value!!

    fun cancel() {
        job.cancel()
    }

    companion object {
        val TEST_DISPATCHER = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

        fun <T : Any> test(initialState: T): ViewStateStore<T> {
            return ViewStateStore(initialState, TEST_DISPATCHER)
        }
    }
}

suspend fun <S> FlowCollector<in Action<S>>.emitAction(action: S.() -> S) = emit(StateAction(action))

suspend inline fun <T : Any> Flow<Action<T>>.states(initialState: T): List<Any> {
    return fold(emptyList()) { states, action ->
        val element: Any = if (action is StateAction)
            action(states.lastOrNull() as T? ?: initialState)
        else
            action
        states + element
    }
}

suspend inline fun <reified S : Any> states(
        initialState: S,
        crossinline f: suspend (S) -> Flow<Action<S>>
): List<S> =
        f(initialState)
                .states(initialState)
                .filterIsInstance<S>()

suspend inline fun <reified S : Any> signals(
        initialState: S,
        crossinline f: suspend (S) -> Flow<Action<S>>
): List<Signal> =
        f(initialState)
                .states(initialState)
                .filterIsInstance<Signal>()

fun <T, R> Flow<Action<T>>.mapActions(copy: R.(StateAction<T>) -> R): Flow<Action<R>> =
        map { action: Action<T> -> action.map(copy) }

fun <R, S> Action<R>.map(copy: S.(StateAction<R>) -> S): Action<S> {
    return if (this is Signal) {
        this
    } else {
        val stateAction = this as StateAction<R>
        StateAction { copy(stateAction) }
    }
}