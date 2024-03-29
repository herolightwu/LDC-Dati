package com.epasaule.bluetoothutillib.broadcasts;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Paul on 10/7/2015.
 */
public class BluetoothPairingReceiver extends BroadcastReceiver {

    private Callback mCallback;

    public BluetoothPairingReceiver(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * Register this receiver with a series of intent filters.
     * @param context the context that will register the receiver.
     * @param callback the callback to notify when there are changes.
     * @return an instance of the {@code BluetoothStateReceiver}
     */
    public static BluetoothPairingReceiver register(Context context, Callback callback) {
        BluetoothPairingReceiver receiver = new BluetoothPairingReceiver(callback);
        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(receiver, intent);
        return receiver;
    }

    /**
     * Safe method to unregister the receiver in case of errors. Still unregisters the receiver for
     * all filters it has been registered for.
     * @param context the context that had registered the receiver
     * @param receiver the receiver that was registered.
     */
    public static void safeUnregister(Context context, BluetoothPairingReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                //paired
            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                //unpaired
            }

        }
    }

    public interface Callback {
        public void onDevicePaired(BluetoothDevice device);
        public void onDeviceUnpaired(BluetoothDevice device);
    }
}
