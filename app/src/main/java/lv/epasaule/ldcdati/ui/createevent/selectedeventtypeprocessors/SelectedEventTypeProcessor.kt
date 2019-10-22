package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import lv.epasaule.ldcdati.network.soap.createevent.Event

interface SelectedEventTypeProcessor {

    fun init() { }

    val showDefaultViews: Boolean get() = true

    val visibleExtraViews: Set<Int> get() = emptySet()

    val checkBoxCreateEventKeptAbroadListener: ((isChecked: Boolean) -> Unit)? get() = null

    val event: Event

}