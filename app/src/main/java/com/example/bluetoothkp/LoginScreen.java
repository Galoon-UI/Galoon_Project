package com.example.bluetoothkp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginScreen extends AppCompatActivity implements DatabaseBackEnd.asyncResponse{

    Button btLogin;
    TextView tvSignUp;
    EditText etNumber,etPassword;
    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BLUETOOTH = 1;
    String balance,institution,number,id,bdName;
    int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_login_screen);

        status = 0;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Intent intentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentEnable,REQUEST_ENABLE_BLUETOOTH);
        }
        findViewByIds();
        implementListeners();
        //onLogin();
    }

    private void onLogin(View view) {
        String number = etNumber.getText().toString();
        String password = etPassword.getText().toString();
        String type = "login";

        DatabaseBackEnd backgroundWorker = new DatabaseBackEnd(this);
        backgroundWorker.delegate = this;
        backgroundWorker.execute(type,number,password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        status = 0;
    }

    private void implementListeners() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin(findViewById(android.R.id.content));
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this,SignUp.class));
            }
        });
    }

    private void findViewByIds() {
        btLogin = findViewById(R.id.btLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        etNumber = findViewById(R.id.etNumber);
        etPassword = findViewById(R.id.etPassword);
    }

    @Override
    public void processFinish(String output) {
        try {
            output = output.substring(5,output.length()-1);
            if(output.length() >= 5){
                status = 1;
            }
            else{
                status  = 0;
            }
            //Toast.makeText(getApplicationContext(),String.valueOf(status),Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
            JSONArray myListsAll = new JSONArray(output);
            JSONObject jsonobject = (JSONObject) myListsAll.get(0);
            id = jsonobject.optString("id");
            balance = jsonobject.optString("balance");
            institution = jsonobject.optString("institution");
            bdName = jsonobject.optString("Default_Device");
            number = jsonobject.optString("number");

            final ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            if(status == 1){
                                //onLogin(findViewById(android.R.id.content));
                                Intent intent = new Intent(LoginScreen.this,MainActivity.class);
                                String alert = "login";
                                intent.putExtra("alert",alert);
                                intent.putExtra("balance",balance);
                                intent.putExtra("institution",institution);
                                intent.putExtra("number",number);
                                intent.putExtra("bdName",bdName);
                                intent.putExtra("id",id);
                                startActivity(intent);
                                //Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Password Salah",Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 500);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getApplicationContext(),output,Toast.LENGTH_LONG).show();
    }
}


