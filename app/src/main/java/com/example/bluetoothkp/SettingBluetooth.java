package com.example.bluetoothkp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SettingBluetooth extends AppCompatActivity implements Serializable {

    Button btList;
    ListView lvList;
    //TextView tvStatus;
    ImageButton ibHome;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] arrDevice;
    String[] arrDevName;
    String bdName = null;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "BluetoothKP";
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bluetooth);

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        implementListeners();
    }


    private void implementListeners() {
        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bd = bluetoothAdapter.getBondedDevices();
                arrDevName = new String[bd.size()];
                int index = 0;
                arrDevice = new BluetoothDevice[bd.size()];

                if(bd.size() > 0){
                    for(BluetoothDevice device : bd){
                        arrDevice[index] = device;
                        arrDevName[index] = device.getName();
                        index++;
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arrDevName);
                lvList.setAdapter(arrayAdapter);
            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bdName = arrDevName[position];
                Toast.makeText(getApplicationContext(),bdName,Toast.LENGTH_LONG).show();
                //Client client = new Client(arrDevice[position]);
                //client.start();
                //tvStatus.setText("Connecting");
            }
        });

        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(SettingBluetooth.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading ...");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if(bdName != null){
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(SettingBluetooth.this,MainActivity.class);
                                    String alert = "setting";
                                    intent.putExtra("deviceName",bdName);
                                    intent.putExtra("alert",alert);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Device not Selected",Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 500);

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case STATE_LISTENING:
                    //tvStatus.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    //tvStatus.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    //tvStatus.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    //tvStatus.setText("Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    //tvStatus.setText("Received");
                    byte[] readBuff = (byte[]) msg.obj;
                    String txtRead = new String(readBuff,0,msg.arg1);
                    break;
                default:

            }
            return true;
        }
    });

    //inisialisasi Semua view awal
    private void findViewByIdes() {
        btList = (Button) findViewById(R.id.btList);
        lvList = (ListView) findViewById(R.id.lvList);
        //tvStatus = (TextView) findViewById(R.id.tvStatus);
        ibHome = findViewById(R.id.ibHome);

    }

}
