package net.wasnot.android.tetherenabler.helper;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by akihiroaida on 2014/10/08.
 */
public class WifiManagerHelper {

    public static final String WIFI_AP_STATE_CHANGED_ACTION
            = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";

    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;

    private static final Class CLASS = WifiManager.class;
    private WifiManager mWifiManager;

    public WifiManagerHelper(Context con) {
        mWifiManager = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
    }

    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = CLASS.getDeclaredMethod("getWifiApConfiguration");
            Object result = method.invoke(mWifiManager);
            return (WifiConfiguration) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = CLASS
                    .getDeclaredMethod("setWifiApConfiguration", WifiConfiguration.class);
            Object result = method.invoke(mWifiManager, wifiConfig);
            return;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return;
    }

    public int getWifiApState() {
        try {
            Method method = CLASS.getDeclaredMethod("getWifiApState");
            Object result = method.invoke(mWifiManager);
            return (Integer) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enable) {
        try {
            Method method = CLASS
                    .getDeclaredMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            Object result = method.invoke(mWifiManager, wifiConfig, enable);
            return (Boolean) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    public void setWifiEnabled(boolean enabled) {
        mWifiManager.setWifiEnabled(enabled);
    }
}
