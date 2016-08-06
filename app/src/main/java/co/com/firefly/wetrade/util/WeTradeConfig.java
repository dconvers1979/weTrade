package co.com.firefly.wetrade.util;

import android.graphics.Bitmap;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

/**
 * Created by toshiba on 25/07/2016.
 */
public class WeTradeConfig {
    private static WeTradeConfig ourInstance = new WeTradeConfig();
    private GoogleSignInAccount account;
    private GoogleApiClient mGoogleApiClient;
    private int spanCount = 2;

    public static WeTradeConfig getInstance() {
        return ourInstance;
    }

    private WeTradeConfig() {
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }
}
