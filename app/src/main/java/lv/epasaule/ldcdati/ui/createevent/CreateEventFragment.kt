package lv.epasaule.ldcdati.ui.createevent

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxAdapterView
import com.jakewharton.rxbinding.widget.RxAutoCompleteTextView
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_create_event.*
import lv.epasaule.ldcdati.App
import lv.epasaule.ldcdati.R
import lv.epasaule.ldcdati.network.soap.SoapApiFacade
import lv.epasaule.ldcdati.network.soap.createevent.Event
import lv.epasaule.ldcdati.network.soap.createevent.EventType
import lv.epasaule.ldcdati.ui.createevent.selectedeventtypeprocessors.SelectedEventTypeProcessor
import lv.epasaule.ldcdati.ui.dialog.*
import lv.epasaule.ldcdati.util.longDateStr
import lv.epasaule.ldcdati.util.opt
import lv.epasaule.ldcdati.util.pickValue
import lv.epasaule.ldcdati.util.primaryLocale
import lv.epasaule.ldcdati.widget.AddressData
import lv.epasaule.ldcdati.widget.AutoSuggestAdapter
import rx.Observable
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.schedulers.Schedulers.io
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit


private const val ARG_ANIMAL_ID = "animalId"
private const val ARG_ANIMAL_NAME = "animalName"
private const val ARG_ANIMAL_IDS = "animalIds"

const val SELECT_EVENT_DATE_DIALOG_ID = 100
const val SELECT_DANGER_END_DATE_DIALOG_ID = 101
const val SELECT_NEXT_VACCINE_DATE_DIALOG_ID = 102
const val SELECT_EVENT_TIME_DIALOG_ID = 200
const val SELECT_DANGER_END_TIME_DIALOG_ID = 201
const val SELECT_NEXT_VACCINE_TIME_DIALOG_ID = 202
const val CANCEL_CONFIRM_DIALOG_ID = 300
const val SAVE_CONFIRM_DIALOG_ID = 301

class CreateEventFragment :
        Fragment(),
        SelectDateDialogFragmentListener,
        SelectTimeDialogFragmentListener,
        SimpleConfirmDialogListener
{
    private lateinit var animalId: String
    private var animalName: String? = null
    private var animalIdsStr: String? = null
    private var listener: CreateEventFragmentListener? = null
    private val createEventHelper = CreateEventHelper()
    private var eventTypeProcessor: SelectedEventTypeProcessor? = null
    private val soapApiFacade = SoapApiFacade
    private val compositeSubscription = CompositeSubscription()
    private lateinit var autoSuggestAdapter: AutoSuggestAdapter
    private val saveEventSubject = PublishSubject.create<Unit>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CreateEventFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CreateEventFragmentListener")
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCancelConfirmDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            animalId = it.getString(ARG_ANIMAL_ID) ?: throw IllegalArgumentException()
            animalName = it.getString(ARG_ANIMAL_NAME)
            animalIdsStr = it.getString(ARG_ANIMAL_IDS)
        }
        compositeSubscription.addAll(
                sessionId.take(1).pickValue.switchMap { sessionId ->
                    soapApiFacade.vaccines(sessionId)
                }
                        .observeOn(mainThread())
                        .doOnError { showErrorDialog(message = it.message) }
                        .onErrorResumeNext(Observable.empty())
                        .subscribe {
                            val fault = it.body.fault
                            if (fault != null) {
                                showErrorDialog(message = fault.reason.text)
                            } else {
                                spinnerCreateEventVaccines.adapter = VaccinesArrayAdapter(
                                        requireContext(),
                                        listOf(
                                                VaccineItem("", getString(R.string.vaccine_type_prompt))
                                        ) + it.body.getVakcinasResponse!!.vaccineItems
                                )
                            }
                        },
                app.userSession.sessionData().take(1).pickValue
                        .observeOn(mainThread())
                        .subscribe {
                            editTextCreateEventVeterinarianCertificate.setText(it.certificateNumber)
                            editTextCreateEventVeterinarianCertificate.isEnabled =
                                    it.certificateNumber.isNullOrEmpty()
                        }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_create_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        subscribe()
    }

    override fun onDestroy() {
        compositeSubscription.clear()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDismissSelectDateDialog(dialogId: Int) {

    }

    override fun onDateSet(dialogId: Int, year: Int, month: Int, dayOfMonth: Int) {
        val (editTextDate: EditText, selectTimeDialogId: Int) = when (dialogId) {
            SELECT_EVENT_DATE_DIALOG_ID ->
                editTextCreateEventEventDate to SELECT_EVENT_TIME_DIALOG_ID
            SELECT_DANGER_END_DATE_DIALOG_ID ->
                editTextCreateEventDangerEndDate to SELECT_DANGER_END_TIME_DIALOG_ID
            SELECT_NEXT_VACCINE_DATE_DIALOG_ID ->
                editTextCreateEventNextVaccineDate to SELECT_NEXT_VACCINE_TIME_DIALOG_ID
            else -> throw IllegalArgumentException()
        }
        val prevSelectedDateTime = editTextDate.selectedDateTime
        val newSelectedDateTime = prevSelectedDateTime
                ?.copy(year = year, month = month, day = dayOfMonth)
                ?: SelectedDateTime(year = year, month = month, day = dayOfMonth)
        editTextDate.selectedDateTime = newSelectedDateTime
        val args = SelectTimeFragmentArgs(
                selectTimeDialogId,
                newSelectedDateTime.hourOfDay,
                newSelectedDateTime.minute
        )
        showSelectTimeDialog(args)
    }

    override fun onDismissSelectTimeDialog(dialogId: Int) {

    }

    override fun onTimeSet(dialogId: Int, hourOfDay: Int, minute: Int) {
        val editTextDate: EditText = when (dialogId) {
            SELECT_EVENT_TIME_DIALOG_ID -> editTextCreateEventEventDate
            SELECT_DANGER_END_TIME_DIALOG_ID -> editTextCreateEventDangerEndDate
            SELECT_NEXT_VACCINE_TIME_DIALOG_ID -> editTextCreateEventNextVaccineDate
            else -> throw IllegalArgumentException()
        }
        val prevSelectedDateTime = editTextDate.selectedDateTime
        val newSelectedDateTime = prevSelectedDateTime
                ?.copy(hourOfDay = hourOfDay, minute = minute)
                ?: SelectedDateTime(hourOfDay = hourOfDay, minute = minute)
        editTextDate.selectedDateTime = newSelectedDateTime
        editTextDate.setText(
                newSelectedDateTime.utcLong(requireContext()).longDateStr(requireContext())
        )
    }

    override fun onDismissConfirmDialog(dialogId: Int) {

    }

    override fun onAcceptConfirmDialog(dialogId: Int) {
        when (dialogId) {
            SAVE_CONFIRM_DIALOG_ID -> saveEventSubject.onNext(Unit)
            CANCEL_CONFIRM_DIALOG_ID -> onEventCancelled()
        }
    }

    private fun onSaveEvent(): Observable<Unit> = sessionId.take(1).pickValue
            .map { CreateEventArgs(
                    it,
                    animalId,
                    eventDate,
                    eventTypeProcessor?.event,
                    comments
            )
            }
            .filter { it.hasAllRequiredArgs }
            .flatMap { with(it) { soapApiFacade.createEvent(
                    sessionId!!,
                    animalId!!,
                    eventDate!!,
                    event!!,
                    comments
            ) } }
            .observeOn(mainThread())
            .doOnNext {
                val fault = it.body.fault
                if (fault != null) {
                    showErrorDialog(message = fault.reason.text)
                } else {
                    val animalStatus = it.body.izveidotNotikumuResponse!!.izveidotNotikumuResult
                    showInfoDialog(
                            message = getString(R.string.create_event_success).format(animalStatus)
                    )
                    onEventSaved()
                }
            }
            .doOnError { showErrorDialog(message = it.message) }
            .onErrorResumeNext(Observable.empty())
            .map { Unit }

    private fun onEventSaved() {
        requireActivity().supportFragmentManager.popBackStack()
        listener?.onEventSaved()
    }

    private fun onEventCancelled() {
        requireActivity().supportFragmentManager.popBackStack()
        listener?.onEventCancelled()
    }

    private fun onEventTypeSelected(eventType: EventType) {
        val eventTypeProcessor =
                createEventHelper.eventTypeToEventTypeProcessor(eventType, requireView())

        val visibleExtraViews = eventTypeProcessor.visibleExtraViews
        val goneExtraViews = allExtraViews - visibleExtraViews
        defaultViews.forEach {
            view!!.findViewById<View>(it).isVisible = eventTypeProcessor.showDefaultViews
        }
        visibleExtraViews.forEach { view!!.findViewById<View>(it).isVisible = true }
        goneExtraViews.forEach { view!!.findViewById<View>(it).isVisible = false }

        eventTypeProcessor.init()

        this.eventTypeProcessor = eventTypeProcessor
    }

    private fun initViews() {
        textViewCreateEventNameValue.text = animalName
        textViewCreateEventIdsValue.text = animalIdsStr

        val defaultCreateDateTime = fromCurrentCalendar(requireContext())
        editTextCreateEventEventDate.selectedDateTime = defaultCreateDateTime
        editTextCreateEventEventDate.setText(
                defaultCreateDateTime.utcLong(requireContext()).longDateStr(requireContext())
        )
        listOf(
                editTextCreateEventEventDate,
                editTextCreateEventDangerEndDate,
                editTextCreateEventNextVaccineDate
        ).forEach {
            it.isFocusable = false
            it.isClickable = true
            it.isCursorVisible = false
        }

        initAddressCodeViews()
    }

    private fun initAddressCodeViews() {
        autoSuggestAdapter = AutoSuggestAdapter(
                requireContext(),
                R.layout.find_address_dropdown_item,
                R.id.textViewFindAddressItem
        )
        autoCompleteTextViewCreateEventAddress.threshold = 4
        autoCompleteTextViewCreateEventAddress.setAdapter(autoSuggestAdapter)
    }

    private fun subscribe() {
        compositeSubscription.addAll(
                RxCompoundButton.checkedChanges(checkBoxCreateEventKeptAbroad).subscribe {
                    eventTypeProcessor?.checkBoxCreateEventKeptAbroadListener?.invoke(it)
                },
                RxAdapterView.itemSelections(spinnerCreateEventEventType).subscribe {
                    onEventTypeSelected(createEventHelper.eventTypeSelectedPositionToEventType(it))
                },
                RxView.clicks(buttonCreateEventEventSave).subscribe { showSaveConfirmDialog() },
                saveEventSubject
                        .switchMap { onSaveEvent() }
                        .subscribe(
                                { },
                                { throwable -> showErrorDialog(message = throwable.message) }
                        ),
                RxView.clicks(buttonCreateEventEventCancel).subscribe { showCancelConfirmDialog() }
        )
        subscribeDate(
                editTextCreateEventEventDate,
                imageViewCreateEventClearEventDate,
                SELECT_EVENT_DATE_DIALOG_ID
        )
        subscribeDate(
                editTextCreateEventDangerEndDate,
                imageViewCreateEventClearDangerEndDate,
                SELECT_DANGER_END_DATE_DIALOG_ID
        )
        subscribeDate(
                editTextCreateEventNextVaccineDate,
                imageViewCreateEventClearNextVaccineDate,
                SELECT_NEXT_VACCINE_DATE_DIALOG_ID
        )
        subscribeLocationChangeAddress()
    }

    private fun subscribeDate(
            editTextDate: EditText,
            imageViewClearDate: ImageView,
            selectDateDialogId: Int
    ) {
        compositeSubscription.addAll(
                RxView.clicks(editTextDate)
                        .subscribe {
                            val selectedDateTime =
                                    editTextDate.selectedDateTime ?: SelectedDateTime()
                            val args = SelectDateFragmentArgs(
                                    selectDateDialogId,
                                    selectedDateTime.year,
                                    selectedDateTime.month,
                                    selectedDateTime.day
                            )
                            showSelectDateDialog(args)
                        }
                ,
                RxView.clicks(imageViewClearDate)
                        .subscribe {
                            editTextDate.selectedDateTime = null
                            editTextDate.text = null
                        }
        )
    }

    private fun subscribeLocationChangeAddress() {
        compositeSubscription.addAll(
                RxTextView.afterTextChangeEvents(autoCompleteTextViewCreateEventAddress)
                        .map { it.editable()?.toString().orEmpty() }
                        .doOnNext { if (it.isEmpty()) autoCompleteTextViewCreateEventAddress.isEnabled = true }
                        .filter { it.length > 3 }
                        .debounce(300L, TimeUnit.MILLISECONDS)
                        .observeOn(io())
                        .switchMap { address ->
                            sessionId
                                    .take(1)
                                    .pickValue
                                    .flatMap { session ->
                                        soapApiFacade.findAddress(session, address)
                                    }
                        }
                        .observeOn(mainThread())
                        .subscribe(
                                { response ->
                                    autoSuggestAdapter.setDataList(
                                            response
                                                    .body
                                                    .getMekletAdresiResponse
                                                    ?.mekletAdresiResult
                                                    ?.adresesDatiList
                                                    ?.filter { !it.kods.isNullOrEmpty() && !it.nosaukums.isNullOrEmpty() }
                                                    ?.map { AddressData(it.kods!!, it.nosaukums!!) }
                                                    ?: emptyList()
                                    )
                                    autoSuggestAdapter.notifyDataSetChanged()
                                },
                                { throwable -> showErrorDialog(message = throwable.message) }
                        )
                ,
                RxAutoCompleteTextView.itemClickEvents(autoCompleteTextViewCreateEventAddress)
                        .subscribe {
                            autoCompleteTextViewCreateEventAddress.addressCode =
                                    autoSuggestAdapter.data(it.position()).code
                            autoCompleteTextViewCreateEventAddress.isEnabled = false
                        }

                ,
                RxView.clicks(imageViewCreateEventClearAddress)
                        .subscribe {
                            autoCompleteTextViewCreateEventAddress.text = null
                            autoCompleteTextViewCreateEventAddress.addressCode = null
                        }
        )
    }

    private fun showCancelConfirmDialog() {
        val args = SimpleConfirmFragmentArgs(
                dialogId = CANCEL_CONFIRM_DIALOG_ID,
                title = getString(R.string.register_event_cancel_title),
                positiveTextId = R.string.register_event_cancel_positive,
                negativeTextId = R.string.register_event_cancel_negative
        )
        showConfirmDialog(args)
    }

    private fun showSaveConfirmDialog() {
        val args = SimpleConfirmFragmentArgs(
                dialogId = SAVE_CONFIRM_DIALOG_ID,
                title = getString(R.string.register_event_confirm_title),
                positiveTextId = R.string.register_event_confirm_positive
        )
        showConfirmDialog(args)
    }

    private val defaultViews get() = setOf(
            R.id.linearLayoutCreateEventEventDate,
            R.id.textInputLayoutCreateEventComments,
            R.id.linearLayoutCreateEventButtons
    )

    private val allExtraViews get() = setOf(
            R.id.linearLayoutCreateEventDangerEndDate,
            R.id.spinnerCreateEventVaccines,
            R.id.linearLayoutCreateEventNextVaccineDate,
            R.id.textInputLayoutCreateEventVeterinarianCertificate,
            R.id.checkBoxCreateEventKeptAbroad,
            R.id.textInputLayoutCreateEventCountryIsoCode,
            R.id.linearLayoutCreateEventAddress,
            R.id.textInputLayoutCreateEventCountryAddressDetails
    )

    private val sessionId get() = app.userSession.sessionData()
            .map { it.value?.sessionId.opt }

    private val eventDate get() = editTextCreateEventEventDate.selectedDateTime
            ?.utcLong(requireContext())

    private val comments get() = editTextCreateEventComments.text?.toString()

    interface CreateEventFragmentListener {
        fun onEventSaved()
        fun onEventCancelled()
    }

    private val app get() = requireActivity().application as App

    companion object {
        @JvmStatic fun newInstance(
                animalId: String, animalName: String?, animalIdsStr: String?
        ) = CreateEventFragment().apply {
            arguments = bundleOf(
                    ARG_ANIMAL_ID to animalId,
                    ARG_ANIMAL_NAME to animalName,
                    ARG_ANIMAL_IDS to animalIdsStr
            )
        }
    }
}

private data class CreateEventArgs(
        val sessionId: String?,
        val animalId: String?,
        val eventDate: Long?,
        val event: Event?,
        val comments: String?
) {
    val hasAllRequiredArgs get() = !sessionId.isNullOrEmpty()
            && !animalId.isNullOrEmpty()
            && eventDate != null
            && event != null
}

data class SelectedDateTime(
        val year: Int? = null,
        val month: Int? = null,
        val day: Int? = null,
        val hourOfDay: Int? = null,
        val minute: Int? = null
) {
    fun toCalendar(context: Context): Calendar? =
            if (year == null || month == null || day == null || hourOfDay == null || minute == null) null
            else getInstance(context.primaryLocale()).apply { set(year, month, day, hourOfDay, minute) }

    fun utcLong(context: Context): Long? = toCalendar(context)?.timeInMillis

    fun nextYear(context: Context): SelectedDateTime? =
            toCalendar(context)?.apply { add(YEAR, 1) }?.let { fromCalendar(it) }

    fun longDateStr(context: Context) = toCalendar(context)
            ?.timeInMillis
            ?.longDateStr(context)
}

fun fromCurrentCalendar(context: Context): SelectedDateTime =
        fromCalendar(getInstance(context.primaryLocale()))

fun fromCalendar(calendar: Calendar): SelectedDateTime =
        SelectedDateTime(
                calendar.get(YEAR),
                calendar.get(MONTH),
                calendar.get(DAY_OF_MONTH),
                calendar.get(HOUR_OF_DAY),
                calendar.get(MINUTE)
        )