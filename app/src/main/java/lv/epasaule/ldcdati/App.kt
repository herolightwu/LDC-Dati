package lv.epasaule.ldcdati

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import lv.epasaule.ldcdati.session.SessionDataSource
import lv.epasaule.ldcdati.session.SessionStore
import lv.epasaule.ldcdati.session.UserSession

class App : Application() {

    lateinit var userSession: UserSession
        private set

    override fun onCreate() {
        super.onCreate()
        userSession = UserSession(
                { SessionStore(it) },
                SessionDataSource(applicationContext.sharedPreferences, objectMapper)
        )
    }
}

val Context.sharedPreferences: SharedPreferences get() =
    getSharedPreferences(packageName, MODE_PRIVATE)

private val objectMapper get() = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
