package lv.epasaule.ldcdati;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epasaule.bluetoothutillib.SimpleBluetooth;
import com.epasaule.bluetoothutillib.dialogs.DeviceDialog;
import com.epasaule.bluetoothutillib.utils.BluetoothUtility;
import com.epasaule.bluetoothutillib.utils.SimpleBluetoothListener;

import androidx.appcompat.app.AppCompatActivity;
import lv.epasaule.ldcdati.ui.MainActivity;

/**
 * Example activity and how to use the Simple bluetooth class.
 */
public class BluetoothExampleActivity extends AppCompatActivity {

    private SimpleBluetooth simpleBluetooth;
    private static final int SCAN_REQUEST = 119;
    private static final int CHOOSE_SERVER_REQUEST = 120;
    private Button createServer, connectToServer, sendData, testActivity;
    private TextView connectionState;
    boolean isConnected;
    private EditText dataToSend;
    private String curMacAddress;

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("MAIN", "OnResume Called");
        //this check needs to be here to ensure that the simple bluetooth is not reset.
        //an issue was occuring when a client would connect to a server. When a client
        // connects they have to select a device, that is another activity, so after they
        //select a device, this gets called again and the reference to the original simpleBluetooth
        //object on the client side gets lost. Thus when send is called, nothing happens because it's
        //a different object.
        if(simpleBluetooth == null) {
            simpleBluetooth = new SimpleBluetooth(this, new SimpleBluetoothListener() {
                @Override
                public void onBluetoothDataReceived(byte[] bytes, String data) {
                    //read the data coming in.
                    Toast.makeText(BluetoothExampleActivity.this, "Data: " + data, Toast.LENGTH_SHORT).show();
                    connectionState.setText("Data: " + data);
                    isConnected = false;
                    Log.w("SIMPLEBT", "Data received");
                }

                @Override
                public void onDeviceConnected(BluetoothDevice device) {
                    //a device is connected so you can now send stuff to it
                    Toast.makeText(BluetoothExampleActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                    connectionState.setText("Connected");
                    isConnected = true;
                    Log.w("SIMPLEBT", "Device connected");
                }

                @Override
                public void onDeviceDisconnected(BluetoothDevice device) {
                    // device was disconnected so connect it again?
                    Toast.makeText(BluetoothExampleActivity.this, "Disconnected!", Toast.LENGTH_SHORT).show();
                    connectionState.setText("Disconnected");
                    Log.w("SIMPLEBT", "Device disconnected");
                }
            });
        }
        simpleBluetooth.initializeSimpleBluetooth();
        simpleBluetooth.setInputStreamType(BluetoothUtility.InputStreamType.BUFFERED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        isConnected = false;
        connectionState = (TextView) findViewById(R.id.connection_state);
        connectionState.setText("Disconnected");
        createServer = (Button) findViewById(R.id.create_server_button);
        connectToServer = (Button) findViewById(R.id.connect_to_server);
        dataToSend = (EditText) findViewById(R.id.enterAnimalId);
        sendData = (Button) findViewById(R.id.send_data);
        testActivity = (Button) findViewById(R.id.test_activity_button);
        testActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothExampleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        createServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleBluetooth.createBluetoothServerConnection();
            }
        });

        connectToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curMacAddress != null) {
                    simpleBluetooth.connectToBluetoothServer(curMacAddress);
                } else {
                    simpleBluetooth.scan(CHOOSE_SERVER_REQUEST);
                }
            }
        });

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleBluetooth.sendData(dataToSend.getText().toString());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.scan) {
            simpleBluetooth.scan(SCAN_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_REQUEST || requestCode == CHOOSE_SERVER_REQUEST) {

            if(resultCode == RESULT_OK) {

                curMacAddress = data.getStringExtra(DeviceDialog.DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA);
                boolean paired = simpleBluetooth.getBluetoothUtility()
                        .checkIfPaired(simpleBluetooth.getBluetoothUtility()
                                .findDeviceByMacAddress(curMacAddress));
                String message = paired ? "is paired" : "is not paired";
                Log.i("ActivityResult", "Device " + message);
                if(requestCode == SCAN_REQUEST) {
                    simpleBluetooth.connectToBluetoothDevice(curMacAddress);
                } else {
                    simpleBluetooth.connectToBluetoothServer(curMacAddress);
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleBluetooth.endSimpleBluetooth();
    }
}
