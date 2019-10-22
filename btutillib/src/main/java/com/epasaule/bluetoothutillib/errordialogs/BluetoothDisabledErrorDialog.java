package com.epasaule.bluetoothutillib.errordialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothDisabledErrorDialog extends DialogFragment {


    public static void showDialog(Context context) {

        BluetoothDisabledErrorDialog bluetoothDisabledErrorDialog =
                BluetoothDisabledErrorDialog.newInstance();
        bluetoothDisabledErrorDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Error");

    }

    public static BluetoothDisabledErrorDialog newInstance() {
        BluetoothDisabledErrorDialog bded = new BluetoothDisabledErrorDialog();
        bded.setCancelable(false);
        return bded;
    }

    @NonNull @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity())
                .title("Bluetooth Disabled")
                .content("Bluetooth was disabled, would you like to re-enable?")
                .positiveText("Yes")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });
        return dialog.build();
    }
}
