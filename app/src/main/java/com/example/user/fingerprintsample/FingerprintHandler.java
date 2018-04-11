package com.example.user.fingerprintsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

import javax.crypto.Cipher;

class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    private CancellationSignal mCancellationSignal;
    private SharedPreferences mSharedPreferences;
    private IAuthenticateListener mListener;

    FingerprintHandler(Context context, SharedPreferences sharedPreferences, IAuthenticateListener listener) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
        mListener = listener;
        mCancellationSignal = new CancellationSignal();
    }

    void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        Cipher cipher = result.getCryptoObject().getCipher();
        String encoded = mSharedPreferences.getString(LoginActivity.KEY_PASSWORD, null);
        String decoded = Utils.decryptString(encoded, cipher);
        mListener.onAuthenticate(decoded);
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, "onAuthenticationFailed", Toast.LENGTH_SHORT).show();
    }

    void cancel() {
        if (mCancellationSignal != null) mCancellationSignal.cancel();
    }
}
