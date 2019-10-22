package lv.epasaule.ldcdati.network.soap.createevent

import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Body
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Body.IzveidotNotikumu
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Body.IzveidotNotikumu.Notikums
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Body.IzveidotNotikumu.Notikums.NotikumaDati
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Body.IzveidotNotikumu.Notikums.NotikumaDati.NotikumaVertiba
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope.Header
import lv.epasaule.ldcdati.util.isoDateStr
import org.simpleframework.xml.*


@Root(name = "soap:Envelope", strict = true)
@NamespaceList(
        Namespace(reference = "http://www.w3.org/2003/05/soap-envelope", prefix = "soap"),
        Namespace(reference = "http://tempuri.org/", prefix = "tem"),
        Namespace(reference = "http://schemas.datacontract.org/2004/07/Muvis.WebServices.MobileServiceActions", prefix = "muv")
)
//@Order(elements = ["soap:Envelope/soap:Header", "soap:Envelope/soap:Body"])
data class CreateEventRequestEnvelope(
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
            var action: String = "http://tempuri.org/IMobileService/IzveidotNotikumu"
    )

    @Root(name = "soap:Body", strict = true)
    data class Body(
            @field:Element(name = "tem:IzveidotNotikumu")
            @param:Element(name = "tem:IzveidotNotikumu")
            var izveidotNotikumu: IzveidotNotikumu
    ) {
        @Root(name = "tem:IzveidotNotikumu", strict = true)
        //@Order(elements = ["tem:sessionId", "tem:notikums"])
        data class IzveidotNotikumu(
                @field:Element(name = "tem:sessionId") @param:Element(name = "tem:sessionId")
                var aSessionId: String,

                @field:Element(name = "tem:notikums") @param:Element(name = "tem:notikums")
                var bNotikums: Notikums
        ) {
            @Root(name = "tem:notikums", strict = true)
            data class Notikums(
                    @field:Element(name = "muv:Detalas", required = false)
                    @param:Element(name = "muv:Detalas", required = false)
                    var aDetalas: String?,

                    @field:Element(name = "muv:Identifikators")
                    @param:Element(name = "muv:Identifikators")
                    var bIdentifikators: String,

                    @field:Element(name = "muv:NotikumaDati", required = false)
                    @param:Element(name = "muv:NotikumaDati", required = false)
                    var cNotikumaDati: NotikumaDati?,

                    @field:Element(name = "muv:NotikumaDatums")
                    @param:Element(name = "muv:NotikumaDatums")
                    var dNotikumaDatums: String,

                    @field:Element(name = "muv:NotikumaVeids")
                    @param:Element(name = "muv:NotikumaVeids")
                    var eNotikumaVeids: Int
            ) {
                @Root(name = "muv:NotikumaDati", strict = true)
                data class NotikumaDati(
                        @field:ElementList(entry = "muv:NotikumaVertiba", inline = true)
                        @param:ElementList(entry = "muv:NotikumaVertiba", inline = true)
                        var notikumaVertibaList: List<NotikumaVertiba>
                ) {
                    @Root(name = "muv:NotikumaVertiba")
                    data class NotikumaVertiba(
                            @field:Element(name = "muv:Lauks") @param:Element(name = "muv:Lauks")
                            var aLauks: String,
                            @field:Element(name = "muv:Vertiba") @param:Element(name = "muv:Vertiba")
                            var bVertiba: String
                    )
                }
            }
        }
    }

}

fun createCreateEventRequestEnvelope(
        sessionId: String,
        animalId: String,
        eventDate: Long,
        event: Event,
        comments: String?
): CreateEventRequestEnvelope {
    val notikumaDati = when (event) {
        is DzivnieksIrBistams -> {
            val dati = listOfNotNull(
                    event.dangerEndDate?.let { NotikumaVertiba("beiguDatums", it.isoDateStr) }
            )
            if (dati.isNotEmpty()) NotikumaDati(dati) else null
        }
        is DzivnieksIrVakcinets -> {
            val dati = listOfNotNull(
                    NotikumaVertiba("vakcinasId", event.vaccineId),
                    NotikumaVertiba("sertifikats", event.certificateNumber),
                    event.nextDate?.let { NotikumaVertiba("nakosaisDatums", it.isoDateStr) }
            )
            if (dati.isNotEmpty()) NotikumaDati(dati) else null
        }
        is MainitaTuresanasAdrese -> {
            val dati = when (event.turetsArzemes) {
                is TrueTuretsArzemes -> listOf(
                        NotikumaVertiba("turetsArzemes", "true"),
                        NotikumaVertiba("valstsIsoKods", event.turetsArzemes.valstsIsoKods)
                )
                is FalseTuretsArzemes -> listOf(
                        NotikumaVertiba("turetsArzemes", "false"),
                        NotikumaVertiba("adresesKods", event.turetsArzemes.adresesKods)
                )
            } + listOfNotNull(
                    event.adresesPrecizejums?.let { NotikumaVertiba("adresesPrecizejums", it) }
            )
            if (dati.isNotEmpty()) NotikumaDati(dati) else null
        }
        else -> null
    }
    val notikums = Notikums(
            bIdentifikators = animalId,
            eNotikumaVeids = event.eventType.eventId,
            dNotikumaDatums = eventDate.isoDateStr,
            aDetalas = comments,
            cNotikumaDati = notikumaDati
    )

    return CreateEventRequestEnvelope(
            Header(),
            Body(IzveidotNotikumu(sessionId, notikums))
    )
}

/*
--> POST https://muvis2.ldc.gov.lv/muvis/WebServices/MobileService.svc
Content-Type: application/soap+xml;charset=UTF-8;action="http://tempuri.org/IMobileService/IzveidotNotikumu"
Content-Length: 822
Accept-Encoding: gzip,deflate
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/" xmlns:muv="http://schemas.datacontract.org/2004/07/Muvis.WebServices.MobileServiceActions">
   <soap:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
      <wsa:Action>http://tempuri.org/IMobileService/IzveidotNotikumu</wsa:Action>
   </soap:Header>
   <soap:Body>
      <tem:IzveidotNotikumu>
         <tem:sessionId>0E5D95CA8DC74781A490DD91C66F05EF</tem:sessionId>
         <tem:notikums>
            <muv:Detalas></muv:Detalas>
            <muv:Identifikators>900000000000066</muv:Identifikators>
            <muv:NotikumaDatums>2019-10-22T21:07:46</muv:NotikumaDatums>
            <muv:NotikumaVeids>14</muv:NotikumaVeids>
         </tem:notikums>
      </tem:IzveidotNotikumu>
   </soap:Body>
</soap:Envelope>
--> END POST (822-byte body)
 */