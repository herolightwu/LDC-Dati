package lv.epasaule.ldcdati.network.soap.logout

import lv.epasaule.ldcdati.network.soap.logout.LogoutRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.logout.LogoutRequestEnvelope.Body.Logout
import lv.epasaule.ldcdati.network.soap.logout.LogoutRequestEnvelope.Header
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
data class LogoutRequestEnvelope(
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
            var action: String = "http://tempuri.org/IMobileService/Logout"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:Logout") @param:Element(name = "tem:Logout")
            var logout: Logout
    ) {
        @Root(name = "tem:Logout", strict = true)
        data class Logout(
                @field:Element(name = "tem:sessionId") @param:Element(name = "tem:sessionId")
                var sessionId: String
        )
    }

}

fun createLogoutRequestEnvelope(sessionId: String) =
        LogoutRequestEnvelope(
                Header(),
                Body(Logout(sessionId))
        )


/*
<soap:Envelope
        xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
        xmlns:tem="http://tempuri.org/">
        <soap:Header
                xmlns:wsa="http://www.w3.org/2005/08/addressing">
                <wsa:Action>http://tempuri.org/IMobileService/Logout</wsa:Action>
        </soap:Header>
        <soap:Body>
                <tem:Logout>
                        <tem:logout>canc885</tem:logout>
                        <tem:password>LDCdatiTEST1!</tem:password>
                </tem:Logout>
        </soap:Body>
</soap:Envelope>
*/