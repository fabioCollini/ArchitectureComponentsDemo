package it.codingjam.github.ui.search

import android.arch.lifecycle.ViewModel
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.ViewStateStore
import javax.inject.Inject

data class EchoViewState(
        val value: String = "0"
)

class EchoViewModel @Inject constructor(
        private val coroutines: Coroutines,
        private val echoInteractor: EchoInteractor
) : ViewModel() {

    val state = ViewStateStore(coroutines, EchoViewState())

    fun incr() {
//        state.dispatchState(echoInteractor::incr)
    }

    fun incrAsync() {
//        state += echoInteractor::incrAsync
        state.dispatchActions(echoInteractor.incrAsync(state()))
    }

    fun incrAsyncSingle() {
//        state.update(echoInteractor::incrAsyncSingle)
        state.dispatchActions(echoInteractor.incrAsync(state()))
    }

    fun incrAsyncSingle2() {
        state.dispatchStateAsync {
            echoInteractor.incrAsyncSingle(state())
        }
//        state.update(echoInteractor.incrAsync(state()))
    }
}