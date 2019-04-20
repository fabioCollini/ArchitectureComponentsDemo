package it.codingjam.github.ui.search

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
interface SearchAppComponent {
    fun fragmentComponent(): SearchFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(core: CoreComponent, viewLib: ViewLibComponent): SearchAppComponent
    }
}

val Application.searchComponent
    get() = (this as ComponentHolder).getOrCreate {
        DaggerSearchAppComponent.factory()
                .create(coreComponent, viewLibComponent)
    }

@Subcomponent
interface SearchFragmentComponent {
    fun inject(fragment: SearchFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: SearchFragment): SearchFragmentComponent
    }
}
