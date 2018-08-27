package it.codingjam.github.ui.search

import assertk.assert
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

typealias Action<T> = (T) -> T

class Store<T>(initialState: T) {
    var state: T = initialState
        private set

    fun execute(f: Action<T>) {
        state = f(state)
    }

    suspend fun execute2(f: suspend (T) -> ReceiveChannel<T>) {
        f(state).consumeEach { state = it }
    }

    suspend fun execute3(f: ReceiveChannel<Action<T>>) {
        f.consumeEach {
            state = it(state)
        }
    }
}

class MyViewModel {
    fun simpleAction(state: String) = state + "d"

    suspend fun asyncAction(state: String): ReceiveChannel<String> = produce {
        send("loading")
        delay(100)
        send("done")
    }

    suspend fun asyncAction2(state: String): ReceiveChannel<Action<String>> = produce<Action<String>> {
        send { "$state loading" }
        delay(100)
        send { "$state done" }
    }
}

class ReduxTest {

    @Test
    fun syncExample() {
        val store = Store("abc")

        store.execute {
            it + "d"
        }

        assertk.assert(store.state).isEqualTo("abcd")
    }

    @Test
    fun syncExampleVM() {
        val vm = MyViewModel()

        val store = Store("abc")

        store.execute(vm::simpleAction)

        assert(store.state).isEqualTo("abcd")
    }

    @Test
    fun syncExampleVMNoStore() {
        val vm = MyViewModel()

        val newState = vm.simpleAction("abc")

        assert(newState).isEqualTo("abcd")
    }

    @Test
    fun asyncExampleVM() {
        val vm = MyViewModel()

        val store = Store("abc")

        runBlocking {
            store.execute2(vm::asyncAction)

            assert(store.state).isEqualTo("done")
        }
    }

    @Test
    fun asyncExampleVMNoStore() {
        val vm = MyViewModel()

        runBlocking {
            val states = vm.asyncAction("initial").toList()

            assert(states).containsExactly("loading", "done")
        }
    }

    @Test
    fun asyncExampleVMactions() {
        val vm = MyViewModel()

        val store = Store("abc")

        runBlocking {
            store.execute3(vm.asyncAction2(store.state))
        }
        assert(store.state).isEqualTo("abc loading done")
    }

    @Test
    fun asyncExampleVMNoStoreactions() {
        val vm = MyViewModel()

        val states: List<Action<String>> = runBlocking {
            vm.asyncAction2("abc").toList()
        }
        assert(states.map { it("abc") })
                .containsExactly("abc loading", "abc done")
    }
}