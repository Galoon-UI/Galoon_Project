package com.example.bluetoothkp;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener,DatabaseBackEnd.asyncResponse {

    Button btSignUp;
    TextView tvLogin;
    EditText etNumber,etPassword;
    Spinner spInstitution;
    private static final String[] arrString = {"SMAN 1 Depok","SMAN 5 Jakarta","SMAN 21 Jakarta","SMA 4 Thamrin",
                                            "SMA Canisius College","SMAK Penabur 1 Jakarta","SMA 2 Bogor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewByIds();
        implementListeners();
    }

    private void onInsert(View view){
        String userNumber = etNumber.getText().toString();
        String userPassword = etPassword.getText().toString();
        String userInstitute = spInstitution.getSelectedItem().toString();
        //Date c = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //String joinDate = df.format(c);
        //String joinDate = "2018-10-10";
        String balance = "10000";
        if(userNumber.length() >= 8 && userPassword.length() >= 3){
            String type = "insert_user_list";
            DatabaseBackEnd databaseBackEnd = new DatabaseBackEnd(this);
            databaseBackEnd.delegate = this;
            databaseBackEnd.execute(type,userNumber,userPassword,userInstitute,balance);
        }
        else if(userNumber.length() < 8){
            Toast.makeText(getApplicationContext(),"Nomor HP anda tidak valid",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Panjang Password minimal 3 karakter",Toast.LENGTH_LONG).show();
        }
    }

    private void implementListeners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignUp.this,android.R.layout.simple_spinner_item,arrString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInstitution.setAdapter(adapter);
        spInstitution.setOnItemSelectedListener(this);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kirim data ke server
                onInsert(findViewById(android.R.id.content));
                finish();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void findViewByIds() {
        btSignUp = findViewById(R.id.btSignUp);
        tvLogin = findViewById(R.id.tvLogin);
        etNumber = findViewById(R.id.etNumber);
        etPassword = findViewById(R.id.etPassword);
        spInstitution = findViewById(R.id.spInstitution);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void processFinish(String output) {

    }
}
