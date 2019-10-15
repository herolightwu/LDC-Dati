package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import android.view.View
import com.jakewharton.rxbinding.widget.RxAdapterView
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_create_event.*
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.createevent.DzivnieksIrVakcinets
import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.ui.createevent.VaccineItem
import lv.epasaule.ldcdati.ui.createevent.selectedDateTime
import rx.Observable

class VaccineSelectedEventTypeProcessor(
        override val containerView: View
) : SelectedEventTypeProcessor, LayoutContainer {

    private val context = containerView.context

    override fun init() {
        Observable.combineLatest(
                RxAdapterView.itemSelections(spinnerCreateEventVaccines)
                        .map { (spinnerCreateEventVaccines.selectedItem as VaccineItem).nosaukums }
                ,
                RxTextView.afterTextChangeEvents(editTextCreateEventEventDate)
                        .map { editTextCreateEventEventDate.selectedDateTime }
        ) { vaccineName, createDate -> Pair(vaccineName, createDate) }
                .subscribe { (vaccineName, createDate) ->
                    val nextDate =
                            if (!vaccineName.equals("Pret trakumsērgu", true)
                                    || createDate == null
                            ) null
                            else createDate.nextYear(context)
                    editTextCreateEventNextVaccineDate.setText(
                            nextDate?.longDateStr(context)
                    )
                    editTextCreateEventNextVaccineDate.selectedDateTime = nextDate
                }
    }

    override val event: Event
        get() = DzivnieksIrVakcinets(
                nextVaccineDate,
                (spinnerCreateEventVaccines.selectedItem as VaccineItem).id,
                editTextCreateEventVeterinarianCertificate.text?.toString().orEmpty()
        )

    override val visibleExtraViews: Set<Int> = setOf(
            R.id.spinnerCreateEventVaccines,
            R.id.linearLayoutCreateEventNextVaccineDate,
            R.id.textInputLayoutCreateEventVeterinarianCertificate
    )

    private val nextVaccineDate get() = editTextCreateEventNextVaccineDate.selectedDateTime
            ?.utcLong(containerView.context)

}