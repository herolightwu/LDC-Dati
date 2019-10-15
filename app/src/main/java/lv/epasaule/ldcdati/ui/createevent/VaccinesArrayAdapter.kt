package lv.epasaule.ldcdati.ui.createevent

import android.content.Context
import android.widget.ArrayAdapter
import lv.epasaule.ldcdati.network.soap.vaccines.VaccinesResponseEnvelope.Body.GetVakcinasResponse

private const val NO_ID: Long = -1

class VaccinesArrayAdapter(
        context: Context,
        vaccineItems: List<VaccineItem>
) : ArrayAdapter<VaccineItem>(
        context,
        android.R.layout.simple_spinner_item,
        vaccineItems
) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getItemId(position: Int): Long = getItem(position)
            ?.id?.hashCode()?.toLong()
            ?: NO_ID

}

data class VaccineItem(
        var id: String,
        var nosaukums: String


) {
    override fun toString(): String {
        return nosaukums
    }
}

val GetVakcinasResponse.vaccineItems get() = getVakcinasResult.vakcinasDatiList
        ?.filter { !it.id.isNullOrEmpty() && !it.nosaukums.isNullOrEmpty() }
        ?.map {
            VaccineItem(it.id.orEmpty(), it.nosaukums.orEmpty())
        }
        ?: emptyList()
