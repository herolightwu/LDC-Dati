package lv.epasaule.ldcdati.network.soap.login

import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class LoginResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {
    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "LoginResponse", required = false)
            @param:Element(name = "LoginResponse", required = false)
            var loginResponse: LoginResponse?
    ) {
        data class LoginResponse(
                @field:Element(name = "LoginResult") @param:Element(name = "LoginResult")
                var loginResult: LoginResult
        ) {
            data class LoginResult(
                    @field:Element(name = "CertificateNumber", required = false)
                    @param:Element(name = "CertificateNumber", required = false)
                    var certificateNumber: String?,

                    @field:Element(name = "PersonId", required = false)
                    @param:Element(name = "PersonId", required = false)
                    var personId: String?,

                    @field:Element(name = "PersonName", required = false)
                    @param:Element(name = "PersonName", required = false)
                    var personName: String?,

                    @field:Element(name = "SessionId")
                    @param:Element(name = "SessionId")
                    var sessionId: String,

                    @field:Element(name = "UserRole", required = false)
                    @param:Element(name = "UserRole", required = false)
                    var userRole: String?
            )
        }
    }

}

val Throwable.loginResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(LoginResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)


/*
<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing">
   <s:Header>
      <a:Action s:mustUnderstand="1">http://tempuri.org/IMobileService/LoginResponse</a:Action>
   </s:Header>
   <s:Body>
      <LoginResponse xmlns="http://tempuri.org/">
         <LoginResult xmlns:b="http://schemas.datacontract.org/2004/07/Muvis.WebServices.MobileServiceActions" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
            <b:CertificateNumber>V-0157-22</b:CertificateNumber>
            <b:PersonId>105645</b:PersonId>
            <b:PersonName>ATIS AIGARS</b:PersonName>
            <b:SessionId>3E8160AB3C04497BAE9B543ED0BA06B3</b:SessionId>
            <b:UserRole>2</b:UserRole>
         </LoginResult>
      </LoginResponse>
      <s:Fault>
         <s:Code>
            <s:Value>s:ERROR_GENERIC</s:Value>
         </s:Code>
         <s:Reason>
            <s:Text xml:lang="lv">Nepareizs lietotāja vārds vai parole!</s:Text>
         </s:Reason>
      </s:Fault>
   </s:Body>
</s:Envelope>
*/