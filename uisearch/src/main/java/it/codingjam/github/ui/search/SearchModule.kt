package it.codingjam.github.ui.search

import android.app.Application
import androidx.fragment.app.Fragment
import dagger.Component
import it.codingjam.github.*
import it.codingjam.github.core.CoreComponent
import it.codingjam.github.core.coreComponent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.getOrCreate

@FeatureAppScope
@Component(dependencies = [ViewLibComponent::class, CoreComponent::class])
interface SearchAppComponent: ViewLibComponent {
    val searchUseCase: SearchUseCase

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

@FeatureFragmentScope
@Component(dependencies = [SearchAppComponent::class])
interface SearchFragmentComponent {
    val viewModel: SearchViewModel

    @Component.Factory
    interface Factory {
        fun create(appComponent: SearchAppComponent): SearchFragmentComponent
    }
}

val Fragment.searchFragmentComponent: SearchFragmentComponent
    get() = getOrCreateFragmentComponent {
        DaggerSearchFragmentComponent.factory()
                .create(requireActivity().application.searchComponent)
    }