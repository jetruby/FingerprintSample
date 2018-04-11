package com.example.user.fingerprintsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements IAuthenticateListener {

    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    private EditText mEditText;
    private SharedPreferences mPreferences;
    private FingerprintHandler mFingerprintHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditText = findViewById(R.id.editText);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.btn_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreferences.contains(KEY_PASSWORD)) initSensor();
    }

    private void signUp() {
        String password = mEditText.getText().toString();
        if (password.length() > 0) {
            savePassword(password);
            mEditText.setText("");
            Toast.makeText(this, "Register Password " + password, Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
    }

    private void login() {
        String password = mEditText.getText().toString();
        if (password.length() > 0) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePassword(String password) {
        if (Utils.checkSensorState(this)) {
            String encoded = Utils.encryptString(password);
            mPreferences.edit().putString(KEY_PASSWORD, encoded).apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFingerprintHandler != null) {
            mFingerprintHandler.cancel();
        }
    }

    private void initSensor() {
        if (Utils.checkSensorState(this)) {
            FingerprintManager.CryptoObject cryptoObject = Utils.getCryptoObject();
            if (cryptoObject != null) {
                Toast.makeText(this, "Use fingerprint to login", Toast.LENGTH_LONG).show();
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                mFingerprintHandler = new FingerprintHandler(this, mPreferences, this);
                mFingerprintHandler.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }

    @Override
    public void onAuthenticate(String decryptPassword) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        mEditText.setText(decryptPassword);
        startActivity(new Intent(this, MainActivity.class));
    }
}
