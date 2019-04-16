package it.codingjam.github.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

sealed class Action<out T>

class StateAction<T>(private val f: T.() -> T) : Action<T>() {
    operator fun invoke(t: T) = t.f()
}

abstract class Signal : Action<Nothing>()

class ActionsFlowCollector<T>(private val innerCollector: (FlowCollector<Action<T>>)) {

    suspend fun emit(action: T.() -> T) =
            innerCollector.emit(StateAction(action))

    suspend fun emit(signal: Signal) =
            innerCollector.emit(signal)
}

fun <T : Any> actionsFlow(block: suspend ActionsFlowCollector<T>.() -> Unit): ActionsFlow<T> {
    return ActionsFlow(object : Flow<Action<T>> {
        override suspend fun collect(collector: FlowCollector<Action<T>>) {
            ActionsFlowCollector(collector).block()
        }
    })
}

private val EmptyActionsFlow = ActionsFlow<Nothing>(emptyFlow())

fun <T : Any> emptyActionsFlow(): ActionsFlow<T> = EmptyActionsFlow

class ActionsFlow<out T : Any>(private val flow: Flow<Action<T>>) : Flow<Action<T>> by flow

fun <T : Any, S : Any> ActionsFlow<T>.mapActions(copy: S.(StateAction<T>) -> S): ActionsFlow<S> =
        ActionsFlow(map { action: Action<T> -> action.map(copy) })

fun <R, S> Action<R>.map(copy: S.(StateAction<R>) -> S): Action<S> {
    return if (this is Signal) {
        this
    } else {
        val stateAction = this as StateAction<R>
        StateAction { copy(stateAction) }
    }
}