package it.codingjam.github.ui.user

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import it.codingjam.github.FeatureAppScope
import it.codingjam.github.ViewLibComponent
import it.codingjam.github.core.CoreComponent
import it.codingjam.github.core.coreComponent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.getOrCreate
import it.codingjam.github.viewLibComponent

@FeatureAppScope
@Component(dependencies = [ViewLibComponent::class, CoreComponent::class])
interface UserAppComponent {
    fun fragmentComponent(): UserFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(core: CoreComponent, viewLib: ViewLibComponent): UserAppComponent
    }
}

val Application.userComponent
    get() = (this as ComponentHolder).getOrCreate {
        DaggerUserAppComponent.factory()
                .create(coreComponent, viewLibComponent)
    }

@Subcomponent
interface UserFragmentComponent {
    fun inject(fragment: UserFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance param: String): UserFragmentComponent
    }
}