package net.wasnot.android.tetherenabler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.wasnot.android.tetherenabler.helper.BluetoothPanHelper;
import net.wasnot.android.tetherenabler.helper.BluetoothProfileHelper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @InjectView(R.id.textView)
    public TextView mTextView;
    @InjectView(R.id.adView)
    public AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(getApplicationContext(), mProfileServiceListener,
                    BluetoothProfileHelper.PAN);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateText();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        mMassStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());
//        mTetherChangeReceiver = new TetherChangeReceiver();
//        IntentFilter filter = new IntentFilter(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
//        Intent intent = registerReceiver(mTetherChangeReceiver, filter);
//
//        filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_STATE);
//        registerReceiver(mTetherChangeReceiver, filter);
//
//        filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_MEDIA_SHARED);
//        filter.addAction(Intent.ACTION_MEDIA_UNSHARED);
//        filter.addDataScheme("file");
//        registerReceiver(mTetherChangeReceiver, filter);
//
//        filter = new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mTetherChangeReceiver, filter);
//
//        if (intent != null) mTetherChangeReceiver.onReceive(activity, intent);
//        if (mWifiApEnabler != null) {
//            mEnableWifiAp.setOnPreferenceChangeListener(this);
//            mWifiApEnabler.resume();
//        }
//
//        updateState();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        unregisterReceiver(mTetherChangeReceiver);
//        mTetherChangeReceiver = null;
//        if (mWifiApEnabler != null) {
//            mEnableWifiAp.setOnPreferenceChangeListener(null);
//            mWifiApEnabler.pause();
//        }
//    }

    @OnClick(R.id.button)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (mBluetoothPanHelper != null) {
//                    mBluetoothPanHelper.setBluetoothTethering(!mBluetoothPanHelper.isTetheringOn());
                    MainService.enableBtTether(this);
                    updateText();
                }
                break;
        }
    }

//    private void updateState() {
//        ConnectivityManager cm =
//                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        String[] available = cm.getTetherableIfaces();
//        String[] tethered = cm.getTetheredIfaces();
//        String[] errored = cm.getTetheringErroredIfaces();
//        updateState(available, tethered, errored);
//    }


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

    private void updateText() {
        if (mTextView != null) {
            if (mBluetoothPanHelper != null) {
                mTextView.setText(
                        "BT tether is " + (mBluetoothPanHelper.isTetheringOn() ? "enable"
                                : "disable"));
            } else {
                mTextView.setText(
                        "BT tether is known");
            }
        }
    }

    private class TetherChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();
//            if (action.equals(ConnectivityManager.ACTION_TETHER_STATE_CHANGED)) {
//                // TODO - this should understand the interface types
//                ArrayList<String> available = intent.getStringArrayListExtra(
//                        ConnectivityManager.EXTRA_AVAILABLE_TETHER);
//                ArrayList<String> active = intent.getStringArrayListExtra(
//                        ConnectivityManager.EXTRA_ACTIVE_TETHER);
//                ArrayList<String> errored = intent.getStringArrayListExtra(
//                        ConnectivityManager.EXTRA_ERRORED_TETHER);
//                updateState(available.toArray(new String[available.size()]),
//                        active.toArray(new String[active.size()]),
//                        errored.toArray(new String[errored.size()]));
//            } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
//                mMassStorageActive = true;
//                updateState();
//            } else if (action.equals(Intent.ACTION_MEDIA_UNSHARED)) {
//                mMassStorageActive = false;
//                updateState();
//            } else if (action.equals(UsbManager.ACTION_USB_STATE)) {
//                mUsbConnected = intent.getBooleanExtra(UsbManager.USB_CONNECTED, false);
//                updateState();
//            } else
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                if (mBluetoothEnableForTether) {
                switch (intent
                        .getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    case BluetoothAdapter.STATE_ON:
                        mBluetoothPanHelper.setBluetoothTethering(true);
//                            mBluetoothEnableForTether = false;
                        break;

                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.ERROR:
//                            mBluetoothEnableForTether = false;
                        break;

                    default:
                        // ignore transition states
                }
//                }
//                updateState();
            }
        }
    }
}
