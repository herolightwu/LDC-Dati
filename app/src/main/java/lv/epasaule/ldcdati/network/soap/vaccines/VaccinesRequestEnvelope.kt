package lv.epasaule.ldcdati.network.soap.vaccines

import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesRequestEnvelope.Body.GetVakcinas
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesRequestEnvelope.Header
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
data class VaccinesRequestEnvelope(
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
            var action: String = "http://tempuri.org/IMobileService/GetVakcinas"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:GetVakcinas") @param:Element(name = "tem:GetVakcinas")
            var getVakcinas: GetVakcinas
    ) {
        @Root(name = "tem:GetVakcinas", strict = true)
        data class GetVakcinas(
                @field:Element(name = "tem:sessionId") @param:Element(name = "tem:sessionId")
                var sessionId: String
        )
    }

}

fun createVaccinesRequestEnvelope(sessionId: String) =
        VaccinesRequestEnvelope(
                Header(),
                Body(GetVakcinas(sessionId))
        )


/*
--> POST https://muvis2.ldc.gov.lv/muvis/WebServices/MobileService.svc
Content-Type: application/soap+xml;charset=UTF-8;action="http://tempuri.org/IMobileService/GetVakcinas"
Content-Length: 431
Accept-Encoding: gzip,deflate
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/">
   <soap:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:Action>http://tempuri.org/IMobileService/GetVakcinas</wsa:Action>
   </soap:Header>
   <soap:Body>
      <tem:GetVakcinas>
         <tem:sessionId>0E5D95CA8DC74781A490DD91C66F05EF</tem:sessionId>
      </tem:GetVakcinas>
   </soap:Body>
</soap:Envelope>
--> END POST (431-byte body)
 */