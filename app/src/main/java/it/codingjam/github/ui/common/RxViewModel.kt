package it.codingjam.github.ui.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewStateLiveData
import org.reactivestreams.Publisher

open class RxViewModel<VS>(initialState: VS) : ViewModel() {

    private val clearedSubject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    protected val cleared: Publisher<Boolean> = clearedSubject.toFlowable(BackpressureStrategy.BUFFER)

    protected val state = ViewStateLiveData(initialState)

    protected val uiActions = UiActionsLiveData()

    override fun onCleared() {
        clearedSubject.onNext(true)
    }

    fun <F> observe(owner: F, observer: (VS) -> Unit) where F : Fragment, F : LifecycleOwner {
        state.observe(owner, Observer { observer(it!!) })
        uiActions.observe(owner)
    }

    fun <A> observe(owner: A, observer: (VS) -> Unit) where A : FragmentActivity, A : LifecycleOwner {
        state.observe(owner, Observer { observer(it!!) })
        uiActions.observe(owner)
    }

    fun observeForever(activity: FragmentActivity, observer: (VS) -> Unit) {
        state.observeForever { observer(it!!) }
        uiActions.observeForever(activity)
    }
}