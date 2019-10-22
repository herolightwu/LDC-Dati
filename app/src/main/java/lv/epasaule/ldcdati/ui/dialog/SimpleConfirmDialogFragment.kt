package lv.epasaule.ldcdati.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import lv.epasaule.ldcdati.R

@StringRes const val DEFAULT_NEGATIVE_TEXT_ID: Int = R.string.cancel
@StringRes const val DEFAULT_POSITIVE_TEXT_ID: Int = R.string.ok

data class SimpleConfirmFragmentArgs(
        val dialogId: Int = DEFAULT_DIALOG_ID,
        val title: String? = null,
        val message: String? = null,
        @StringRes val negativeTextId: Int = DEFAULT_NEGATIVE_TEXT_ID,
        @StringRes val positiveTextId: Int = DEFAULT_POSITIVE_TEXT_ID
)

class SimpleConfirmDialogFragment : FixedDialogFragment() {

    private lateinit var args: SimpleConfirmFragmentArgs
    private val listener: SimpleConfirmDialogListener?
        get() = targetFragment as? SimpleConfirmDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        args = args(arguments)
        val alertDialogBuilder = AlertDialog.Builder(context!!)
                .setTitle(args.title)
                .setMessage(args.message)
                .setNegativeButton(args.negativeTextId) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(args.positiveTextId) { dialog, _ ->
                    listener?.onAcceptConfirmDialog(args.dialogId)
                    dialog.dismiss()
                }
        return alertDialogBuilder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDismissConfirmDialog(args.dialogId)
    }

    fun tag(dialogId: Int) = "${this::class.java.simpleName}#$dialogId"

    companion object {
        private const val KEY_DIALOG_ID = "dialogId"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_NEGATIVE_TEXT_ID = "negativeTextId"
        private const val KEY_POSITIVE_TEXT_ID = "positiveTextId"

        fun newInstance(
                fragmentArgs: SimpleConfirmFragmentArgs,
                targetFragment: Fragment? = null,
                requestCode: Int = 0
        ): SimpleConfirmDialogFragment =
                SimpleConfirmDialogFragment().apply {
                    arguments = with(fragmentArgs) { bundleOf(
                            KEY_DIALOG_ID to dialogId,
                            KEY_TITLE to title,
                            KEY_MESSAGE to message,
                            KEY_NEGATIVE_TEXT_ID to negativeTextId,
                            KEY_POSITIVE_TEXT_ID to positiveTextId
                    )}
                    targetFragment?.let { setTargetFragment(it, requestCode) }
                }

        private fun args(arguments: Bundle?): SimpleConfirmFragmentArgs = arguments
                ?.let {
                    with(it) {
                        SimpleConfirmFragmentArgs(
                                getInt(KEY_DIALOG_ID),
                                getString(KEY_TITLE),
                                getString(KEY_MESSAGE),
                                getInt(KEY_NEGATIVE_TEXT_ID),
                                getInt(KEY_POSITIVE_TEXT_ID)
                        )
                    }
                }
                ?: SimpleConfirmFragmentArgs()
    }

}

interface SimpleConfirmDialogListener {
    fun onDismissConfirmDialog(dialogId: Int)

    fun onAcceptConfirmDialog(dialogId: Int)
}

fun Fragment.showConfirmDialog(
        simpleConfirmFragmentArgs: SimpleConfirmFragmentArgs
) {
    with(SimpleConfirmDialogFragment.newInstance(
            simpleConfirmFragmentArgs,
            this
    )) {
        show(this@showConfirmDialog.requireFragmentManager(), tag(simpleConfirmFragmentArgs.dialogId))
    }
}