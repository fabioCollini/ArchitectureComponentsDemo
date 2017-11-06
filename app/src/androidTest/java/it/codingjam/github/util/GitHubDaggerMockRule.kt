package it.codingjam.github.util


import android.support.test.InstrumentationRegistry
import it.codingjam.github.GithubApp
import it.codingjam.github.di.AppComponent
import it.codingjam.github.di.AppModule
import it.cosenonjaviste.daggermock.DaggerMock

fun gitHubDaggerMockRule() = DaggerMock.rule<AppComponent>(AppModule(app)) {
    set { app.component = it }
}

private val app: GithubApp
    get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GithubApp
