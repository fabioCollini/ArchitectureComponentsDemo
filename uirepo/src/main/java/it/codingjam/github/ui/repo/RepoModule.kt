package it.codingjam.github.ui.repo

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.codingjam.github.FeatureAppScope
import it.codingjam.github.FeatureFragmentScope
import it.codingjam.github.ViewLibComponent
import it.codingjam.github.core.CoreComponent
import it.codingjam.github.core.RepoId
import it.codingjam.github.core.coreComponent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.getOrCreate
import it.codingjam.github.viewLibComponent

@FeatureAppScope
@Component(dependencies = [ViewLibComponent::class, CoreComponent::class])
interface RepoAppComponent {
    val repoUseCase: RepoUseCase

    @Component.Factory
    interface Factory {
        fun create(core: CoreComponent, viewLib: ViewLibComponent): RepoAppComponent
    }
}

val Application.repoComponent
    get() = (this as ComponentHolder).getOrCreate {
        DaggerRepoAppComponent.factory()
                .create(coreComponent, viewLibComponent)
    }

@FeatureFragmentScope
@Component(dependencies = [ViewLibComponent::class, RepoAppComponent::class])
interface RepoFragmentComponent {
    fun inject(fragment: RepoFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance param: RepoId, repoAppComponent: RepoAppComponent, viewLib: ViewLibComponent): RepoFragmentComponent
    }
}