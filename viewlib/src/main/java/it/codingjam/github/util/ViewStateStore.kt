package it.codingjam.github.util


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewStateStore<T : Any>(
        initialState: T,
        private val scope: CoroutineScope,
        private val dispatcher: CoroutineDispatcher
) {

    private val stateLiveData = MutableLiveData<T>().apply {
        value = initialState
    }

    private val signalsLiveData = EventsLiveData<Signal>()

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            stateLiveData.observe(owner, Observer { observer(it!!) })

    fun observeSignals(owner: LifecycleOwner, observer: (Signal) -> Unit) =
            signalsLiveData.observe(owner) { observer(it) }

    fun dispatchState(state: T) {
        scope.launch {
            stateLiveData.value = state
        }
    }

    fun dispatchSignal(action: Signal) {
        signalsLiveData.addEvent(action)
    }

    private fun dispatch(action: Action<T>) {
        if (action is StateAction<T>) {
            stateLiveData.value = action(invoke())
        } else if (action is Signal) {
            signalsLiveData.addEvent(action)
        }
    }

    fun dispatchAction(f: suspend () -> Action<T>) {
//        dispatchActions(flow { emit(f()) })
        scope.launch {
            val action = withContext(dispatcher) {
                f()
            }
            dispatch(action)
        }
    }

    fun dispatchActions(flow: Flow<Action<T>>) {
        scope.launch {
            flow
                    .flowOn(dispatcher)
                    .collect { action ->
                        dispatch(action)
                    }
        }
    }

    operator fun invoke() = stateLiveData.value!!
}

suspend fun <S> FlowCollector<Action<S>>.emitAction(action: S.() -> S) = emit(StateAction(action))

suspend inline fun <T : Any> Flow<Action<T>>.states(initialState: T): List<Any> {
    return fold(initialState to emptyList<Any>()) { (prevState, states), action ->
        if (action is StateAction) {
            val curState = action(prevState)
            curState to states + curState
        } else
            prevState to states + action
    }.second
}

fun <R, S> Flow<Action<R>>.mapActions(copy: S.(StateAction<R>) -> S): Flow<Action<S>> =
        map { action: Action<R> -> action.map(copy) }

fun <R, S> Action<R>.map(copy: S.(StateAction<R>) -> S): Action<S> {
    return if (this is Signal) {
        this
    } else {
        val stateAction = this as StateAction<R>
        StateAction { copy(stateAction) }
    }
}