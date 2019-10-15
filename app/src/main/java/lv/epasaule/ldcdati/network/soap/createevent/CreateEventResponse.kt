package lv.epasaule.ldcdati.network.soap.createevent

import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class CreateEventResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {
    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "IzveidotNotikumuResponse", required = false)
            @param:Element(name = "IzveidotNotikumuResponse", required = false)
            var izveidotNotikumuResponse: IzveidotNotikumuResponse?
    ) {
        data class IzveidotNotikumuResponse(
                @field:Element(name = "IzveidotNotikumuResult")
                @param:Element(name = "IzveidotNotikumuResult")
                var izveidotNotikumuResult: String
        )
    }

}

val Throwable.createEventResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(CreateEventResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)

/*
<-- 200 OK https://muvis2.ldc.gov.lv/muvis/WebServices/MobileService.svc (1859ms)
Server: nginx
Date: Tue, 22 Oct 2019 18:12:28 GMT
Content-Type: application/soap+xml; charset=utf-8
Content-Length: 397
Connection: keep-alive
Cache-Control: private
X-Frame-Options: SAMEORIGIN
<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing"><s:Header><a:Action s:mustUnderstand="1">http://tempuri.org/IMobileService/IzveidotNotikumuResponse</a:Action></s:Header><s:Body><IzveidotNotikumuResponse xmlns="http://tempuri.org/"><IzveidotNotikumuResult>Atrasts</IzveidotNotikumuResult></IzveidotNotikumuResponse></s:Body></s:Envelope>
 */