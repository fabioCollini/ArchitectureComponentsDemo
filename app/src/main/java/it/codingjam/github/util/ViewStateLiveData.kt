package it.codingjam.github.util


import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.support.annotation.MainThread
import io.reactivex.subjects.BehaviorSubject
import it.codingjam.github.ui.common.RxViewModel
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewStateLiveData<T>(initialState: T) : MutableLiveData<T>() {

    private val subject = BehaviorSubject.create<T>()

    init {
        value = initialState
    }

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
        subject.onNext(value)
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }

    @MainThread inline fun update(updater: T.() -> T) {
        val currentState = value
        val newState = updater.invoke(currentState)
        value = newState
        Timber.d("%s", newState)
    }

    @MainThread inline fun <V> updateOnEvent(crossinline updater: T.(V) -> T): (V) -> Unit = {
        val currentState = value
        val newState = updater.invoke(currentState, it)
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

    fun <F> bind(getter: (T) -> F, setter: T.(F) -> T) : ReadOnlyProperty<RxViewModel<*>, ObservableField<F>> =
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
}
