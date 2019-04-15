package it.codingjam.github.util

sealed class Action<out T>

class StateAction<T>(private val f: T.() -> T) : Action<T>() {
    operator fun invoke(t: T) = t.f()
}

abstract class Signal : Action<Nothing>()
