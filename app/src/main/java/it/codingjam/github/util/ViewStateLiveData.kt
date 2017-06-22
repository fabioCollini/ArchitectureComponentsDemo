package it.codingjam.github.util


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.ObservableField
import android.support.annotation.MainThread
import io.reactivex.subjects.BehaviorSubject
import it.codingjam.github.ui.common.RxViewModel
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewStateLiveData<T>(initialState: T) {

    private val liveData = MutableLiveData<T>()

    private val subject = BehaviorSubject.create<T>()

    init {
        liveData.value = initialState
    }

    var value: T
        get() = liveData.value!!
        set(value) {
            liveData.value = value
            subject.onNext(value)
        }

    @MainThread inline fun update(updater: T.() -> T) {
        val currentState = value
        val newState = updater(currentState)
        value = newState
        Timber.d("%s", newState)
    }

    @MainThread inline fun <V> updateOnEvent(crossinline updater: T.(V) -> T): (V) -> Unit = {
        val currentState = value
        val newState = currentState.updater(it)
        value = newState
        Timber.d("%s", newState)
    }

    fun <F> bind(observableField: ObservableField<F>, getter: (T) -> F, setter: T.(F) -> T) {
        observableField.addOnPropertyChangedCallback(object : android.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: android.databinding.Observable, i: Int) {
                update {
                    if (getter(this) == observableField.get()) {
                        this
                    } else {
                        setter(observableField.get())
                    }
                }
            }
        })

        subject.map(getter).distinctUntilChanged().subscribe { observableField.set(it) }
    }

    fun <F> bind(getter: (T) -> F, setter: T.(F) -> T): ReadOnlyProperty<RxViewModel<*>, ObservableField<F>> =
            object : ReadOnlyProperty<RxViewModel<*>, ObservableField<F>> {
                private var observableField: ObservableField<F>? = null

                override fun getValue(thisRef: RxViewModel<*>, property: KProperty<*>): ObservableField<F> {
                    if (observableField == null) {
                        observableField = ObservableField()
                        bind(observableField!!, getter, setter)
                    }
                    return observableField!!
                }
            }

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            liveData.observe(owner, Observer { observer(it!!) })

    fun observeForever(observer: (T) -> Unit) {
        liveData.observeForever { observer(it!!) }
    }
}
