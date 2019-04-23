package it.codingjam.github.ui.user

import android.app.Application
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import it.codingjam.github.*
import it.codingjam.github.core.CoreComponent
import it.codingjam.github.core.coreComponent
import it.codingjam.github.core.utils.ComponentHolder
import it.codingjam.github.core.utils.getOrCreate

@FeatureAppScope
@Component(dependencies = [ViewLibComponent::class, CoreComponent::class])
interface UserAppComponent : ViewLibComponent {
    val userUseCase: UserUseCase

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

@FeatureFragmentScope
@Component(dependencies = [UserAppComponent::class])
interface UserFragmentComponent {
    val viewModel: UserViewModel

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance param: String, userAppComponent: UserAppComponent): UserFragmentComponent
    }
}

val Fragment.userFragmentComponent: UserFragmentComponent
    get() = getOrCreateFragmentComponent {
        DaggerUserFragmentComponent.factory()
                .create(UserFragment.param(this), requireActivity().application.userComponent)
    }