package net.wasnot.android.tetherenabler;

import net.wasnot.android.tetherenabler.helper.BluetoothPanHelper;
import net.wasnot.android.tetherenabler.helper.BluetoothProfileHelper;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {
    private static final String ACTION_BT_TETHER="net.wasnot.android.tether.ACTION_BT_TETHER";

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(getApplicationContext(), mProfileServiceListener,
                    BluetoothProfileHelper.PAN);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null)
        return super.onStartCommand(intent, flags, startId);
        if(ACTION_BT_TETHER.equals(intent.getAction())){

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BluetoothPanHelper mBluetoothPanHelper;

    private BluetoothProfile.ServiceListener mProfileServiceListener =
            new BluetoothProfile.ServiceListener() {
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    mBluetoothPanHelper = new BluetoothPanHelper(proxy);
                    updateText();
                }

                public void onServiceDisconnected(int profile) {
                    mBluetoothPanHelper = null;
                }
            };
}
