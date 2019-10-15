package lv.epasaule.ldcdati.network.soap.login

import lv.epasaule.ldcdati.network.soap.login.LoginRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.login.LoginRequestEnvelope.Body.Login
import lv.epasaule.ldcdati.network.soap.login.LoginRequestEnvelope.Header
import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Root


@Root(name = "soap:Envelope", strict = true)
@NamespaceList(
        Namespace(reference = "http://www.w3.org/2003/05/soap-envelope", prefix = "soap"),
        Namespace(reference = "http://tempuri.org/", prefix = "tem")
)
//@Order(elements = ["soap:Envelope/soap:Header", "soap:Envelope/soap:Body"])
data class LoginRequestEnvelope(
        @field:Element(name = "soap:Header") @param:Element(name = "soap:Header")
        var aHeader: Header,

        @field:Element(name = "soap:Body") @param:Element(name = "soap:Body")
        var bBody: Body
) {
    @Root(name = "soap:Header", strict = true)
    @NamespaceList(
            Namespace(reference = "http://www.w3.org/2005/08/addressing", prefix = "wsa")
    )
    data class Header(
            @field:Element(name = "wsa:Action") @param:Element(name = "wsa:Action")
            var action: String = "http://tempuri.org/IMobileService/Login"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:Login") @param:Element(name = "tem:Login")
            var login: Login
    ) {
        @Root(name = "tem:Login", strict = true)
        data class Login(
                @field:Element(name = "tem:login") @param:Element(name = "tem:login")
                var login: String,

                @field:Element(name = "tem:password") @param:Element(name = "tem:password")
                var password: String
        )
    }

}

fun createLoginRequestEnvelope(login: String, password: String) =
        LoginRequestEnvelope(
                Header(),
                Body(Login(login, password))
        )


/*
<soap:Envelope
        xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
        xmlns:tem="http://tempuri.org/">
        <soap:Header
                xmlns:wsa="http://www.w3.org/2005/08/addressing">
                <wsa:Action>http://tempuri.org/IMobileService/Login</wsa:Action>
        </soap:Header>
        <soap:Body>
                <tem:Login>
                        <tem:login>canc885</tem:login>
                        <tem:password>LDCdatiTEST1!</tem:password>
                </tem:Login>
        </soap:Body>
</soap:Envelope>
*/