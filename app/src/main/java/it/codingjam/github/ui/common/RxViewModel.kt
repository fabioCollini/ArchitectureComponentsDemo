package it.codingjam.github.ui.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
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

    override fun onCleared() = clearedSubject.onNext(true)

    fun observeUiActions(owner: LifecycleOwner, executor: ((FragmentActivity) -> Unit) -> Unit) {
        uiActions.observe(owner, executor)
    }

    fun observeState(owner: LifecycleOwner, observer: (VS) -> Unit) {
        state.observe(owner, observer)
    }

    fun observeUiActionsForever(executor: ((FragmentActivity) -> Unit) -> Unit) {
        uiActions.observeForever(executor)
    }

    fun observeStateForever(observer: (VS) -> Unit) {
        state.observeForever(observer)
    }
}