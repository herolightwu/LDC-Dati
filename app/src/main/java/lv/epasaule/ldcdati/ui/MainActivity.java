package lv.epasaule.ldcdati.ui;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.epasaule.bluetoothutillib.abstracts.BaseBluetoothActivity;
import com.epasaule.bluetoothutillib.utils.SimpleBluetoothListener;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import kotlin.Unit;
import lv.epasaule.ldcdati.App;
import lv.epasaule.ldcdati.R;
import lv.epasaule.ldcdati.Utils;
import lv.epasaule.ldcdati.adapter.ResultRow;
import lv.epasaule.ldcdati.adapter.ResultsAdapter;
import lv.epasaule.ldcdati.barcodereader.BarcodeCaptureActivity;
import lv.epasaule.ldcdati.json.JsonParser;
import lv.epasaule.ldcdati.json.JsonParser.AnimalsData;
import lv.epasaule.ldcdati.network.soap.SoapApiFacade;
import lv.epasaule.ldcdati.network.soap.animal.AnimalResponseEnvelope;
import lv.epasaule.ldcdati.network.soap.animal.AnimalResponseEnvelope.Body.GetDzivniekaDatiResponse.GetDzivniekaDatiResult.Identifikatori.DzivniekaIdentifikators;
import lv.epasaule.ldcdati.network.soap.common.Fault;
import lv.epasaule.ldcdati.network.web.WebApiFacade;
import lv.epasaule.ldcdati.session.SessionData;
import lv.epasaule.ldcdati.ui.createevent.CreateEventFragment;
import lv.epasaule.ldcdati.ui.createevent.CreateEventFragment.CreateEventFragmentListener;
import lv.epasaule.ldcdati.ui.login.LoginDialogFragment;
import lv.epasaule.ldcdati.ui.login.LogoutDialogFragment;
import lv.epasaule.ldcdati.util.Opt;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static lv.epasaule.ldcdati.Constants.PATTERN_ANIMAL_CODE_OF_12_DIGITS;
import static lv.epasaule.ldcdati.ui.dialog.SimpleInfoDialogFragmentKt.DEFAULT_DIALOG_ID;
import static lv.epasaule.ldcdati.ui.dialog.SimpleInfoDialogFragmentKt.showErrorDialog;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

// TODO:
// 1) logout on session expired error: ERROR_SESSIONNOTFOUND
public class MainActivity extends BaseBluetoothActivity
        implements CreateEventFragmentListener {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "MainActivity";

    // use a compound button so either checkbox or switch widgets work.
    private Toolbar mToolbar;
    private LinearLayout mainScreen;
    private RecyclerView mRVResults;
    private LinearLayout mLLUsageHint;
    private TextView tvWebResponseCount;
    private Button buttonRegisterEvent;
    private ResultsAdapter mAdapter;
    private EditText mETAnimalId;
    private ImageView ivClearAnimalId;
    private boolean mAutoFocus = true;
    private boolean mUseFlash;
    private SoapApiFacade soapApiFacade = SoapApiFacade.INSTANCE;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private PublishSubject<String> getAnimalDataSubject = PublishSubject.create();
    private PublishSubject<Unit> optionsMenuCreated = PublishSubject.create();
    private PublishSubject<Unit> loginClickSubject = PublishSubject.create();

    @Override
    public SimpleBluetoothListener getSimpleBluetoothListener() {
        return new SimpleBluetoothListener() {
            @Override
            public void onBluetoothDataReceived(byte[] bytes, String data) {
                super.onBluetoothDataReceived(bytes, data);
                mETAnimalId.setText(data);
                getAnimalData();
            }

            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                super.onDeviceConnected(device);
            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device) {
                super.onDeviceDisconnected(device);
            }

            @Override
            public void onDiscoveryStarted() {
                super.onDiscoveryStarted();
            }

            @Override
            public void onDiscoveryFinished() {
                super.onDiscoveryFinished();
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {
                super.onDevicePaired(device);
            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {
                super.onDeviceUnpaired(device);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setIcon(R.drawable.ic_bar_logo);

        mainScreen = findViewById(R.id.mainScreen);

        mRVResults = (RecyclerView) findViewById(R.id.list_view);
        mRVResults.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ResultsAdapter(Collections.<ResultRow>emptyList());

        mRVResults.setAdapter(mAdapter);
        mRVResults.setHasFixedSize(true);
        mRVResults.setItemAnimator(new SlideInLeftAnimator());
        mRVResults.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mLLUsageHint = (LinearLayout) findViewById(R.id.usage_hint);
        tvWebResponseCount = (TextView) findViewById(R.id.web_response_count);
        mETAnimalId = (EditText) findViewById(R.id.enterAnimalId);
        mETAnimalId
                .setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        getAnimalData();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                });
        ivClearAnimalId = findViewById(R.id.imageViewClearAnimalId);

        Button button = (Button) findViewById(R.id.send_data);
        button.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            getAnimalData();
        });

        findViewById(R.id.scan_barcode).setOnClickListener(v -> {
            // launch barcode activity.
            Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, mAutoFocus);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, mUseFlash);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        });

        buttonRegisterEvent = findViewById(R.id.buttonRegisterEvent);

        Observable<TextViewAfterTextChangeEvent> animalIdReset = RxTextView.afterTextChangeEvents(mETAnimalId)
                .filter(event -> event.editable() == null || event.editable().toString().isEmpty())
                .share();

        compositeSubscription.addAll(
                Observable.combineLatest(
                        sessionData(),
                        optionsMenuCreated.take(1),
                        (sessionData, __) -> sessionData
                )
                        .observeOn(mainThread())
                        .subscribe(
                                sessionData -> {
                                    MenuItem menuItem = mToolbar.getMenu().findItem(R.id.login);
                                    if (menuItem != null) {
                                        if (sessionData.isPresent()) {
                                            menuItem.setIcon(R.drawable.ic_face_logged_in);
                                        } else {
                                            menuItem.setIcon(R.drawable.ic_face_logged_out);
                                        }
                                    }
                                }
                        ),

                loginClickSubject
                        .switchMap(__ -> sessionData().take(1).map(Opt::isPresent))
                        .subscribe(hasSession -> {
                            if (hasSession) showLogoutDialog();
                            else showLoginDialog();
                        }),

                animalIdReset.subscribe(__ -> mETAnimalId.setEnabled(true)),

                RxView.clicks(ivClearAnimalId)
                        .subscribe(__ -> mETAnimalId.setText(null)),

                getAnimalDataSubject
                        .doOnNext(__ -> mETAnimalId.setEnabled(false))
                        .switchMap(this::getAnimalDataWeb)
                        .subscribe(),

                Observable.combineLatest(
                        sessionId(),
                        Observable.merge(
                                animalIdReset.map(__ -> new Pair<Boolean, String>(false, null)),
                                getAnimalDataSubject.map(animalId -> new Pair<>(true, animalId))
                        ),
                        Pair::new
                )
                        .switchMap(pair -> {
                                    if (!pair.first.isPresent() || !pair.second.first) return Observable.just(false);
                                    else return showRegisterButton(pair.first.getValue(), pair.second.second);
                                }
                        )
                        .observeOn(mainThread())
                        .doOnError(throwable -> showErrorDialog(
                                this,
                                DEFAULT_DIALOG_ID,
                                throwable.getMessage(),
                                getString(R.string.app_error)
                                )
                        )
                        .onErrorResumeNext(Observable.empty())
                        .subscribe(show -> buttonRegisterEvent.setVisibility(show ? VISIBLE : GONE)),

                RxView.clicks(buttonRegisterEvent).switchMap(unit -> sessionData().take(1)
                        .observeOn(mainThread())
                        .doOnNext(optSessionData -> {
                            if (!optSessionData.isPresent()) showLoginDialog();
                        })
                        .filter(Opt::isPresent)
                        .map(sessionDataOpt -> sessionDataOpt.getValue().getSessionId())
                        .flatMap(sessionId -> {
                                    String animalId;
                                    if (mETAnimalId.getText() == null
                                            || isEmpty(animalId = mETAnimalId.getText().toString())) {
                                        return Observable.empty();
                                    }
                                    return soapApiFacade.animal(sessionId, animalId)
                                            .doOnNext(response -> {
                                                Fault fault = response.getBody().getFault();
                                                if (fault != null) {
                                                    showErrorDialog(
                                                            this,
                                                            DEFAULT_DIALOG_ID,
                                                            fault.getReason().getText(),
                                                            getString(R.string.app_error)
                                                    );
                                                }
                                            })
                                            .filter(AnimalResponseEnvelope::isHomeAnimal)
                                            .map(response -> {
                                                String name = response.getName();
                                                List<DzivniekaIdentifikators> rawIds = response.getIds();
                                                List<String> ids = new ArrayList<>();
                                                for (DzivniekaIdentifikators identifikators : rawIds) {
                                                    ids.add(identifikators.getApraksts().split(" ")[0]);
                                                }
                                                StringBuilder idsStr = new StringBuilder();
                                                for (int i = 0; i < ids.size(); i++) {
                                                    if (i > 0) idsStr.append(", ");
                                                    idsStr.append(ids.get(i));
                                                }
                                                return new Pair<>(name, idsStr.toString());
                                            });
                                }
                        )
                        .observeOn(mainThread())
                        .doOnError(throwable -> showErrorDialog(
                                this,
                                DEFAULT_DIALOG_ID,
                                throwable.getMessage(),
                                getString(R.string.app_error)
                                )
                        )
                        .onErrorResumeNext(Observable.empty())
                )
                        .observeOn(mainThread())
                        .subscribe(this::navigateToCreateEvent)
        );

        // Must be done during an initialization phase like onCreate
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        Log.i(TAG, "All permissions granted.");
                    } else {
                        Log.i(TAG, "Some or all permissions are not granted.");
                        Toast.makeText(MainActivity.this, R.string.app_stop_no_permissions, Toast.LENGTH_LONG)
                                .show();
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (tvWebResponseCount.isShown()) {
            mLLUsageHint.setVisibility(VISIBLE);
            mRVResults.setVisibility(GONE);
            tvWebResponseCount.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        optionsMenuCreated.onNext(Unit.INSTANCE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                loginClickSubject.onNext(Unit.INSTANCE);
                return true;
            case R.id.setup_camera:
                showCameraPopup();
                return true;
            case R.id.scan_bluetooth:
                launchBluetoothScanActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    mETAnimalId.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    getAnimalData();
                } else {
//                  statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
//              statusMessage.setVisibility(View.VISIBLE);
//              statusMessage.setText(String.format(getString(R.string.barcode_error),
//                      CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBluetoothEnabled() {
        super.onBluetoothEnabled();
        simpleBluetooth.isInitialized = true;
        Toast.makeText(this, "BT Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceSelected(String macAddress) {
        super.onDeviceSelected(macAddress);
        Toast.makeText(this, "Device " + macAddress, Toast.LENGTH_SHORT).show();
    }

    private void showLoginDialog() {
        LoginDialogFragment.getNewInstance().show(
                getSupportFragmentManager(), "LoginDialogFragment"
        );
    }

    private void showLogoutDialog() {
        LogoutDialogFragment.getNewInstance().show(
                getSupportFragmentManager(), "LogoutDialogFragment"
        );
    }

    private Observable<Unit> getAnimalDataWeb(String animalId) {
        return WebApiFacade.getInstance()
                .sendData(animalId)
                .observeOn(mainThread())
                .doOnNext(json -> {
                            final AnimalsData animalsData = JsonParser.jsonToResultRows(json);
                            int animalCounter = animalsData.counter();
                            List<ResultRow> resultRows = animalsData.results();
                            if (resultRows != null && !resultRows.isEmpty()) {
                                mRVResults.setVisibility(VISIBLE);
                                new Handler().post(() -> mAdapter.set(animalsData.results()));
                            } else {
                                mRVResults.setVisibility(GONE);
                            }
                            mLLUsageHint.setVisibility(GONE);
                            tvWebResponseCount.setVisibility(VISIBLE);
                            tvWebResponseCount.setText(String.format(getString(R.string.server_response_result), animalCounter));
                        }
                )
                .doOnError( throwable -> {
                            mRVResults.setVisibility(GONE);
                            mLLUsageHint.setVisibility(GONE);
                            tvWebResponseCount.setVisibility(VISIBLE);
                            tvWebResponseCount.setText(String.format(getString(R.string.server_response_result), 0));
                            showErrorDialog(
                                    MainActivity.this,
                                    DEFAULT_DIALOG_ID,
                                    throwable.getMessage(),
                                    getString(R.string.app_error)
                            );
                            Log.e(TAG, throwable.getMessage());

                        }
                )
                .onErrorResumeNext(Observable.empty())
                .map(__ -> Unit.INSTANCE);
    }

    private Observable<Boolean> showRegisterButton(String sessionId, String animalId) {
        return soapApiFacade.animal(sessionId, animalId)
                .map(AnimalResponseEnvelope::isHomeAnimal)
                .observeOn(mainThread())
                .onErrorResumeNext(Observable.just(false));
    }

    private void navigateToCreateEvent(Pair<String, String> pair) {
        String animalId;
        if (mETAnimalId.getText() == null || isEmpty(animalId = mETAnimalId.getText().toString())) {
            return;
        }
        mainScreen.setVisibility(GONE);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("createEvent");
        if (fragment == null) {
            fragment = CreateEventFragment.newInstance(
                    animalId,
                    pair.first,
                    pair.second
            );
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayoutContainer, fragment, "createEvent")
                .addToBackStack(null)
                .commit();
    }

    private void launchBluetoothScanActivity() {
        // launch bluetooth scan activity.
        if (simpleBluetooth.initializeSimpleBluetooth()) {
            onBluetoothEnabled();
        }
    }

    private void showCameraPopup() {
        PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.camera_popup_window, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        CheckBox cbAutoFocus = (CheckBox) layout.findViewById(R.id.auto_focus);
        CheckBox cbUseFlash = (CheckBox) layout.findViewById(R.id.use_flash);

        cbAutoFocus.setChecked(mAutoFocus);
        cbUseFlash.setChecked(mUseFlash);

        cbAutoFocus.setOnCheckedChangeListener((buttonView, isChecked) -> mAutoFocus = isChecked);

        cbUseFlash.setOnCheckedChangeListener((buttonView, isChecked) -> mUseFlash = isChecked);

        // Clear the default translucent background
        popup.showAsDropDown(findViewById(R.id.setup_camera_anchor));
    }

    private void getAnimalData() {
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            mRVResults.setVisibility(GONE);
            mLLUsageHint.setVisibility(GONE);
            tvWebResponseCount.setVisibility(VISIBLE);
            tvWebResponseCount.setText(getString(R.string.no_network_available));
            Toast.makeText(
                    MainActivity.this,
                    getString(R.string.no_network_available),
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        String animalId = mETAnimalId.getText().toString();
        if (animalId.length() == 12 && animalId.startsWith("0")) {
            Matcher matcher = PATTERN_ANIMAL_CODE_OF_12_DIGITS.matcher(animalId);
            if (matcher.matches()) {
                animalId = "LV" + animalId;
            }
        }
        getAnimalDataSubject.onNext(animalId);
    }

    private Observable<Opt<SessionData>> sessionData() {
        return ((App) getApplication()).getUserSession().sessionData();
    }

    private Observable<Opt<String>> sessionId() {
        return ((App) getApplication()).getUserSession().sessionId();
    }

    @Override public void onEventSaved() {
        mainScreen.setVisibility(VISIBLE);
    }

    @Override public void onEventCancelled() {
        mainScreen.setVisibility(VISIBLE);
    }

}
