package it.codingjam.github.ui.search

import android.arch.lifecycle.ViewModel
import it.codingjam.github.util.Coroutines
import it.codingjam.github.util.StateAction
import it.codingjam.github.util.ViewStateStore
import it.codingjam.github.util.convert
import javax.inject.Inject

data class MultiEchoViewState(
        val value1: EchoViewState,
        val value2: EchoViewState
)

class MultiEchoViewModel @Inject constructor(
        private val coroutines: Coroutines,
        private val echoInteractor: EchoInteractor
) : ViewModel() {

    val state = ViewStateStore(coroutines, MultiEchoViewState(
            EchoViewState(),
            EchoViewState()
    ))

    fun <S> ViewStateStore<MultiEchoViewState>.sub(f: (S, MultiEchoViewState) -> MultiEchoViewState): ViewStateStore<EchoViewState> {
        return ViewStateStore(coroutines, EchoViewState())
    }

    fun incr() {
//        state.sub<EchoViewState> { newSub, state -> state.copy(value1 = newSub) }
//                .plusAssign(echoInteractor.incr(state().value1))

        state.dispatchState(StateAction {
            copy(value1 = echoInteractor.incr(value1))
        })
    }

    fun incrAsyncSingle() {
//        val converter: MultiEchoViewState.((EchoViewState) -> EchoViewState) -> MultiEchoViewState = { copy(value1 = it(value1)) }
//
//        asyncStateAction2({ copy(value1 = it(value1)) }) {
//            echoInteractor.incrAsyncSingle(state().value1)
//        }

        state.dispatchStateAsync {
            echoInteractor.incrAsyncSingle(state().value1)
                    .convert { copy(value1 = it(value1)) }
        }
        state.dispatchStateAsync {
            echoInteractor.incrAsyncSingle(state().value1)
                    .convert { copy(value1 = it(value1)) }
        }

//            val v: ((EchoViewState) -> EchoViewState) = echoInteractor.incrAsyncSingle(state().value1)
//
//        val f: suspend () -> ((MultiEchoViewState) -> MultiEchoViewState) = {
//            { it: MultiEchoViewState -> it }
//        }
//        state.asyncStateAction(f)
    }

//    fun asyncStateAction2(
//            converter: MultiEchoViewState.((EchoViewState) -> EchoViewState) -> MultiEchoViewState,
//            f: suspend () -> (EchoViewState) -> EchoViewState
//    ) {
//        state.asyncStateAction {
//            f().convert(converter)
//        }
//    }

    fun incrAsync() {
//        state.dispatchActions(echoInteractor.incrAsync(state().value1).convert {
//            copy(value1 = it(value1))
//        })
    }
}
