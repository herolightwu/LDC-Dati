package lv.epasaule.ldcdati.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.dialog_fragment_logout.*
import lv.epasaule.ldcdati.App
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.SoapApiFacade
import lv.epasaule.ldcdati.ui.dialog.showErrorDialog
import lv.epasaule.ldcdati.util.opt
import lv.epasaule.ldcdati.util.pickValue
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.subscriptions.CompositeSubscription


class LogoutDialogFragment : DialogFragment() {

    private val soapApiFacade = SoapApiFacade
    private val compositeSubscription = CompositeSubscription()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_fragment_logout, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeSubscription.addAll(
                RxView.clicks(buttonCancel).subscribe { dismiss() },

                sessionData
                        .map { it.value?.personName.opt }
                        .observeOn(mainThread())
                        .subscribe { textViewLogoutTitle.text = it.value },

                RxView.clicks(buttonLogout).switchMap {
                    sessionId
                            .take(1)
                            .pickValue
                            .flatMap { mySessionId ->
                                soapApiFacade.logout(mySessionId)
                                        .flatMap {
                                            val fault = it.body.fault
                                            if (fault != null) {
                                                app.userSession.updateSession(null)
                                                        .observeOn(mainThread())
                                                        .doOnNext {
                                                            showErrorDialog(message = fault.reason.text)
                                                            dismiss()
                                                        }
                                            } else {
                                                dismiss()
                                                app.userSession.updateSession(null)
                                            }
                                        }
                                        .observeOn(mainThread())
                                        .doOnError {
                                            showErrorDialog(message = it.message)
                                            dismiss()
                                        }
                                        .onErrorResumeNext(app.userSession.updateSession(null))
                            }
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

    private val sessionData get() = app.userSession.sessionData()

    private val sessionId get() = sessionData.map { it.value?.sessionId.opt }

    companion object {
        private val TAG = LogoutDialogFragment::class.java.simpleName

        @JvmStatic val newInstance get() = LogoutDialogFragment()
    }

}