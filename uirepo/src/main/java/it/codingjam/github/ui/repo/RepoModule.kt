package it.codingjam.github.ui.repo

import android.app.Application
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import it.codingjam.github.*
import it.codingjam.github.core.CoreComponent
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.coreComponent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.ui.repo.RepoFragment.Companion.param

@FeatureAppScope
@Component(dependencies = [ViewLibComponent::class, CoreComponent::class])
interface RepoAppComponent : ViewLibComponent {
    val repoUseCase: RepoUseCase

    @Component.Factory
    interface Factory {
        fun create(core: CoreComponent, viewLib: ViewLibComponent): RepoAppComponent
    }
}

val Application.repoComponent
    get() = getOrCreate {
        DaggerRepoAppComponent.factory()
                .create((this as ComponentHolder).coreComponent, viewLibComponent)
    }

@FeatureFragmentScope
@Component(dependencies = [RepoAppComponent::class])
interface RepoFragmentComponent {
    val viewModel: RepoViewModel

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance param: RepoId, repoAppComponent: RepoAppComponent): RepoFragmentComponent
    }
}

val Fragment.repoFragmentComponent: RepoFragmentComponent
    get() = getOrCreateFragmentComponent {
        DaggerRepoFragmentComponent.factory()
                .create(param, requireActivity().application.repoComponent)
    }