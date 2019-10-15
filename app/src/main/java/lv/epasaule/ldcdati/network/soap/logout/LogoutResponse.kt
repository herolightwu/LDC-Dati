package lv.epasaule.ldcdati.network.soap.logout

import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class LogoutResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {
    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "LogoutResponse", required = false)
            @param:Element(name = "LogoutResponse", required = false)
            var logoutResponse: LogoutResponse?
    ) {
        data class LogoutResponse(
                @field:Element(name = "LogoutResult") @param:Element(name = "LogoutResult")
                var logoutResult: Boolean
        )
    }

}

val Throwable.logoutResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(LogoutResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)

/*
<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing">
   <s:Header>
      <a:Action s:mustUnderstand="1">http://tempuri.org/IMobileService/LogoutResponse</a:Action>
   </s:Header>
   <s:Body>
      <LogoutResponse xmlns="http://tempuri.org/">
            <LogoutResult>true</LogoutResult>
      </LogoutResponse>
      <s:Fault>
         <s:Code>
            <s:Value>s:ERROR_SESSIONNOTFOUND</s:Value>
         </s:Code>
         <s:Reason>
            <s:Text xml:lang="lv">Sessija ar identifikatoru '?' nav atrasta!</s:Text>
         </s:Reason>
      </s:Fault>
   </s:Body>
</s:Envelope>
*/