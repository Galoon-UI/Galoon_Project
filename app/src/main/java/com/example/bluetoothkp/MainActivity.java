package com.example.bluetoothkp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements DatabaseBackEnd.asyncResponse {

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    BluetoothAdapter bluetoothAdapter;
    SendReceive sendReceive;
    BluetoothDevice bdDevice;
    public static String bdName = null;
    public static String balance;
    public static String institution;
    public static String number;
    public static String id;
    String condition = "";
    String amount;
    int temp_amount;

    public InputStream inStream;
    public OutputStream outStream;
    public BluetoothSocket bluetoothSocket;

    AlertDialog.Builder builder;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView tvBalance;
    //ImageButton ibSetting;
    //ImageButton ibRun,ibTopUp;
    Button btSetting,btTopUp,btRun,btReset;
    TextView tvStatus,tvDevice;

    int input = 1;
    boolean state = false;

    private static final String APP_NAME = "BluetoothKP";
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        implementListeners();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent in = getIntent();
        String alert_receive = (String) in.getSerializableExtra("alert");
        if(alert_receive.equals("login")){
            id = (String) in.getSerializableExtra("id");
            balance = (String) in.getSerializableExtra("balance");
            institution = (String) in.getSerializableExtra("institution");
            number = (String) in.getSerializableExtra("number");
            String temp = (String) in.getSerializableExtra("bdName");
            if(!temp.equalsIgnoreCase("None")){
                bdName = temp;
            }
            tvDevice.setText("Device : " + bdName);
            tvBalance.setText(balance + " ml");
            btRun.setText("200 ml");
            //tvMessage.setText("id " + id );
        }
        else if(alert_receive.equals("setting")){
            bdName = (String) in.getSerializableExtra("deviceName");
            //Toast.makeText(getApplicationContext(),bdName+"/"+number+"/"+institution,Toast.LENGTH_LONG).show();
            insertDevice(findViewById(android.R.id.content));
            tvDevice.setText("Device : " + bdName);
            tvBalance.setText(balance + " ml");
        }
        if(bdName != null){
            Set<BluetoothDevice> bd = bluetoothAdapter.getBondedDevices();
            if(bd.size() > 0){
                for(BluetoothDevice device : bd){
                    if(device.getName().equals(bdName)){
                        bdDevice = device;
                        //Toast.makeText(getApplicationContext(),"SUCCESS!",Toast.LENGTH_LONG).show();
                    }
                }
            }
            Client client = new Client(bdDevice);
            client.start();
        }

    }


    private void implementListeners() {
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bdName != null){
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setIndeterminate(true);
                    tvStatus.setText("Status : Connecting");
                    progressDialog.setMessage("Reconnecting...");
                    progressDialog.show();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    Set<BluetoothDevice> bd = bluetoothAdapter.getBondedDevices();
                                    if(bd.size() > 0){
                                        for(BluetoothDevice device : bd){
                                            if(device.getName().equals(bdName)){
                                                bdDevice = device;
                                                //Toast.makeText(getApplicationContext(),"SUCCESS!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                    Client client = new Client(bdDevice);
                                    client.start();
                                }
                            }, 300);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Device sedang digunakan!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TopUp.class);
                intent.putExtra("id",id);
                intent.putExtra("institution",institution);
                intent.putExtra("number",number);
                startActivity(intent);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = findViewById(checkedId);
                String temp = radioButton.getText().toString();
                if(temp.equals("200 ml")){
                    input = 1;
                    btRun.setText("200 ml");
                    Toast.makeText(getApplicationContext(),"Changed to 200 ml",Toast.LENGTH_LONG).show();
                }
                else if(temp.equals("400 ml")){
                    input = 2;
                    btRun.setText("400 ml");
                    Toast.makeText(getApplicationContext(),"Changed to 400 ml",Toast.LENGTH_LONG).show();
                }
                else if(temp.equals("600 ml")){
                    input = 3;
                    btRun.setText("600 ml");
                    Toast.makeText(getApplicationContext(),"Changed to 600 ml",Toast.LENGTH_LONG).show();
                }
            }

        });

        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingBluetooth.class));
            }
        });

        btRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvStatus.getText().toString().equals("Status : Connected") || tvStatus.getText().toString().equals("Status : Received")){
                    if(input == 1){
                        if(Integer.parseInt(balance) >= 200){
                            String temp = "1";
                            sendReceive.write(temp.getBytes());
                            state = true;
                            //condition = "F";
                            Toast.makeText(getApplicationContext(),"Transaksi Berhasil",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Saldo anda tidak mencukupi",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(input == 2){
                        if(Integer.parseInt(balance) >= 400){
                            String temp = "2";
                            sendReceive.write(temp.getBytes());
                            state = true;
                            //condition = "F";
                            Toast.makeText(getApplicationContext(),"Transaksi Berhasil",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Saldo anda tidak mencukupi",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(input == 3){
                        if(Integer.parseInt(balance) >= 600){
                            String temp = "3";
                            sendReceive.write(temp.getBytes());
                            state = true;
                            //condition = "F";
                            Toast.makeText(getApplicationContext(),"Transaksi Berhasil",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Saldo anda tidak mencukupi",Toast.LENGTH_LONG).show();
                        }
                    }
                    if(state){
                        //onInsert(findViewById(android.R.id.content));
                        if(input == 1){
                            temp_amount = 200;
                        }
                        else if(input == 2){
                            temp_amount = 400;
                        }
                        else if(input == 3){
                            temp_amount = 600;
                        }
                        int int_balance = Integer.parseInt(balance) - temp_amount;
                        balance = Integer.toString(int_balance);
                        tvBalance.setText(balance + "ml");
                        onInsert(findViewById(android.R.id.content));
                        //onUpdateUser(findViewById(android.R.id.content));
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    state = false;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onBack(View view) {
        String type = "resume_main_activity";

        DatabaseBackEnd backgroundWorker = new DatabaseBackEnd(this);
        backgroundWorker.delegate = this;
        backgroundWorker.execute(type,id);
    }

    private void onInsert(View view) {
        //Date c = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        //String transaction_date = df.format(c);

        if(input == 1){
            amount = "200";
        }
        else if(input == 2){
            amount = "400";
        }
        else if(input == 3){
            amount = "600";
        }
        String type = "insert_user_transaction";
        DatabaseBackEnd databaseBackEnd = new DatabaseBackEnd(this);
        databaseBackEnd.delegate = this;
        databaseBackEnd.execute(type,number,amount,bdName,institution,balance);
    }

    private void insertDevice(View view) {
        String type = "update_user_device";
        DatabaseBackEnd databaseBackEnd = new DatabaseBackEnd(this);
        databaseBackEnd.delegate = this;
        databaseBackEnd.execute(type,number,institution,bdName);
    }

    private void onUpdateUser(View view) {
        String type = "update_user_list";
        DatabaseBackEnd databaseBackEnd = new DatabaseBackEnd(this);
        databaseBackEnd.delegate = this;
        databaseBackEnd.execute(type,number,institution,balance);
    }

    @Override
    public void onStop() {
        super.onStop();
        //onUpdateUser(findViewById(android.R.id.content));
        resetConnection();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onBack(findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBack(findViewById(android.R.id.content));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //onUpdateUser(findViewById(android.R.id.content));
        resetConnection();
    }

    private void findViewByIds() {
        radioGroup = findViewById(R.id.radiogroup);
        tvBalance = findViewById(R.id.tvBalance);
        btSetting = findViewById(R.id.btSetting);
        btRun = findViewById(R.id.btRun);
        tvStatus = findViewById(R.id.tvStatus);
        //tvMessage = findViewById(R.id.tvMessage);
        tvDevice = findViewById(R.id.tvDevice);
        btTopUp = findViewById(R.id.btTopUp);
        btReset = findViewById(R.id.btReset);

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you wish to perform another transaction?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetConnection();
                dialog.dismiss();
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case STATE_LISTENING:
                    tvStatus.setText("Status : Listening");
                    break;
                case STATE_CONNECTING:
                    tvStatus.setText("Status : Connecting");
                    break;
                case STATE_CONNECTED:
                    tvStatus.setText("Status : Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    tvStatus.setText("Status : Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    tvStatus.setText("Status : Received");
                    byte[] readBuff = (byte[]) msg.obj;
                    condition = new String(readBuff,0,msg.arg1);
                    //tvMessage.setText(condition);
                    break;
                default:

            }
            return true;
        }
    });

    @Override
    public void processFinish(String output) {
        //tvMessage.setText(output);
        String sub = output.substring(0,5);
        if(sub.equalsIgnoreCase("{\"r\":")){
            //Toast.makeText(getApplicationContext(),sub,Toast.LENGTH_LONG).show();
            String balanceUpdated = output.substring(5,output.length()-1);
            JSONObject jsonobject = null;
            JSONArray myListsAll = null;
            try {
                myListsAll = new JSONArray(balanceUpdated);
                jsonobject = (JSONObject) myListsAll.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            balance = jsonobject.optString("balance");
            tvBalance.setText(balance + " ml");
        }
        else{
            Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
        }
    }

    private class Server extends Thread {
        private BluetoothServerSocket serverSocket;

        public Server(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;
            while(socket == null){
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
                if(socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    //Receive/Send
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class Client extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public Client(BluetoothDevice device1){
            this.device = device1;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }

        }
    }

    private void resetConnection() {
        /**
        if (inStream != null) {
            try {
                inStream.close();
            } catch (Exception e)
            {

            }
            inStream = null;
        }
        **/
        if (outStream != null) {
            try {
                outStream.close();
            } catch (Exception e) {

            }
            outStream = null;
            tvStatus.setText("Status : Disconnected");
        }

        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {

            }
            bluetoothSocket = null;
            tvStatus.setText("Status : Disconnected");
        }
    }

    private class SendReceive extends Thread{

        private SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inStream = tempIn;
            outStream = tempOut;

        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes = inStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void write(byte[] bytes){
            try {
                outStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
