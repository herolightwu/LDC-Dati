package com.epasaule.bluetoothutillib.abstracts;


import android.content.Intent;
import android.os.Bundle;

import com.epasaule.bluetoothutillib.SimpleBluetooth;
import com.epasaule.bluetoothutillib.dialogs.DeviceDialog;
import com.epasaule.bluetoothutillib.utils.BluetoothUtility;
import com.epasaule.bluetoothutillib.utils.SimpleBluetoothListener;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Paul Tsouchlos
 *
 * This is a base activity to use when you want an activity that handles the following:
 * - Enabling bluetooth.
 * - Scanning for devices.
 * - Connecting to a device.
 * - Receiving data from the device.
 * - Sending data to the device.
 */
public abstract class BaseBluetoothActivity extends AppCompatActivity {

    /**
     * Must be overriden witha  simple bluetooth listener.
     * @return a SimpleBluetoothListener. Now you don't have to override all the methods!
     */
    public abstract SimpleBluetoothListener getSimpleBluetoothListener();

    /**
     * The {@code SimpleBluetooth} object for this activity.
     */
    protected SimpleBluetooth simpleBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpleBluetooth = new SimpleBluetooth(this, getSimpleBluetoothListener());
/*
        if(simpleBluetooth.initializeSimpleBluetooth()) {
            onBluetoothEnabled();
        }
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == BluetoothUtility.REQUEST_BLUETOOTH) {
            onBluetoothEnabled();
        } else if(resultCode == RESULT_OK && requestCode == BluetoothUtility.REQUEST_BLUETOOTH_SCAN) {
            String macAddress = data.getStringExtra(DeviceDialog.DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA);
            onDeviceSelected(macAddress);
        } else if(resultCode == RESULT_OK && requestCode == BluetoothUtility.REQUEST_MAKE_DEVICE_DISCOVERABLE) {
            // device is discoverable now.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This is always called by the activity and indicates that bluetooth is now enabled.
     * By default the Activity will request for a scan of nearby devices.
     */
    public void onBluetoothEnabled() {
        requestScan();
    }

    /**
     * This method is called after you call {#requestScan} and a device is selected from the list.
     * By default, the activity will attempt to connect to the device.
     * @param macAddress, the macAddress of the selected device.
     */
    public void onDeviceSelected(String macAddress) {
        simpleBluetooth.connectToBluetoothDevice(macAddress);
    }

    /**
     * Sends data to the currently connected device.
     * @param data the string to send to the device.
     */
    public void sendData(String data) {
        simpleBluetooth.sendData(data);
    }

    /**
     * Call this to request a scan and connect to a device.
     */
    public void requestScan() {
        simpleBluetooth.scan(BluetoothUtility.REQUEST_BLUETOOTH_SCAN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleBluetooth.endSimpleBluetooth();
    }

    public SimpleBluetooth getSimpleBluetooth() {
        return this.simpleBluetooth;
    }

}
