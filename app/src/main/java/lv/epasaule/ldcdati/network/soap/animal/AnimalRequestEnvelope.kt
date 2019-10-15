package lv.epasaule.ldcdati.network.soap.animal

import lv.epasaule.ldcdati.network.soap.animal.AnimalRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.animal.AnimalRequestEnvelope.Body.GetDzivniekaDati
import lv.epasaule.ldcdati.network.soap.animal.AnimalRequestEnvelope.Header
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
data class AnimalRequestEnvelope(
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
            var action: String = "http://tempuri.org/IMobileService/GetDzivniekaDati"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:GetDzivniekaDati") @param:Element(name = "tem:GetDzivniekaDati")
            var getDzivniekaDati: GetDzivniekaDati
    ) {
        @Root(name = "tem:GetDzivniekaDati", strict = true)
        data class GetDzivniekaDati(
                @field:Element(name = "tem:sessionId") @param:Element(name = "tem:sessionId")
                var aSessionId: String,

                @field:Element(name = "tem:identifikators") @param:Element(name = "tem:identifikators")
                var bAnimalId: String
        )
    }

}

fun createAnimalRequestEnvelope(sessionId: String, animalId: String) =
        AnimalRequestEnvelope(
                Header(),
                Body(GetDzivniekaDati(sessionId, animalId))
        )


/*
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/">
    <soap:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
    <wsa:Action>http://tempuri.org/IMobileService/GetDzivniekaDati</wsa:Action></soap:Header>
    <soap:Body>
        <tem:GetDzivniekaDati>
            <tem:sessionId>2DAC7C73701348BEA8C6FD5FBF0A16BF</tem:sessionId>
            <tem:identifikators>900000000000132</tem:identifikators>
        </tem:GetDzivniekaDati>
    </soap:Body>
</soap:Envelope>
*/