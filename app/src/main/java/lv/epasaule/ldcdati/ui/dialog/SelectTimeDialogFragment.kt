package lv.epasaule.ldcdati.ui.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.util.Calendar.*


const val DEFAULT_SELECT_TIME_DIALOG_ID = 1

data class SelectTimeFragmentArgs(
        val dialogId: Int = DEFAULT_SELECT_TIME_DIALOG_ID,
        val hourOfDay: Int? = null,
        val minute: Int? = null
)

class SelectTimeDialogFragment : FixedDialogFragment(), OnTimeSetListener {

    private lateinit var args: SelectTimeFragmentArgs
    private val listener: SelectTimeDialogFragmentListener?
        get() = targetFragment as? SelectTimeDialogFragmentListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        args = args(arguments)
        val (hourOfDay, minute) = with(args) {
            if (hourOfDay != null && minute != null) {
                hourOfDay to minute
            } else {
                val calendar = getInstance()
                calendar.get(HOUR_OF_DAY) to calendar.get(MINUTE)
            }
        }
        return TimePickerDialog(requireActivity(), this, hourOfDay, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        listener?.onTimeSet(args.dialogId, hourOfDay, minute)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismissSelectTimeDialog(args.dialogId)
    }

    fun tag(dialogId: Int) = "${this::class.java.simpleName}#$dialogId"

    companion object {
        private const val KEY_DIALOG_ID = "dialogId"
        private const val KEY_HOUR_OF_DAY = "hourOfDay"
        private const val KEY_MINUTE = "minute"

        fun newInstance(
                fragmentArgs: SelectTimeFragmentArgs,
                targetFragment: Fragment? = null,
                requestCode: Int = 0
        ): SelectTimeDialogFragment =
                SelectTimeDialogFragment().apply {
                    arguments = with(fragmentArgs) { bundleOf(
                            KEY_DIALOG_ID to dialogId,
                            KEY_HOUR_OF_DAY to (hourOfDay ?: -1),
                            KEY_MINUTE to (minute ?: -1)
                    )}
                    targetFragment?.let { setTargetFragment(it, requestCode) }
                }

        private fun args(arguments: Bundle?): SelectTimeFragmentArgs = arguments
                ?.let { bundle ->
                    with(bundle) {
                        SelectTimeFragmentArgs(
                                getInt(KEY_DIALOG_ID),
                                getInt(KEY_HOUR_OF_DAY).takeIf { it != -1 },
                                getInt(KEY_MINUTE).takeIf { it != -1 }
                        )
                    }
                }
                ?: SelectTimeFragmentArgs()
    }

}

interface SelectTimeDialogFragmentListener {
    fun onDismissSelectTimeDialog(dialogId: Int)

    fun onTimeSet(dialogId: Int, hourOfDay: Int, minute: Int)

}

fun Fragment.showSelectTimeDialog(
        selectTimeFragmentArgs: SelectTimeFragmentArgs
) {
    with(SelectTimeDialogFragment.newInstance(
            selectTimeFragmentArgs,
            this
    )) {
        show(this@showSelectTimeDialog.requireFragmentManager(), tag(selectTimeFragmentArgs.dialogId))
    }
}