package it.codingjam.github.ui.repo

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import it.codingjam.github.ViewLibModule
import it.codingjam.github.espresso.TestComponent
import it.codingjam.github.espresso.TestComponentBuilder
import it.codingjam.github.testdata.TestAppModule
import it.codingjam.github.ui.search.SearchModule
import javax.inject.Singleton

@Singleton
@Component(modules = [TestAppModule::class, ViewLibModule::class, AndroidSupportInjectionModule::class, SearchModule::class])
interface SearchTestComponent : TestComponent {

    @Component.Builder
    interface Builder : TestComponentBuilder {

        fun testAppModule(testAppModule: TestAppModule): Builder

        fun viewLibModule(viewLibModule: ViewLibModule): Builder

        fun build(): SearchTestComponent
    }
}