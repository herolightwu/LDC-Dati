package lv.epasaule.ldcdati.network.soap

import lv.epasaule.ldcdati.network.soap.animal.AnimalRequestEnvelope
import lv.epasaule.ldcdati.network.soap.animal.AnimalResponseEnvelope
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventRequestEnvelope
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventResponseEnvelope
import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressRequestEnvelope
import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressResponseEnvelope
import lv.epasaule.ldcdati.network.soap.login.LoginRequestEnvelope
import lv.epasaule.ldcdati.network.soap.login.LoginResponseEnvelope
import lv.epasaule.ldcdati.network.soap.logout.LogoutRequestEnvelope
import lv.epasaule.ldcdati.network.soap.logout.LogoutResponseEnvelope
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesRequestEnvelope
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesResponseEnvelope
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import rx.Observable


interface SoapApi {

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/Login\""
    )
    @POST("MobileService.svc")
    fun login(@Body body: LoginRequestEnvelope): Observable<LoginResponseEnvelope>

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/Logout\""
    )
    @POST("MobileService.svc")
    fun logout(@Body body: LogoutRequestEnvelope): Observable<LogoutResponseEnvelope>

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/GetDzivniekaDati\""
    )
    @POST("MobileService.svc")
    fun animal(@Body body: AnimalRequestEnvelope): Observable<AnimalResponseEnvelope>

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/GetVakcinas\""
    )
    @POST("MobileService.svc")
    fun vaccines(@Body body: VaccinesRequestEnvelope): Observable<VaccinesResponseEnvelope>

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/MekletAdresi\""
    )
    @POST("MobileService.svc")
    fun findAddress(@Body body: FindAddressRequestEnvelope): Observable<FindAddressResponseEnvelope>

    @Headers(
            "Accept-Encoding: gzip,deflate",
            "Content-Type: application/soap+xml;charset=UTF-8;action=\"http://tempuri.org/IMobileService/IzveidotNotikumu\""
    )
    @POST("MobileService.svc")
    fun createEvent(@Body body: CreateEventRequestEnvelope): Observable<CreateEventResponseEnvelope>

}