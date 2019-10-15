package lv.epasaule.ldcdati.network.soap.vaccines

import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class VaccinesResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {
    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "GetVakcinasResponse", required = false)
            @param:Element(name = "GetVakcinasResponse", required = false)
            var getVakcinasResponse: GetVakcinasResponse?
    ) {
        data class GetVakcinasResponse(
                @field:Element(name = "GetVakcinasResult")
                @param:Element(name = "GetVakcinasResult")
                var getVakcinasResult: GetVakcinasResult
        ) {
            data class GetVakcinasResult(
                    @field:ElementList(entry = "VakcinasDati", inline = true, required = false)
                    @param:ElementList(entry = "VakcinasDati", inline = true, required = false)
                    var vakcinasDatiList: List<VakcinasDati>? = null
            ) {
                @Root(name = "VakcinasDati")
                data class VakcinasDati(
                        @field:Element(name = "Id", required = false)
                        @param:Element(name = "Id", required = false)
                        var id: String?,

                        @field:Element(name = "Nosaukums", required = false)
                        @param:Element(name = "Nosaukums", required = false)
                        var nosaukums: String?
                )
            }
        }
    }

}

val Throwable.vaccinesResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(VaccinesResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)


/*
<-- 200 OK https://muvis2.ldc.gov.lv/muvis/WebServices/MobileService.svc (122ms)
Server: nginx
Date: Tue, 22 Oct 2019 18:07:47 GMT
Content-Type: application/soap+xml; charset=utf-8
Content-Length: 748
Connection: keep-alive
Cache-Control: private
X-Frame-Options: SAMEORIGIN
<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing"><s:Header><a:Action s:mustUnderstand="1">http://tempuri.org/IMobileService/GetVakcinasResponse</a:Action></s:Header><s:Body><GetVakcinasResponse xmlns="http://tempuri.org/"><GetVakcinasResult xmlns:b="http://schemas.datacontract.org/2004/07/Muvis.WebServices.MobileServiceActions" xmlns:i="http://www.w3.org/2001/XMLSchema-instance"><b:VakcinasDati><b:Id>60b1b48a-1fe1-475b-8ee0-a01400bc7f81</b:Id><b:Nosaukums>Pret trakumsērgu</b:Nosaukums></b:VakcinasDati><b:VakcinasDati><b:Id>19fe9c99-de65-41d7-b7c1-a7f4008a2ce2</b:Id><b:Nosaukums>Antivir</b:Nosaukums></b:VakcinasDati></GetVakcinasResult></GetVakcinasResponse></s:Body></s:Envelope>
 */