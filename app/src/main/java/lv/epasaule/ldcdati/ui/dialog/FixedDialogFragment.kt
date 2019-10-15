package lv.epasaule.ldcdati.ui.dialog

import androidx.fragment.app.DialogFragment


/**
 * https://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation
 */
open class FixedDialogFragment : DialogFragment() {

    override fun onDestroyView() {
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (activity?.isChangingConfigurations != false) {
            dialog?.setDismissMessage(null)
        }
        super.onDestroyView()
    }

}