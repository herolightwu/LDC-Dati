package lv.epasaule.ldcdati.session


class SessionStore(getInitialValue: () -> SessionData?) : Store<SessionData>(getInitialValue)