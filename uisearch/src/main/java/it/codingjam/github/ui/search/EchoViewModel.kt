package it.codingjam.github.ui.search

import android.arch.lifecycle.ViewModel
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.ViewStateHolder2
import javax.inject.Inject

data class EchoViewState(
        val value: String = "0"
)

class EchoViewModel @Inject constructor(
        private val coroutines: Coroutines,
        private val echoInteractor: EchoInteractor
) : ViewModel() {

    val state = ViewStateHolder2(coroutines, EchoViewState())

    fun incr() {
        state.updateSynchronousStateAction(echoInteractor::incr)
    }

    fun incrAsync() {
        state.updateChannelCreator(echoInteractor::incrAsync)
//        state.update(echoInteractor.incrAsync(state()))
    }

    fun incrAsyncSingle() {
//        state.update(echoInteractor::incrAsyncSingle)
        state.updateChannel(echoInteractor.incrAsync(state()))
    }

    fun incrAsyncSingle2() {
        state.updateStateAction(echoInteractor::incrAsyncSingle)
//        state.update(echoInteractor.incrAsync(state()))
    }
}