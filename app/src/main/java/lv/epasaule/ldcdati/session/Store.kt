package lv.epasaule.ldcdati.session

import lv.epasaule.ldcdati.util.Opt
import lv.epasaule.ldcdati.util.opt
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.Subject

abstract class Store<T>(getInitialValue: (() -> T?)? = null) {

    protected open val storeSubject: Subject<Lazy<T?>, Lazy<T?>> =
            (getInitialValue
                    ?.let { BehaviorSubject.create(lazy { it.invoke() }) }
                    ?: BehaviorSubject.create()
                    )
                    .toSerialized()

    /**
     * Initial value is calculated once on a subscription thread
     */
    fun observe(): Observable<Opt<T?>> = storeSubject
            .asObservable()
            .map { it.value.opt }

    fun publish(value: T?) = storeSubject.onNext(lazyOf(value))

}