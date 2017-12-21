package it.codingjam.github


import android.support.test.InstrumentationRegistry
import it.codingjam.github.di.AppModule
import it.cosenonjaviste.daggermock.DaggerMock

fun gitHubDaggerMockRule() = DaggerMock.rule<TestComponent>(AppModule(), TestModule()) {
    set { app.component = it }
    customizeBuilder<TestComponent.Builder> { it.application(app) }
}

private val app: GithubApp
    get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GithubApp
