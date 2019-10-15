package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_create_event.*
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.createevent.DzivnieksIrBistams
import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.ui.createevent.selectedDateTime

class DangerousSelectedEventTypeProcessor(
        override val containerView: View
) : SelectedEventTypeProcessor, LayoutContainer {

    override val event: Event get() = DzivnieksIrBistams(dangerEndDate)

    override val visibleExtraViews: Set<Int> = setOf(
            R.id.linearLayoutCreateEventDangerEndDate
    )

    private val dangerEndDate get() = editTextCreateEventDangerEndDate.selectedDateTime
            ?.utcLong(containerView.context)

}