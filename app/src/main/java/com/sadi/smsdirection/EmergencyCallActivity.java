package com.sadi.smsdirection;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sadi.smsdirection.emergency.SmsReceiver;

public class EmergencyCallActivity extends AppCompatActivity {

    EditText phoneNumberEt;
    Spinner sensibilitySp;
    Switch emergencyCallSw;
    LinearLayout callServiceLo;
    int switchStatus;
    String ePhoneNumber;
    String sensibility;
    int indexOfSp;
    int shakeValue;
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int random;
    SmsReceiver receiver;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Emergency Call");
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        initialize();

        sharedPreferencesData();
        switchStatus();
        switchClick();
        sensibilityTypeSpinner();

    }


    private void sensibilityTypeSpinner() {


        if (switchStatus == 1) {
            sensibilitySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sensibility = (String) parent.getItemAtPosition(position);
                    saveData();
                    sharedPreferencesData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void sharedPreferencesData() {

        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        switchStatus = sharedPreferences.getInt("switchStatuss", 0);
        ePhoneNumber = sharedPreferences.getString("ePhoneNumber", "");
        sensibility = sharedPreferences.getString("sensibilitys", "Medium");


        if (sensibility.equals("Very High")) {
            indexOfSp = 0;
            shakeValue = 8;
        } else if (sensibility.equals("High")) {
            indexOfSp = 1;
            shakeValue = 11;
        } else if (sensibility.equals("Medium")) {
            indexOfSp = 2;
            shakeValue = 14;
        } else if (sensibility.equals("Low")) {
            indexOfSp = 3;
            shakeValue = 17;
        } else if (sensibility.equals("Very Low")) {
            indexOfSp = 4;
            shakeValue = 20;
        }

        editor.putInt("shakeValue", shakeValue);
        editor.commit();
    }

    private void saveData() {
        editor.putInt("switchStatuss", switchStatus);

        Operations.SaveToSharedPreference(getApplicationContext(), "ePhoneNumbers", ePhoneNumber);

        Operations.SaveToSharedPreference(getApplicationContext(), "sensibilitys", sensibility);
        phoneNumberEt.setText(ePhoneNumber);

    }

    private void switchClick() {

        emergencyCallSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    phoneNumberEt.setEnabled(true);
                    sensibilitySp.setEnabled(true);
                    switchStatus = 1;
                    phoneNumberEt.setText(ePhoneNumber);
                    sensibilitySp.setSelection(indexOfSp);

                    Operations.IntSaveToSharedPreference(getApplicationContext(), "switchStatuss", switchStatus);
                    Operations.SaveToSharedPreference(getApplicationContext(), "sensibilitys", sensibility);

                    Toast toast = Toast.makeText(EmergencyCallActivity.this, " Emergency Call Started ", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    toast.getView().setBackgroundColor(Color.GREEN);
                    v.setTextColor(Color.BLACK);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);

                    toast.show();
                    callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOn));

                } else {
                    phoneNumberEt.setEnabled(false);
                    sensibilitySp.setEnabled(false);
                    switchStatus = 0;

                    Operations.IntSaveToSharedPreference(getApplicationContext(), "switchStatuss", switchStatus);
                    Operations.SaveToSharedPreference(getApplicationContext(), "sensibilitys", sensibility);
                    Toast toast = Toast.makeText(EmergencyCallActivity.this, " Emergency Call Stopped ", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    toast.getView().setBackgroundColor(Color.RED);
                    v.setTextColor(Color.WHITE);
                    view = toast.getView();
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                    callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));
                }

            }
        });

    }

    private void switchStatus() {

        if (switchStatus == 0) {
            emergencyCallSw.setChecked(false);
            phoneNumberEt.setEnabled(false);
            sensibilitySp.setEnabled(false);
            callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));

        } else if (switchStatus == 1) {
            emergencyCallSw.setChecked(true);
            phoneNumberEt.setEnabled(true);
            sensibilitySp.setEnabled(true);
            phoneNumberEt.setText(ePhoneNumber);
            sensibilitySp.setSelection(indexOfSp);
            callServiceLo.setBackgroundColor(ContextCompat.getColor(this, R.color.serviceOn));
        }
    }

    private void initialize() {
        phoneNumberEt = (EditText) findViewById(R.id.ePhoneNumberEts);
        emergencyCallSw = (Switch) findViewById(R.id.emergencyCallSws);
        sensibilitySp = (Spinner) findViewById(R.id.sensibilitySps);
        callServiceLo = (LinearLayout) findViewById(R.id.callServiceLo);
        phoneNumberEt.setInputType(0);
    }

    public void verifyUserPhone(View view) {
        callVerify();
    }


    public void callVerify() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmergencyCallActivity.this);
        alertDialog.setTitle("Emergency Number");
        alertDialog.setMessage("Set Emergency Number:");
        final EditText input = new EditText(EmergencyCallActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_action_warning);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ePhoneNumber = input.getText().toString();
                        sensibility = sensibilitySp.getSelectedItem().toString();
                        random = (int) (Math.random() * 1000 + 100);
                        String emergencyRandom = String.valueOf(random);
                        Operations.SaveToSharedPreference(getApplicationContext(), "emergencyRandom", emergencyRandom);

                        if (ePhoneNumber.length() > 0 && random > 0) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(ePhoneNumber, null, String.valueOf(random), null, null);
                            Operations.SaveToSharedPreference(getApplicationContext(), "ePhoneNumbers", ePhoneNumber);
                            showMessage("Please Wait...!","to Verify your PhoneNumber");
                            return;
                        } else {
                            Toast.makeText(EmergencyCallActivity.this, "Blank Phone Number!!!", Toast.LENGTH_SHORT).show();
                        }


                    }

                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
