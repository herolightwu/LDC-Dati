package lv.epasaule.ldcdati.widget

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.IdRes


class AutoSuggestAdapter(
        context: Context,
        resource: Int,
        @IdRes textViewResourceId: Int
) : ArrayAdapter<AddressData>(context, resource, textViewResourceId), Filterable {

    private var dataList: List<AddressData> = emptyList()

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): AddressData? {
        return dataList[position]
    }

    fun setDataList(list: List<AddressData>) {
        dataList = list
    }

    fun data(position: Int): AddressData {
        return dataList[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            }
        }
    }

}

data class AddressData(val code: String, val address: String) {

    override fun toString(): String {
        return address
    }

}