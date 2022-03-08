package xyz.flussigkatz.spoonzilla.util

import androidx.lifecycle.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class AutoDisposable : LifecycleEventObserver {
    lateinit var compositeDisposable: CompositeDisposable

    fun bindTo(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        compositeDisposable = CompositeDisposable()
    }

    fun add(disposable: Disposable) {
        if(::compositeDisposable.isInitialized) {
            compositeDisposable.add(disposable)
        } else {
            throw NotImplementedError("must bind AutoDisposable to a Lifecycle first")
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) compositeDisposable.dispose()
    }

}

fun Disposable.addTo(autoDisposable: AutoDisposable) {
    autoDisposable.add(this)
}