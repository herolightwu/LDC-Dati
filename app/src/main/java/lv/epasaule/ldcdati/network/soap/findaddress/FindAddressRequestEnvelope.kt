package lv.epasaule.ldcdati.network.soap.findaddress

import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressRequestEnvelope.Body.MekletAdresi
import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressRequestEnvelope.Header
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
data class FindAddressRequestEnvelope(
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
            var action: String = "http://tempuri.org/IMobileService/MekletAdresi"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:MekletAdresi") @param:Element(name = "tem:MekletAdresi")
            var mekletAdresi: MekletAdresi
    ) {
        @Root(name = "tem:MekletAdresi", strict = true)
        data class MekletAdresi(
                @field:Element(name = "tem:sessionId") @param:Element(name = "tem:sessionId")
                var sessionId: String,

                @field:Element(name = "tem:teksts") @param:Element(name = "tem:teksts")
                var teksts: String
        )
    }

}

fun createFindAddressRequestEnvelope(sessionId: String, address: String) =
        FindAddressRequestEnvelope(
                Header(),
                Body(MekletAdresi(sessionId, address))
        )


/*
--> POST https://muvis2.ldc.gov.lv/muvis/WebServices/MobileService.svc
Content-Type: application/soap+xml;charset=UTF-8;action="http://tempuri.org/IMobileService/MekletAdresi"
Content-Length: 519
Accept-Encoding: gzip,deflate
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/">
   <soap:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:Action>http://tempuri.org/IMobileService/MekletAdresi</wsa:Action>
   </soap:Header>
   <soap:Body>
      <tem:MekletAdresi>
         <tem:sessionId>3DA2970B4E014226A1C1C2D2AED0F9F1</tem:sessionId>
         <tem:teksts>Starta 40</tem:teksts>
      </tem:MekletAdresi>
   </soap:Body>
</soap:Envelope>
--> END POST (431-byte body)
 */