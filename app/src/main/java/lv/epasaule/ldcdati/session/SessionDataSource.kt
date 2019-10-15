package lv.epasaule.ldcdati.session

import android.content.SharedPreferences
import androidx.core.content.edit
import com.fasterxml.jackson.databind.ObjectMapper


private const val KEY_SESSION_DATA = "sessionData"

class SessionDataSource(
        private val sharedPreferences: SharedPreferences,
        private val objectMapper: ObjectMapper
) {

    val sessionData: SessionData? get() =
        sharedPreferences.getString(KEY_SESSION_DATA, null)?.let {
            objectMapper.readValue(it, SessionData::class.java)
        }

    fun setSessionData(sessionData: SessionData?) {
        sharedPreferences.edit {
            putString(
                    KEY_SESSION_DATA,
                    sessionData?.let { objectMapper.writeValueAsString(it) }
            )
        }
    }

}