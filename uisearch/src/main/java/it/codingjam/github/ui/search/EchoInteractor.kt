package it.codingjam.github.ui.search

import it.codingjam.github.util.StateAction
import it.codingjam.github.util.produceActions
import it.codingjam.github.util.send
import kotlinx.coroutines.experimental.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoInteractor @Inject constructor() {

    fun incr(state: EchoViewState) = state.copy(value = state.value + 1)

    fun incrAsync(state: EchoViewState) = produceActions<EchoViewState> {
        send {
            copy(value = "Loading...")
        }

        delay(1000)

        send {
            copy(value = value + 1)
        }

        delay(1000)

        send {
            copy(value = value + 2)
        }
    }

    suspend fun incrAsyncSingle(initialState: EchoViewState): StateAction<EchoViewState> {
        delay(1000)

        return StateAction {
            copy(value = initialState.value + 1)
        }
    }
}