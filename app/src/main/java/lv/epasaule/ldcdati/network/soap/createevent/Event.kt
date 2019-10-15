package lv.epasaule.ldcdati.network.soap.createevent

import lv.epasaule.ldcdati.network.soap.createevent.EventType.*

enum class EventType(val eventId: Int) {
    NO_EVENT_TYPE_SELECTED(-1),
    DZIVNIEKS_IR_BISTAMS(1),
    REGISTRETA_DZIVNIEKA_NAVE(5),
    REGISTRETA_DZIVNIEKA_EITANAZIJA(6),
    DZIVNIEKS_IR_STERILIZETS(7),
    DZIVNIEKS_PAZUDIS(8),
    DZIVNIEKS_IR_VAKCINETS(9),
    MAINITA_TURESANAS_ADRESE(10),
    DZIVNIEKS_ATRASTS(14),
    DZIVNIEKS_ATGUTS(15)
}

interface HasEventType {
    val eventType: EventType
}

sealed class Event : HasEventType

object NoEventTypeSelected : Event() {
    override val eventType = NO_EVENT_TYPE_SELECTED
}

data class DzivnieksIrBistams(val dangerEndDate: Long?) : Event() {
    override val eventType = DZIVNIEKS_IR_BISTAMS
}

object RegistretaDzivniekaNave : Event() {
    override val eventType = REGISTRETA_DZIVNIEKA_NAVE
}

object RegistretaDzivniekaEitanazija : Event() {
    override val eventType = REGISTRETA_DZIVNIEKA_EITANAZIJA
}

object DzivnieksIrSterilizets : Event() {
    override val eventType = DZIVNIEKS_IR_STERILIZETS
}

object DzivnieksPazudis : Event() {
    override val eventType = DZIVNIEKS_PAZUDIS
}

data class DzivnieksIrVakcinets(
        val nextDate: Long?,
        val vaccineId: String,
        val certificateNumber: String
) : Event() {
    override val eventType = DZIVNIEKS_IR_VAKCINETS
}

data class MainitaTuresanasAdrese(
        val turetsArzemes: TuretsArzemes,
        val adresesPrecizejums: String?
) : Event() {
    override val eventType = MAINITA_TURESANAS_ADRESE
}

object DzivnieksAtrasts : Event() {
    override val eventType = DZIVNIEKS_ATRASTS
}

object DzivnieksAtguts : Event() {
    override val eventType = DZIVNIEKS_ATGUTS
}


sealed class TuretsArzemes

data class TrueTuretsArzemes(val valstsIsoKods: String) : TuretsArzemes()

data class FalseTuretsArzemes(val adresesKods: String) : TuretsArzemes()