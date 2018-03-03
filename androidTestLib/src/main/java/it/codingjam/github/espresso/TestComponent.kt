package it.codingjam.github.espresso

import android.app.Application
import dagger.BindsInstance

interface TestComponent {
    fun inject(app: TestApplication)
}

interface TestComponentBuilder {
    @BindsInstance fun application(application: Application)
}