package it.codingjam.github.ui.common

import android.arch.lifecycle.ViewModel
import it.codingjam.github.util.UiActionsLiveData
import it.codingjam.github.util.ViewStateLiveData

open class RxViewModel<VS>(initialState: VS) : ViewModel() {

    val liveData = ViewStateLiveData(initialState)

    protected var state: VS by liveData

    val uiActions = UiActionsLiveData()
}