package it.codingjam.github.util

import android.os.AsyncTask
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

interface Coroutines {
    operator fun invoke(f: suspend CoroutineScope.() -> Unit)

    fun cancel()

    suspend fun onUi(f: suspend CoroutineScope.() -> Unit)
}

class AndroidCoroutines : Coroutines, CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(block = f)
    }

    override fun cancel() {
        job.cancel()
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.Main, block = f)
    }
}

class TestCoroutines : Coroutines {
    override fun invoke(f: suspend CoroutineScope.() -> Unit) {
        runBlocking { f() }
    }

    override fun cancel() {
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        runBlocking { f() }
    }
}

class AndroidTestCoroutines : Coroutines, CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private val bgContext = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    override operator fun invoke(f: suspend CoroutineScope.() -> Unit) {
        launch(job + bgContext, block = f)
    }

    override fun cancel() {
        job.cancel()
    }

    override suspend fun onUi(f: suspend CoroutineScope.() -> Unit) {
        withContext(Dispatchers.Main, block = f)
    }
}