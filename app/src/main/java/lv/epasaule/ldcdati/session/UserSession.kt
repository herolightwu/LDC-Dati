package lv.epasaule.ldcdati.session

import lv.epasaule.ldcdati.util.Opt
import lv.epasaule.ldcdati.util.opt
import rx.Observable


class UserSession(
        getStore: (() -> SessionData?) -> SessionStore,
        private val dataSource: SessionDataSource
) {

    private val store: SessionStore = getStore.invoke(::initialValue)

    fun sessionData(): Observable<Opt<SessionData?>> = store.observe()

    fun sessionId(): Observable<Opt<String?>> = sessionData()
            .map { it.value?.sessionId.opt }

    fun updateSession(sessionData: SessionData?): Observable<Unit> =
            Observable.fromCallable { setSessionData(sessionData) }

    @Synchronized private fun initialValue() = dataSource.sessionData

    @Synchronized private fun setSessionData(sessionData: SessionData?) {
        dataSource.setSessionData(sessionData)
        store.publish(sessionData)
    }

}