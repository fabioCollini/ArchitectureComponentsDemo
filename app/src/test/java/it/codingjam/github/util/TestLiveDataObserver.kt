package it.codingjam.github.util

import java.util.*

class TestLiveDataObserver<T> {

    private val values = ArrayList<T>()

    fun onChanged(value: T) {
        values.add(value)
    }

    fun getValues(): List<T> {
        return values
    }
}
