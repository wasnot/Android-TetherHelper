package net.wasnot.android.tetherenabler.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by akihiroaida on 2014/10/02.
 */
public class TetherUtil {

    private String[] mUsbRegexs;

    private String[] mWifiRegexs;

    private String[] mBluetoothRegexs;

    public boolean isTetherAbailabe(Context con) {

        ConnectivityManager cm =
                (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

//        mUsbRegexs = cm.getTetherableUsbRegexs();
//        mWifiRegexs = cm.getTetherableWifiRegexs();
//        mBluetoothRegexs = cm.getTetherableBluetoothRegexs();
//
//        final boolean usbAvailable = mUsbRegexs.length != 0;
//        final boolean wifiAvailable = mWifiRegexs.length != 0;
//        final boolean bluetoothAvailable = mBluetoothRegexs.length != 0;
//        return usbAvailable;
        return false;
    }
}
