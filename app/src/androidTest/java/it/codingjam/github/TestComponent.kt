package it.codingjam.github

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.codingjam.github.di.AppComponent
import it.codingjam.github.di.AppModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, TestModule::class])
interface TestComponent : AppComponent {

    @Component.Builder interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun testModule(testModule: TestModule): Builder

        fun build(): TestComponent
    }
}