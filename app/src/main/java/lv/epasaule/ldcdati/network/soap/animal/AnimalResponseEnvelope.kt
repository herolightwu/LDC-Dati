package lv.epasaule.ldcdati.network.soap.animal

import lv.epasaule.ldcdati.network.soap.animal.AnimalResponseEnvelope.Body.GetDzivniekaDatiResponse.GetDzivniekaDatiResult.Identifikatori.DzivniekaIdentifikators
import lv.epasaule.ldcdati.network.soap.common.Fault
import lv.epasaule.ldcdati.network.soap.xmlSerializer
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.HttpException
import rx.Observable


@Root(name = "Envelope", strict = false)
data class AnimalResponseEnvelope(
        @field:Element(name = "Body") @param:Element(name = "Body")
        var body: Body
) {

    val isHomeAnimal: Boolean get() {
        val getDzivniekaDatiResponse =
                body.getDzivniekaDatiResponse ?: return false
        val majasDzivnieks = getDzivniekaDatiResponse
                .getDzivniekaDatiResult
                .majasDzivnieks
        return (majasDzivnieks != null && majasDzivnieks.equals("true", ignoreCase = true))
    }

    val name: String? get() = body
            .getDzivniekaDatiResponse
            ?.getDzivniekaDatiResult
            ?.vards

    val ids: List<DzivniekaIdentifikators>? get() = body
            .getDzivniekaDatiResponse
            ?.getDzivniekaDatiResult
            ?.identifikatori
            ?.dzivniekaIdentifikatorsList
            ?: emptyList()

    data class Body(
            @field:Element(name = "Fault", required = false)
            @param:Element(name = "Fault", required = false)
            var fault: Fault?,

            @field:Element(name = "GetDzivniekaDatiResponse", required = false)
            @param:Element(name = "GetDzivniekaDatiResponse", required = false)
            var getDzivniekaDatiResponse: GetDzivniekaDatiResponse?
    ) {
        data class GetDzivniekaDatiResponse(
                @field:Element(name = "GetDzivniekaDatiResult")
                @param:Element(name = "GetDzivniekaDatiResult")
                var getDzivniekaDatiResult: GetDzivniekaDatiResult
        ) {

            data class GetDzivniekaDatiResult(
//                    @field:Element(name = "Aizliegumi") @param:Element(name = "Aizliegumi")
//                    var aizliegumi: ???,
//
//                    @field:Element(name = "Asiniba") @param:Element(name = "Asiniba")
//                    var asiniba: ???,

                    @field:Element(name = "DzimsanasDatums", required = false)
                    @param:Element(name = "DzimsanasDatums", required = false)
                    var dzimsanasDatums: String?,

                    @field:Element(name = "Dzimums", required = false)
                    @param:Element(name = "Dzimums", required = false)
                    var dzimums: String?,

//                    @field:Element(name = "Ganampulks") @param:Element(name = "Ganampulks")
//                    var ganampulks: ???,

                    @field:Element(name = "Identifikatori", required = false)
                    @param:Element(name = "Identifikatori", required = false)
                    var identifikatori: Identifikatori?,

//                    @field:Element(name = "IzslegsanasDatums") @param:Element(name = "IzslegsanasDatums")
//                    var izslegsanasDatums: ???,

//                    @field:Element(name = "IzslegsanasIemesls") @param:Element(name = "IzslegsanasIemesls")
//                    var izslegsanasIemesls: ???,

                    @field:Element(name = "MajasDzivnieks", required = false)
                    @param:Element(name = "MajasDzivnieks", required = false)
                    var majasDzivnieks: String?,

//                    @field:Element(name = "Novietne") @param:Element(name = "Novietne")
//                    var novietne: ???,

                    @field:Element(name = "Pote", required = false)
                    @param:Element(name = "Pote", required = false)
                    var pote: String?,

                    @field:Element(name = "Skirne", required = false)
                    @param:Element(name = "Skirne", required = false)
                    var skirne: String?,

                    @field:Element(name = "Status", required = false)
                    @param:Element(name = "Status", required = false)
                    var status: String?,

                    @field:Element(name = "Suga", required = false)
                    @param:Element(name = "Suga", required = false)
                    var suga: String?,

                    @field:Element(name = "TuresanasAdrese", required = false)
                    @param:Element(name = "TuresanasAdrese", required = false)
                    var turesanasAdrese: String?,

                    @field:Element(name = "Vakcinacijas", required = false)
                    @param:Element(name = "Vakcinacijas", required = false)
                    var vakcinacijas: Vakcinacijas?,

                    @field:Element(name = "Vards", required = false)
                    @param:Element(name = "Vards", required = false)
                    var vards: String?
            ) {
                @Root(name = "Identifikatori")
                data class Identifikatori(
                        @field:ElementList(entry = "DzivniekaIdentifikators", inline = true, required = false)
                        @param:ElementList(entry = "DzivniekaIdentifikators", inline = true, required = false)
                        var dzivniekaIdentifikatorsList: List<DzivniekaIdentifikators>? = null
                ) {
                    @Root(name = "DzivniekaIdentifikators")
                    data class DzivniekaIdentifikators(
                            @field:Element(name = "Apraksts", required = false)
                            @param:Element(name = "Apraksts", required = false)
                            var apraksts: String?
                    )
                }

                data class Vakcinacijas(
                        @field:ElementList(entry = "DzivniekaVakcinacija", inline = true, required = false)
                        @param:ElementList(entry = "DzivniekaVakcinacija", inline = true, required = false)
                        var dzivniekaVakcinacijaList: List<DzivniekaVakcinacija>? = null
                ) {
                    @Root(name = "DzivniekaVakcinacija")
                    data class DzivniekaVakcinacija(
                            @field:Element(name = "NakosaisDatums", required = false)
                            @param:Element(name = "NakosaisDatums", required = false)
                            var nakosaisDatums: String? = null,

                            @field:Element(name = "NotikumaDatums", required = false)
                            @param:Element(name = "NotikumaDatums", required = false)
                            var notikumaDatums: String? = null,

                            @field:Element(name = "Sertifikats", required = false)
                            @param:Element(name = "Sertifikats", required = false)
                            var sertifikats: String? = null,

                            @field:Element(name = "VakcinasId", required = false)
                            @param:Element(name = "VakcinasId", required = false)
                            var vakcinasId: String? = null
                    )
                }
            }
        }
    }

}

val Throwable.animalResponseEnvelope get() = (this as? HttpException)
        ?.response()
        ?.errorBody()
        ?.byteStream()
        ?.let {
            Observable.just(xmlSerializer.read(AnimalResponseEnvelope::class.java, it))
        }
        ?: Observable.error(this)