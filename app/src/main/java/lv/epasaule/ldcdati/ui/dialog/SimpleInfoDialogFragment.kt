package lv.epasaule.ldcdati.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import lv.epasaule.ldcdati.R

const val DEFAULT_DIALOG_ID = 1

data class SimpleInfoFragmentArgs(
        val dialogId: Int = DEFAULT_DIALOG_ID,
        val title: String? = null,
        val message: String? = null
)

class SimpleInfoDialogFragment : FixedDialogFragment() {

    private lateinit var args: SimpleInfoFragmentArgs
    private val listener: SimpleInfoDialogListener?
        get() = targetFragment as? SimpleInfoDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        args = args(arguments)
        val alertDialogBuilder = AlertDialog.Builder(context!!)
                .setTitle(args.title)
                .setMessage(args.message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
        return alertDialogBuilder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismissDialog(args.dialogId)
    }

    fun tag(dialogId: Int) = "${this::class.java.simpleName}#$dialogId"

    companion object {
        private const val KEY_DIALOG_ID = "dialogId"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"

        fun newInstance(
                fragmentArgs: SimpleInfoFragmentArgs,
                targetFragment: Fragment? = null,
                requestCode: Int = 0
        ) = SimpleInfoDialogFragment().apply {
            arguments = with(fragmentArgs) { bundleOf(
                    KEY_DIALOG_ID to dialogId,
                    KEY_TITLE to title,
                    KEY_MESSAGE to message
            )}
            targetFragment?.let { setTargetFragment(it, requestCode) }
        }

        private fun args(arguments: Bundle?): SimpleInfoFragmentArgs = arguments
                ?.let {
                    with(it) {
                        SimpleInfoFragmentArgs(
                                getInt(KEY_DIALOG_ID),
                                getString(KEY_TITLE),
                                getString(KEY_MESSAGE)
                        )
                    }
                }
                ?: SimpleInfoFragmentArgs()
    }

}

interface SimpleInfoDialogListener {
    fun onDismissDialog(dialogId: Int)
}

fun Fragment.showInfoDialog(
        dialogId: Int = DEFAULT_DIALOG_ID,
        message: String?,
        title: String = getString(R.string.app_info)
) {
    with(SimpleInfoDialogFragment.newInstance(
            SimpleInfoFragmentArgs(dialogId, title, message),
            this
    )) {
        show(this@showInfoDialog.requireFragmentManager(), tag(dialogId))
    }
}

fun Fragment.showErrorDialog(
        dialogId: Int = DEFAULT_DIALOG_ID,
        message: String?,
        title: String = getString(R.string.app_error)
) {
    with(SimpleInfoDialogFragment.newInstance(
            SimpleInfoFragmentArgs(dialogId, title, message),
            this
    )) {
        show(this@showErrorDialog.requireFragmentManager(), tag(dialogId))
    }
}

fun FragmentActivity.showErrorDialog(
        dialogId: Int = DEFAULT_DIALOG_ID,
        message: String?,
        title: String = getString(R.string.app_error)
) {
    with(SimpleInfoDialogFragment.newInstance(
            SimpleInfoFragmentArgs(dialogId, title, message),
            null,
            0
    )) {
        show(this@showErrorDialog.supportFragmentManager, tag(dialogId))
    }
}