package lv.epasaule.ldcdati.util

import android.view.inputmethod.EditorInfo
import android.widget.TextView

fun TextView.doOnActionDone(action: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            action()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}