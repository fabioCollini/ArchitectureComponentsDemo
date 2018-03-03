package it.codingjam.github

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import it.codingjam.github.espresso.TestAndroidInjectorActivityBindingModule
import it.codingjam.github.espresso.TestComponent
import it.codingjam.github.espresso.TestComponentBuilder
import it.codingjam.github.testdata.TestAppModule
import it.codingjam.github.ui.repo.RepoModule
import javax.inject.Singleton

@Singleton
@Component(modules = [TestAppModule::class, ViewLibModule::class, AndroidSupportInjectionModule::class, TestAndroidInjectorActivityBindingModule::class, RepoModule::class])
interface RepoTestComponent : TestComponent {

    @Component.Builder
    interface Builder : TestComponentBuilder {

        fun viewLibModule(viewLibModule: ViewLibModule): Builder

        fun build(): RepoTestComponent
    }
}