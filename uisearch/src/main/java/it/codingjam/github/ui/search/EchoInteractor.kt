package it.codingjam.github.ui.search

import it.codingjam.github.util.StateAction
import it.codingjam.github.util.execute
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EchoInteractor @Inject constructor() {

    fun incr(state: EchoViewState) = state.copy(value = state.value + 1)

    fun incrAsync(state: EchoViewState) = execute<EchoViewState> {
        send {
            state.copy(value = "Loading...")
        }

        delay(1000)

        send {
            state.copy(value = state.value + 1)
        }

        delay(1000)

        send {
            state.copy(value = state.value + 2)
        }
    }

    suspend fun incrAsyncSingle(initialState: EchoViewState): StateAction<EchoViewState> {
        delay(1000)

        return {
            it.copy(value = initialState.value + 1)
        }
    }
}