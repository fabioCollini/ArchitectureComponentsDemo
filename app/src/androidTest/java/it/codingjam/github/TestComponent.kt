package it.codingjam.github

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import it.codingjam.github.espresso.TestAndroidInjectorActivityBindingModule
import it.codingjam.github.espresso.TestApplication
import it.codingjam.github.ui.repo.RepoModule
import javax.inject.Singleton


@Singleton
@Component(modules = [TestAppModule::class, ViewLibModule::class, AndroidSupportInjectionModule::class, TestAndroidInjectorActivityBindingModule::class, RepoModule::class])
interface TestComponent {

    fun inject(app: TestApplication)

    @Component.Builder interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun viewLibModule(viewLibModule: ViewLibModule): Builder

        fun build(): TestComponent
    }
}