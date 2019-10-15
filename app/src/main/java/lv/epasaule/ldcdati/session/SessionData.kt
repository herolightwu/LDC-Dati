package lv.epasaule.ldcdati.session

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import lv.epasaule.ldcdati.network.soap.login.LoginResponseEnvelope.Body.LoginResponse.LoginResult


data class SessionData @JsonCreator constructor(
        @JsonProperty("certificateNumber") val certificateNumber: String?,
        @JsonProperty("personId") val personId: String?,
        @JsonProperty("personName") val personName: String?,
        @JsonProperty("sessionId") val sessionId: String,
        @JsonProperty("userRole") val userRole: String?
)

val LoginResult.sessionData get() = SessionData(
        certificateNumber, personId, personName, sessionId, userRole
)