package net.wasnot.android.tetherenabler;

import net.wasnot.android.tetherenabler.helper.BluetoothPanHelper;
import net.wasnot.android.tetherenabler.helper.BluetoothProfileHelper;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

    private static final String TAG = MainService.class.getSimpleName();
    public static final String ACTION_BT_TETHER = "net.wasnot.android.tether.ACTION_BT_TETHER";

    private boolean mSetEnable = false;

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
        Log.d(TAG, "onStartCommand " + intent);
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (ACTION_BT_TETHER.equals(intent.getAction())) {
            if (mBluetoothPanHelper != null) {
                mBluetoothPanHelper.setBluetoothTethering(true);
            } else {
                mSetEnable = true;
            }
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
//                    updateText();
                    if (mSetEnable) {
                        mBluetoothPanHelper.setBluetoothTethering(true);
                    }
                }

                public void onServiceDisconnected(int profile) {
                    mBluetoothPanHelper = null;
                }
            };

    public static void enableBtTether(Context con) {
        Intent i = new Intent(con, MainService.class);
        i.setAction(MainService.ACTION_BT_TETHER);
        con.startService(i);
    }
}
