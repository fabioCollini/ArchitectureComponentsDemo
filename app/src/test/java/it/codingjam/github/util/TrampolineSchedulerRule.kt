package it.codingjam.github.util

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TrampolineSchedulerRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    override fun finished(description: Description?) {
        super.finished(description)
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}