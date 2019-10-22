package lv.epasaule.ldcdati.ui.createevent

import android.view.View
import lv.epasaule.ldcdati.network.soap.createevent.EventType
import lv.epasaule.ldcdati.network.soap.createevent.EventType.*
import lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors.*

class CreateEventHelper {

    fun eventTypeSelectedPositionToEventType(position: Int) = when (position) {
        0 -> NO_EVENT_TYPE_SELECTED
//      1 -> DZIVNIEKS_IR_BISTAMS
        1 -> REGISTRETA_DZIVNIEKA_NAVE
        2 -> REGISTRETA_DZIVNIEKA_EITANAZIJA
        3 -> DZIVNIEKS_IR_STERILIZETS
        4 -> DZIVNIEKS_PAZUDIS
        5 -> DZIVNIEKS_IR_VAKCINETS
        6 -> MAINITA_TURESANAS_ADRESE
        7 -> DZIVNIEKS_ATRASTS
        8 -> DZIVNIEKS_ATGUTS
        else -> throw IllegalStateException()
    }

    fun eventTypeToEventTypeProcessor(
            eventType: EventType,
            containerView: View
    ) = when (eventType) {
        NO_EVENT_TYPE_SELECTED -> NoEventTypeProcessor()
        DZIVNIEKS_IR_BISTAMS -> DangerousSelectedEventTypeProcessor(containerView)
        REGISTRETA_DZIVNIEKA_NAVE -> DeathSelectedEventTypeProcessor
        REGISTRETA_DZIVNIEKA_EITANAZIJA -> EuthanasiaSelectedEventTypeProcessor
        DZIVNIEKS_IR_STERILIZETS -> SterilizationSelectedEventTypeProcessor
        DZIVNIEKS_PAZUDIS -> LostSelectedEventTypeProcessor
        DZIVNIEKS_IR_VAKCINETS -> VaccineSelectedEventTypeProcessor(containerView)
        MAINITA_TURESANAS_ADRESE -> LocationChangeSelectedEventTypeProcessor(containerView)
        DZIVNIEKS_ATRASTS -> FoundSelectedEventTypeProcessor
        DZIVNIEKS_ATGUTS -> ReturnedSelectedEventTypeProcessor
    }

}