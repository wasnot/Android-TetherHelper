package net.wasnot.android.tetherenabler.helper;

import android.bluetooth.BluetoothProfile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by akihiroaida on 2014/10/02.
 */
public class BluetoothPanHelper {

    private final static String CLASS_NAME = "android.bluetooth.BluetoothPan";

    private Object mBluetoothPan;

    public BluetoothPanHelper(BluetoothProfile profile) {
        mBluetoothPan = profile;
    }

    public boolean isTetheringOn() {
        Class clazz = null;
        try {
            clazz = Class.forName(CLASS_NAME);
            Method method = clazz.getDeclaredMethod("isTetheringOn");
            Object result = method.invoke(mBluetoothPan);
            return (Boolean) result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setBluetoothTethering(boolean enable) {
        Class clazz = null;
        try {
            clazz = Class.forName(CLASS_NAME);
            Method method = clazz.getDeclaredMethod("setBluetoothTethering", boolean.class);
            Object result = method.invoke(mBluetoothPan, enable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
