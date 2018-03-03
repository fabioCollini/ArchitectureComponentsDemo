package it.codingjam.github.espresso

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import it.codingjam.github.util.ViewModelFactory
import org.mockito.Mockito
import javax.inject.Provider
import kotlin.reflect.KClass

class TestViewModelFactory<out VM : ViewModel>(kclass: KClass<VM>) : ViewModelFactory {

    val viewModel = Mockito.mock(kclass.java)

    override fun <VM : ViewModel> invoke(fragment: Fragment, provider: Provider<VM>): VM {
        return viewModel as VM
    }

    companion object {
        inline fun <reified VM : ViewModel> create(): ViewModelFactory = TestViewModelFactory(VM::class)
    }
}


fun <VM : ViewModel> ViewModelFactory.viewModel(): VM = (this as TestViewModelFactory<VM>).viewModel

