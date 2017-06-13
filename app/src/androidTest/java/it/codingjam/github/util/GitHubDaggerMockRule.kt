package it.codingjam.github.util


import android.support.test.InstrumentationRegistry

import it.codingjam.github.GithubApp
import it.codingjam.github.di.AppComponent
import it.codingjam.github.di.AppModule

import it.cosenonjaviste.daggermock.DaggerMockRule

class GitHubDaggerMockRule : DaggerMockRule<AppComponent>(AppComponent::class.java, AppModule()) {
    init {
        set { component -> app.component = component }
    }

    private val app: GithubApp
        get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as GithubApp
}
