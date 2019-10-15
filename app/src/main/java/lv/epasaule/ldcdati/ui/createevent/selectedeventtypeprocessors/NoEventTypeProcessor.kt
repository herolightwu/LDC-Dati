package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.network.soap.createevent.NoEventTypeSelected

class NoEventTypeProcessor : SelectedEventTypeProcessor {
    override val event: Event = NoEventTypeSelected

    override val showDefaultViews get() = false

}