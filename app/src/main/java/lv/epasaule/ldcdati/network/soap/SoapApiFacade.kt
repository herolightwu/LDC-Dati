package lv.epasaule.ldcdati.network.soap

import lv.epasaule.ldcdati.network.soap.animal.AnimalResponseEnvelope
import lv.epasaule.ldcdati.network.soap.animal.animalResponseEnvelope
import lv.epasaule.ldcdati.network.soap.animal.createAnimalRequestEnvelope
import lv.epasaule.ldcdati.network.soap.createevent.CreateEventResponseEnvelope
import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.network.soap.createevent.createCreateEventRequestEnvelope
import lv.epasaule.ldcdati.network.soap.createevent.createEventResponseEnvelope
import lv.epasaule.ldcdati.network.soap.findaddress.FindAddressResponseEnvelope
import lv.epasaule.ldcdati.network.soap.findaddress.createFindAddressRequestEnvelope
import lv.epasaule.ldcdati.network.soap.findaddress.findAddressResponseEnvelope
import lv.epasaule.ldcdati.network.soap.login.LoginResponseEnvelope
import lv.epasaule.ldcdati.network.soap.login.createLoginRequestEnvelope
import lv.epasaule.ldcdati.network.soap.login.loginResponseEnvelope
import lv.epasaule.ldcdati.network.soap.logout.LogoutResponseEnvelope
import lv.epasaule.ldcdati.network.soap.logout.createLogoutRequestEnvelope
import lv.epasaule.ldcdati.network.soap.logout.logoutResponseEnvelope
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesResponseEnvelope
import lv.epasaule.ldcdati.network.soap.vaccines.createVaccinesRequestEnvelope
import lv.epasaule.ldcdati.network.soap.vaccines.vaccinesResponseEnvelope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import rx.Observable
import rx.schedulers.Schedulers.io

private const val API_BASE_URL = "https://muvis2.ldc.gov.lv/muvis/WebServices/"

object SoapApiFacade {

    private val soapApi: SoapApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        val callAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(io())

        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build()

        soapApi = retrofit.create(SoapApi::class.java)
    }

    fun login(login: String, password: String): Observable<LoginResponseEnvelope> =
            soapApi.login(createLoginRequestEnvelope(login, password))
                    .onErrorResumeNext { it.loginResponseEnvelope }

    fun logout(sessionId: String): Observable<LogoutResponseEnvelope> =
            soapApi.logout(createLogoutRequestEnvelope(sessionId))
                    .onErrorResumeNext { it.logoutResponseEnvelope }

    fun animal(sessionId: String, animalId: String): Observable<AnimalResponseEnvelope> =
            soapApi.animal(createAnimalRequestEnvelope(sessionId, animalId))
                    .onErrorResumeNext { it.animalResponseEnvelope }

    fun vaccines(sessionId: String): Observable<VaccinesResponseEnvelope> =
            soapApi.vaccines(createVaccinesRequestEnvelope(sessionId))
                    .onErrorResumeNext { it.vaccinesResponseEnvelope }

    fun findAddress(sessionId: String, address: String): Observable<FindAddressResponseEnvelope> =
            soapApi.findAddress(createFindAddressRequestEnvelope(sessionId, address))
                    .onErrorResumeNext { it.findAddressResponseEnvelope }

    fun createEvent(
            sessionId: String,
            animalId: String,
            eventDate: Long,
            event: Event,
            comments: String? = null
    ): Observable<CreateEventResponseEnvelope> =
            soapApi.createEvent(
                    createCreateEventRequestEnvelope(sessionId, animalId, eventDate, event, comments)
            )
                    .onErrorResumeNext { it.createEventResponseEnvelope }

}

val xmlSerializer = Persister()
