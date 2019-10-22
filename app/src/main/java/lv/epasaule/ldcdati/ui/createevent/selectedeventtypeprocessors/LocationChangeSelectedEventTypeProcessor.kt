package lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors

import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_create_event.*
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.network.soap.createevent.FalseTuretsArzemes
import lv.epasaule.ldcdati.network.soap.createevent.MainitaTuresanasAdrese
import lv.epasaule.ldcdati.network.soap.createevent.TrueTuretsArzemes
import lv.epasaule.ldcdati.ui.createevent.addressCode

class LocationChangeSelectedEventTypeProcessor(
        override val containerView: View
) : SelectedEventTypeProcessor, LayoutContainer {

    override fun init() {
        textInputLayoutCreateEventCountryIsoCode.isVisible = checkBoxCreateEventKeptAbroad.isChecked
        linearLayoutCreateEventAddress.isVisible = !checkBoxCreateEventKeptAbroad.isChecked
    }

    override val event: Event
        get() = MainitaTuresanasAdrese(
                if (checkBoxCreateEventKeptAbroad.isChecked) TrueTuretsArzemes(
                        editTextCreateEventCountryIsoCode.text?.toString().orEmpty()
                ) else FalseTuretsArzemes(autoCompleteTextViewCreateEventAddress.addressCode.orEmpty()),
                editTextCreateEventCountryAddressDetails.text?.toString()
        )

    override val visibleExtraViews: Set<Int> = setOf(
            R.id.checkBoxCreateEventKeptAbroad,
            R.id.textInputLayoutCreateEventCountryIsoCode,
            R.id.linearLayoutCreateEventAddress,
            R.id.textInputLayoutCreateEventCountryAddressDetails
    )

    override val checkBoxCreateEventKeptAbroadListener: ((isChecked: Boolean) -> Unit)?
        get() = {
            textInputLayoutCreateEventCountryIsoCode.isVisible = it
            linearLayoutCreateEventAddress.isVisible = !it
        }

}