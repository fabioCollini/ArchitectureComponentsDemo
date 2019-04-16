package it.codingjam.github.util


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

class ViewStateStoreFactory(
        private val dispatcher: CoroutineDispatcher
) {
    operator fun <T : Any> invoke(initialState: T, scope: CoroutineScope) =
            ViewStateStore(initialState, scope, dispatcher)
}

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
        scope.launch(Dispatchers.Main.immediate) {
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

    fun dispatchActions(flow: ActionsFlow<T>) {
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