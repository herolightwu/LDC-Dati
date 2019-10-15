package lv.epasaule.ldcdati.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.dialog_fragment_login.*
import lv.epasaule.ldcdati.App
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.SoapApiFacade
import lv.epasaule.ldcdati.session.sessionData
import lv.epasaule.ldcdati.ui.dialog.showErrorDialog
import lv.epasaule.ldcdati.util.doOnActionDone
import rx.Observable
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.subscriptions.CompositeSubscription


class LoginDialogFragment : DialogFragment() {

    private val soapApiFacade = SoapApiFacade
    private val compositeSubscription = CompositeSubscription()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_fragment_login, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show soft keyboard automatically and request focus to field
        login.requestFocus()
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)

        password.doOnActionDone { loginButton.performClick() }

        compositeSubscription.add(RxView.clicks(loginButton).switchMap {
            soapApiFacade.login(
                    login.text?.toString().orEmpty(),
                    password.text?.toString().orEmpty()
            )
                    .flatMap {
                        val fault = it.body.fault
                        if (fault != null) {
                            app.userSession.updateSession(null)
                                    .observeOn(mainThread())
                                    .doOnNext { showErrorDialog(message = fault.reason.text) }
                        } else {
                            val loginResult = it.body.loginResponse!!.loginResult
                            app.userSession.updateSession(loginResult.sessionData)
                                    .observeOn(mainThread())
                                    .doOnNext {
                                        Toast.makeText(
                                                context,
                                                getString(R.string.auth_successful),
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        dismiss()
                                    }
                        }
                    }
                    .observeOn(mainThread())
                    .doOnError { showErrorDialog(message = it.message) }
                    .onErrorResumeNext(Observable.empty())
        }
                .observeOn(mainThread())
                .subscribe(
                        { },
                        { throwable -> showErrorDialog(message = throwable.message) }
                )
        )
    }

    override fun onDestroy() {
        compositeSubscription.clear()
        super.onDestroy()
    }

    private val app get() = requireActivity().application as App

    companion object {
        private val TAG = LoginDialogFragment::class.java.simpleName

        @JvmStatic val newInstance get() = LoginDialogFragment()
    }

}