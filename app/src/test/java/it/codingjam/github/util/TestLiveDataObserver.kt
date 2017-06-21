package it.codingjam.github.util

class TestLiveDataObserver<T>: (T) -> Unit {

    val values = mutableListOf<T>()

    override fun invoke(value: T) {
        values.add(value)
    }
}
