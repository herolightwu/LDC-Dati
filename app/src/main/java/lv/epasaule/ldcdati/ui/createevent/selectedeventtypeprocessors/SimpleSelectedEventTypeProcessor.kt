package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import lv.epasaule.ldcdati.network.soap.createevent.*

object DeathSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = RegistretaDzivniekaNave
}

object EuthanasiaSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = RegistretaDzivniekaEitanazija
}

object SterilizationSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = DzivnieksIrSterilizets
}

object LostSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = DzivnieksPazudis
}

object FoundSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = DzivnieksAtrasts
}

object ReturnedSelectedEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = DzivnieksAtguts
}