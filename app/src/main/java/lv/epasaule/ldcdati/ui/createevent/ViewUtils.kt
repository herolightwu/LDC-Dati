package lv.epasaule.ldcdati.ui.createevent

import android.widget.EditText
import android.widget.TextView
import lv.epasaule.ldcdati.R

var EditText.selectedDateTime: SelectedDateTime?
    get() = getTag(R.id.viewTagDateTime) as SelectedDateTime?
    set(value) = setTag(R.id.viewTagDateTime, value)

var TextView.addressCode: String?
    get() = getTag(R.id.viewTagAddressCode) as String?
    set(value) = setTag(R.id.viewTagAddressCode, value)