package it.codingjam.github

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import it.codingjam.github.api.ApiModule
import it.codingjam.github.di.AndroidInjectorActivityBindingModule
import it.codingjam.github.di.AppComponent
import it.codingjam.github.di.AppModule
import it.codingjam.github.espresso.TestAndroidInjectorActivityBindingModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, ApiModule::class, AndroidInjectorActivityBindingModule::class, AndroidSupportInjectionModule::class, TestModule::class, TestAndroidInjectorActivityBindingModule::class])
interface TestComponent : AppComponent {

    @Component.Builder interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun testModule(testModule: TestModule): Builder

        fun build(): TestComponent
    }
}