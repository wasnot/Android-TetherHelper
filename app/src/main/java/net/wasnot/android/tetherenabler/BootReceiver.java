package net.wasnot.android.tetherenabler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent);
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "onReceive boot complete");
            Context con = context.getApplicationContext();
            if (PreferenceManager.getDefaultSharedPreferences(con)
                    .getBoolean(PreferenceUtil.ENABLE_AUTO_BLUETOOTH_TETHERING, false)) {
                MainService.enableBtTether(con);
            }
        }
    }
}
