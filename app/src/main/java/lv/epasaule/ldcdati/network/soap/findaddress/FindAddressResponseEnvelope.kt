package lv.epasaule.ldcdati.network.soap.findaddress

import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class FindAddressResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {
    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "MekletAdresiResponse", required = false)
            @param:Element(name = "MekletAdresiResponse", required = false)
            var getMekletAdresiResponse: MekletAdresiResponse?
    ) {
        data class MekletAdresiResponse(
                @field:Element(name = "MekletAdresiResult")
                @param:Element(name = "MekletAdresiResult")
                var mekletAdresiResult: MekletAdresiResult
        ) {
            data class MekletAdresiResult(
                    @field:ElementList(entry = "AdresesDati", inline = true, required = false)
                    @param:ElementList(entry = "AdresesDati", inline = true, required = false)
                    var adresesDatiList: List<AdresesDati>? = null
            ) {
                @Root(name = "AdresesDati")
                data class AdresesDati(
                        @field:Element(name = "Kods", required = false)
                        @param:Element(name = "Kods", required = false)
                        var kods: String?,

                        @field:Element(name = "Nosaukums", required = false)
                        @param:Element(name = "Nosaukums", required = false)
                        var nosaukums: String?
                )
            }
        }
    }

}

val Throwable.findAddressResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(FindAddressResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)


/*
HTTP/1.1 200 OK
Server: nginx
Date: Fri, 25 Oct 2019 18:42:54 GMT
Content-Type: application/soap+xml; charset=utf-8
Content-Length: 1097
Connection: keep-alive
Cache-Control: private
X-Frame-Options: SAMEORIGIN

<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:a="http://www.w3.org/2005/08/addressing">
    <s:Header>
        <a:Action s:mustUnderstand="1">http://tempuri.org/IMobileService/MekletAdresiResponse</a:Action>
    </s:Header>
    <s:Body>
        <MekletAdresiResponse xmlns="http://tempuri.org/">
            <MekletAdresiResult xmlns:b="http://schemas.datacontract.org/2004/07/Muvis.WebServices.MobileServiceActions" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
                <b:AdresesDati>
                    <b:Kods>113807345</b:Kods>
                    <b:Nosaukums>STARTA IELA 40 - 1, RĪGA, LV-1039</b:Nosaukums>
                </b:AdresesDati>
                <b:AdresesDati>
                    <b:Kods>113807353</b:Kods>
                    <b:Nosaukums>STARTA IELA 40 - 2, RĪGA, LV-1039</b:Nosaukums>
                </b:AdresesDati>
            </MekletAdresiResult>
        </MekletAdresiResponse>
    </s:Body>
</s:Envelope>
*/