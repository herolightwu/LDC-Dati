package lv.epasaule.ldcdati.ui.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.util.Calendar.*


const val DEFAULT_SELECT_DATE_DIALOG_ID = 1

data class SelectDateFragmentArgs(
        val dialogId: Int = DEFAULT_SELECT_DATE_DIALOG_ID,
        val year: Int? = null,
        val month: Int? = null,
        val day: Int? = null
)

class SelectDateDialogFragment : FixedDialogFragment(), OnDateSetListener {

    private lateinit var args: SelectDateFragmentArgs
    private val listener: SelectDateDialogFragmentListener?
        get() = targetFragment as? SelectDateDialogFragmentListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        args = args(arguments)
        val (year, month, day) = with(args) {
            if (year != null && month != null && day != null) {
                Triple(year, month, day)
            } else {
                val calendar = getInstance()
                Triple(
                        calendar.get(YEAR),
                        calendar.get(MONTH),
                        calendar.get(DAY_OF_MONTH)
                )
            }
        }
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        listener?.onDateSet(args.dialogId, year, month, dayOfMonth)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismissSelectDateDialog(args.dialogId)
    }

    fun tag(dialogId: Int) = "${this::class.java.simpleName}#$dialogId"

    companion object {
        private const val KEY_DIALOG_ID = "dialogId"
        private const val KEY_YEAR = "year"
        private const val KEY_MONTH = "month"
        private const val KEY_DAY = "day"

        fun newInstance(
                fragmentArgs: SelectDateFragmentArgs,
                targetFragment: Fragment? = null,
                requestCode: Int = 0
        ): SelectDateDialogFragment =
                SelectDateDialogFragment().apply {
                    arguments = with(fragmentArgs) { bundleOf(
                            KEY_DIALOG_ID to dialogId,
                            KEY_YEAR to (year ?: -1),
                            KEY_MONTH to (month ?: -1),
                            KEY_DAY to (day ?: -1)
                    )}
                    targetFragment?.let { setTargetFragment(it, requestCode) }
                }

        private fun args(arguments: Bundle?): SelectDateFragmentArgs = arguments
                ?.let { bundle ->
                    with(bundle) {
                        SelectDateFragmentArgs(
                                getInt(KEY_DIALOG_ID),
                                getInt(KEY_YEAR).takeIf { it != -1 },
                                getInt(KEY_MONTH).takeIf { it != -1 },
                                getInt(KEY_DAY).takeIf { it != -1 }
                        )
                    }
                }
                ?: SelectDateFragmentArgs()
    }

}

interface SelectDateDialogFragmentListener {
    fun onDismissSelectDateDialog(dialogId: Int)

    fun onDateSet(dialogId: Int, year: Int, month: Int, dayOfMonth: Int)

}

fun Fragment.showSelectDateDialog(
        selectDateFragmentArgs: SelectDateFragmentArgs
) {
    with(SelectDateDialogFragment.newInstance(
            selectDateFragmentArgs,
            this
    )) {
        show(this@showSelectDateDialog.requireFragmentManager(), tag(selectDateFragmentArgs.dialogId))
    }
}