package it.codingjam.github.util

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

class UiScheduler(private val context: CoroutineContext = Unconfined) {
    private val job = Job()

    operator fun invoke(f: suspend () -> Unit) {
        async(job + context) { f() }
    }

    fun cancel() {
        job.cancel()
    }
}