package net.wasnot.android.tetherenabler.helper;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by akihiroaida on 2014/10/08.
 */
public class ConnectivityManagerHelper {

    public static final int TETHER_ERROR_NO_ERROR = 0;
    public static final String ACTION_TETHER_STATE_CHANGED
            = "android.net.conn.TETHER_STATE_CHANGED";
    public static final String EXTRA_AVAILABLE_TETHER = "availableArray";
    public static final String EXTRA_ACTIVE_TETHER = "activeArray";
    public static final String EXTRA_ERRORED_TETHER = "erroredArray";

    private static final Class CLASS = ConnectivityManager.class;
    private ConnectivityManager mConnectivityManager;

    public ConnectivityManagerHelper(Context con) {
        mConnectivityManager = (ConnectivityManager) con
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String[] getTetherableIfaces() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetherableIfaces");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getTetheredIfaces() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetheredIfaces");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getTetheringErroredIfaces() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetheringErroredIfaces");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getTetherableUsbRegexs() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetherableUsbRegexs");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getTetherableWifiRegexs() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetherableWifiRegexs");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getTetherableBluetoothRegexs() {
        try {
            Method method = CLASS.getDeclaredMethod("getTetherableBluetoothRegexs");
            Object result = method.invoke(mConnectivityManager);
            return (String[]) result;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLastTetherError(String s) {
        try {
            Method method = CLASS.getDeclaredMethod("getLastTetherError", String.class);
            Object result = method.invoke(mConnectivityManager, s);
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

    public int setUsbTethering(boolean state) {
        try {
            Method method = CLASS.getDeclaredMethod("setUsbTethering", boolean.class);
            Object result = method.invoke(mConnectivityManager, state);
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

    public int untether(String iface) {
        try {
            Method method = CLASS.getDeclaredMethod("untether", String.class);
            Object result = method.invoke(mConnectivityManager, iface);
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
}
