package net.wasnot.android.tetherenabler;

import net.wasnot.android.tetherenabler.helper.BluetoothPanHelper;
import net.wasnot.android.tetherenabler.helper.BluetoothProfileHelper;
import net.wasnot.android.tetherenabler.helper.ConnectivityManagerHelper;
import net.wasnot.android.tetherenabler.helper.IntentHelper;
import net.wasnot.android.tetherenabler.helper.UsbManagerHelper;
import net.wasnot.android.tetherenabler.helper.WifiApDialog;
import net.wasnot.android.tetherenabler.helper.WifiApEnabler;
import net.wasnot.android.tetherenabler.helper.WifiManagerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by akihiroaida on 2014/10/08.
 */
public class TestActivity extends PreferenceActivity
        implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener {

    private static final String USB_TETHER_SETTINGS = "usb_tether_settings";
    private static final String ENABLE_WIFI_AP = "enable_wifi_ap";
    private static final String ENABLE_BLUETOOTH_TETHERING = "enable_bluetooth_tethering";
    private static final String TETHERING_HELP = "tethering_help";
    private static final String USB_HELP_MODIFIER = "usb_";
    private static final String WIFI_HELP_MODIFIER = "wifi_";
    private static final String HELP_URL = "file:///android_asset/html/%y%z/tethering_%xhelp.html";
    private static final String HELP_PATH = "html/%y%z/tethering_help.html";

    private static final int DIALOG_TETHER_HELP = 1;
    private static final int DIALOG_AP_SETTINGS = 2;

    private WebView mView;
    private CheckBoxPreference mUsbTether;

    private WifiApEnabler mWifiApEnabler;
    private CheckBoxPreference mEnableWifiAp;
    private static final int MHS_REQUEST = 0;

    private CheckBoxPreference mBluetoothTether;

    private PreferenceScreen mTetherHelp;

    private BroadcastReceiver mTetherChangeReceiver;

    private String[] mUsbRegexs;

    private String[] mWifiRegexs;

    private String[] mBluetoothRegexs;
    private BluetoothPanHelper mBluetoothPan;

    private static final String WIFI_AP_SSID_AND_SECURITY = "wifi_ap_ssid_and_security";
    private static final int CONFIG_SUBTEXT = R.string.wifi_tether_configure_subtext;

    private String[] mSecurityType;
    private Preference mCreateNetwork;

    private WifiApDialog mDialog;
    private WifiManagerHelper mWifiManager;
    private WifiConfiguration mWifiConfig = null;

    private boolean mUsbConnected;
    private boolean mMassStorageActive;

    private boolean mBluetoothEnableForTether;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.tether_prefs);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(this.getApplicationContext(), mProfileServiceListener,
                    BluetoothProfileHelper.PAN);
        }

        mEnableWifiAp =
                (CheckBoxPreference) findPreference(ENABLE_WIFI_AP);
        Preference wifiApSettings = findPreference(WIFI_AP_SSID_AND_SECURITY);
        mUsbTether = (CheckBoxPreference) findPreference(USB_TETHER_SETTINGS);
        mBluetoothTether = (CheckBoxPreference) findPreference(ENABLE_BLUETOOTH_TETHERING);
        mTetherHelp = (PreferenceScreen) findPreference(TETHERING_HELP);

        ConnectivityManagerHelper cm =new ConnectivityManagerHelper(this);

        mUsbRegexs = cm.getTetherableUsbRegexs();
        mWifiRegexs = cm.getTetherableWifiRegexs();
        mBluetoothRegexs = cm.getTetherableBluetoothRegexs();

        final boolean usbAvailable = mUsbRegexs.length != 0;
        final boolean wifiAvailable = mWifiRegexs.length != 0;
        final boolean bluetoothAvailable = mBluetoothRegexs.length != 0;

        if (!usbAvailable
//                || Utils.isMonkeyRunning()
                ) {
            getPreferenceScreen().removePreference(mUsbTether);
        }

        if (wifiAvailable) {
            mWifiApEnabler = new WifiApEnabler(this, mEnableWifiAp);
            initWifiTethering();
        } else {
            getPreferenceScreen().removePreference(mEnableWifiAp);
            getPreferenceScreen().removePreference(wifiApSettings);
        }

        if (!bluetoothAvailable) {
            getPreferenceScreen().removePreference(mBluetoothTether);
        } else {
            if (mBluetoothPan != null && mBluetoothPan.isTetheringOn()) {
                mBluetoothTether.setChecked(true);
            } else {
                mBluetoothTether.setChecked(false);
            }
        }

        mView = new WebView(this);
    }

    private void initWifiTethering() {
        mWifiManager = new WifiManagerHelper(this);
        mWifiConfig = mWifiManager.getWifiApConfiguration();
        mSecurityType = getResources().getStringArray(R.array.wifi_ap_security);

        mCreateNetwork = findPreference(WIFI_AP_SSID_AND_SECURITY);

        if (mWifiConfig == null) {
            final String s = this.getString(
                    R.string.wifi_tether_configure_ssid_default);
            mCreateNetwork.setSummary(String.format(this.getString(CONFIG_SUBTEXT),
                    s, mSecurityType[WifiApDialog.OPEN_INDEX]));
        } else {
            int index = WifiApDialog.getSecurityTypeIndex(mWifiConfig);
            mCreateNetwork.setSummary(String.format(this.getString(CONFIG_SUBTEXT),
                    mWifiConfig.SSID,
                    mSecurityType[index]));
        }
    }

    private BluetoothProfile.ServiceListener mProfileServiceListener =
            new BluetoothProfile.ServiceListener() {
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    mBluetoothPan = new BluetoothPanHelper(proxy);
                }
                public void onServiceDisconnected(int profile) {
                    mBluetoothPan = null;
                }
            };

    @Override
    public Dialog onCreateDialog(int id) {
        if (id == DIALOG_TETHER_HELP) {
            Locale locale = Locale.getDefault();

            // check for the full language + country resource, if not there, try just language
            final AssetManager am = this.getAssets();
            String path = HELP_PATH.replace("%y", locale.getLanguage().toLowerCase());
            path = path.replace("%z", '_'+locale.getCountry().toLowerCase());
            boolean useCountry = true;
            InputStream is = null;
            try {
                is = am.open(path);
            } catch (Exception ignored) {
                useCountry = false;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ignored) {}
                }
            }
            String url = HELP_URL.replace("%y", locale.getLanguage().toLowerCase());
            url = url.replace("%z", useCountry ? '_'+locale.getCountry().toLowerCase() : "");
            if ((mUsbRegexs.length != 0) && (mWifiRegexs.length == 0)) {
                url = url.replace("%x", USB_HELP_MODIFIER);
            } else if ((mWifiRegexs.length != 0) && (mUsbRegexs.length == 0)) {
                url = url.replace("%x", WIFI_HELP_MODIFIER);
            } else {
                // could assert that both wifi and usb have regexs, but the default
                // is to use this anyway so no check is needed
                url = url.replace("%x", "");
            }

            mView.loadUrl(url);
            // Detach from old parent first
            ViewParent parent = mView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mView);
            }
            return new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.tethering_help_button_text)
                    .setView(mView)
                    .create();
        } else if (id == DIALOG_AP_SETTINGS) {
            mDialog = new WifiApDialog(this, this, mWifiConfig);
            return mDialog;
        }

        return null;
    }

    private class TetherChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManagerHelper.ACTION_TETHER_STATE_CHANGED)) {
                // TODO - this should understand the interface types
                ArrayList<String> available = intent.getStringArrayListExtra(
                        ConnectivityManagerHelper.EXTRA_AVAILABLE_TETHER);
                ArrayList<String> active = intent.getStringArrayListExtra(
                        ConnectivityManagerHelper.EXTRA_ACTIVE_TETHER);
                ArrayList<String> errored = intent.getStringArrayListExtra(
                        ConnectivityManagerHelper.EXTRA_ERRORED_TETHER);
                updateState(available.toArray(new String[available.size()]),
                        active.toArray(new String[active.size()]),
                        errored.toArray(new String[errored.size()]));
            } else if (action.equals(Intent.ACTION_MEDIA_SHARED)) {
                mMassStorageActive = true;
                updateState();
            } else if (action.equals(IntentHelper.ACTION_MEDIA_UNSHARED)) {
                mMassStorageActive = false;
                updateState();
            } else if (action.equals(UsbManagerHelper.ACTION_USB_STATE)) {
                mUsbConnected = intent.getBooleanExtra(UsbManagerHelper.USB_CONNECTED, false);
                updateState();
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (mBluetoothEnableForTether) {
                    switch (intent
                            .getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                        case BluetoothAdapter.STATE_ON:
                            mBluetoothPan.setBluetoothTethering(true);
                            mBluetoothEnableForTether = false;
                            break;

                        case BluetoothAdapter.STATE_OFF:
                        case BluetoothAdapter.ERROR:
                            mBluetoothEnableForTether = false;
                            break;

                        default:
                            // ignore transition states
                    }
                }
                updateState();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mMassStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());
        mTetherChangeReceiver = new TetherChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManagerHelper.ACTION_TETHER_STATE_CHANGED);
        Intent intent = registerReceiver(mTetherChangeReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(UsbManagerHelper.ACTION_USB_STATE);
        registerReceiver(mTetherChangeReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SHARED);
        filter.addAction(IntentHelper.ACTION_MEDIA_UNSHARED);
        filter.addDataScheme("file");
        registerReceiver(mTetherChangeReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mTetherChangeReceiver, filter);

        if (intent != null) mTetherChangeReceiver.onReceive(this, intent);
        if (mWifiApEnabler != null) {
            mEnableWifiAp.setOnPreferenceChangeListener(this);
            mWifiApEnabler.resume();
        }

        updateState();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mTetherChangeReceiver);
        mTetherChangeReceiver = null;
        if (mWifiApEnabler != null) {
            mEnableWifiAp.setOnPreferenceChangeListener(null);
            mWifiApEnabler.pause();
        }
    }

    private void updateState() {
        ConnectivityManagerHelper cm =new ConnectivityManagerHelper(this);

        String[] available = cm.getTetherableIfaces();
        String[] tethered = cm.getTetheredIfaces();
        String[] errored = cm.getTetheringErroredIfaces();
        updateState(available, tethered, errored);
    }

    private void updateState(String[] available, String[] tethered,
            String[] errored) {
        updateUsbState(available, tethered, errored);
        if(mBluetoothPan!=null)
        updateBluetoothState(available, tethered, errored);
    }


    private void updateUsbState(String[] available, String[] tethered,
            String[] errored) {
        ConnectivityManagerHelper cm =new ConnectivityManagerHelper(this);
        boolean usbAvailable = mUsbConnected && !mMassStorageActive;
        int usbError = ConnectivityManagerHelper.TETHER_ERROR_NO_ERROR;
        for (String s : available) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    if (usbError == ConnectivityManagerHelper.TETHER_ERROR_NO_ERROR) {
                        usbError = cm.getLastTetherError(s);
                    }
                }
            }
        }
        boolean usbTethered = false;
        for (String s : tethered) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbTethered = true;
            }
        }
        boolean usbErrored = false;
        for (String s: errored) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbErrored = true;
            }
        }

        if (usbTethered) {
            mUsbTether.setSummary(R.string.usb_tethering_active_subtext);
            mUsbTether.setEnabled(true);
            mUsbTether.setChecked(true);
        } else if (usbAvailable) {
            if (usbError == ConnectivityManagerHelper.TETHER_ERROR_NO_ERROR) {
                mUsbTether.setSummary(R.string.usb_tethering_available_subtext);
            } else {
                mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
            }
            mUsbTether.setEnabled(true);
            mUsbTether.setChecked(false);
        } else if (usbErrored) {
            mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        } else if (mMassStorageActive) {
            mUsbTether.setSummary(R.string.usb_tethering_storage_active_subtext);
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        } else {
            mUsbTether.setSummary(R.string.usb_tethering_unavailable_subtext);
            mUsbTether.setEnabled(false);
            mUsbTether.setChecked(false);
        }
    }

    private void updateBluetoothState(String[] available, String[] tethered,
            String[] errored) {
        int bluetoothTethered = 0;
        for (String s : tethered) {
            for (String regex : mBluetoothRegexs) {
                if (s.matches(regex)) bluetoothTethered++;
            }
        }
        boolean bluetoothErrored = false;
        for (String s: errored) {
            for (String regex : mBluetoothRegexs) {
                if (s.matches(regex)) bluetoothErrored = true;
            }
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        int btState = adapter.getState();
        if (btState == BluetoothAdapter.STATE_TURNING_OFF) {
            mBluetoothTether.setEnabled(false);
            mBluetoothTether.setSummary(R.string.wifi_stopping);
        } else if (btState == BluetoothAdapter.STATE_TURNING_ON) {
            mBluetoothTether.setEnabled(false);
            mBluetoothTether.setSummary(R.string.bluetooth_turning_on);
        } else if (btState == BluetoothAdapter.STATE_ON && mBluetoothPan.isTetheringOn()) {
            mBluetoothTether.setChecked(true);
            mBluetoothTether.setEnabled(true);
            if (bluetoothTethered > 1) {
                String summary = getString(
                        R.string.bluetooth_tethering_devices_connected_subtext, bluetoothTethered);
                mBluetoothTether.setSummary(summary);
            } else if (bluetoothTethered == 1) {
                mBluetoothTether.setSummary(R.string.bluetooth_tethering_device_connected_subtext);
            } else if (bluetoothErrored) {
                mBluetoothTether.setSummary(R.string.bluetooth_tethering_errored_subtext);
            } else {
                mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
            }
        } else {
            mBluetoothTether.setEnabled(true);
            mBluetoothTether.setChecked(false);
            mBluetoothTether.setSummary(R.string.bluetooth_tethering_off_subtext);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object value) {
        boolean enable = (Boolean) value;

        if (enable) {
            //Check if provisioning is needed
            String[] appDetails = getResources().getStringArray(
                    R.array.config_mobile_hotspot_provision_app);

            if (appDetails.length != 2) {
                mWifiApEnabler.setSoftapEnabled(true);
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName(appDetails[0], appDetails[1]);
                startActivityForResult(intent, MHS_REQUEST);
            }
        } else {
            mWifiApEnabler.setSoftapEnabled(false);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == MHS_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mWifiApEnabler.setSoftapEnabled(true);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
        ConnectivityManagerHelper cm =new ConnectivityManagerHelper(this);

        if (preference == mUsbTether) {
            boolean newState = mUsbTether.isChecked();

            if (cm.setUsbTethering(newState) != ConnectivityManagerHelper.TETHER_ERROR_NO_ERROR) {
                mUsbTether.setChecked(false);
                mUsbTether.setSummary(R.string.usb_tethering_errored_subtext);
                return true;
            }
            mUsbTether.setSummary("");
        } else if (preference == mBluetoothTether) {
            boolean bluetoothTetherState = mBluetoothTether.isChecked();

            if (bluetoothTetherState) {
                // turn on Bluetooth first
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (adapter.getState() == BluetoothAdapter.STATE_OFF) {
                    mBluetoothEnableForTether = true;
                    adapter.enable();
                    mBluetoothTether.setSummary(R.string.bluetooth_turning_on);
                    mBluetoothTether.setEnabled(false);
                } else {
                    mBluetoothPan.setBluetoothTethering(true);
                    mBluetoothTether.setSummary(R.string.bluetooth_tethering_available_subtext);
                }
            } else {
                boolean errored = false;

                String [] tethered = cm.getTetheredIfaces();
                String bluetoothIface = findIface(tethered, mBluetoothRegexs);
                if (bluetoothIface != null &&
                        cm.untether(bluetoothIface) != ConnectivityManagerHelper.TETHER_ERROR_NO_ERROR) {
                    errored = true;
                }

                mBluetoothPan.setBluetoothTethering(false);
                if (errored) {
                    mBluetoothTether.setSummary(R.string.bluetooth_tethering_errored_subtext);
                } else {
                    mBluetoothTether.setSummary(R.string.bluetooth_tethering_off_subtext);
                }
            }
        } else if (preference == mTetherHelp) {
            showDialog(DIALOG_TETHER_HELP);
            return true;
        } else if (preference == mCreateNetwork) {
            showDialog(DIALOG_AP_SETTINGS);
        }

        return super.onPreferenceTreeClick(screen, preference);
    }

    private static String findIface(String[] ifaces, String[] regexes) {
        for (String iface : ifaces) {
            for (String regex : regexes) {
                if (iface.matches(regex)) {
                    return iface;
                }
            }
        }
        return null;
    }

    public void onClick(DialogInterface dialogInterface, int button) {
        if (button == DialogInterface.BUTTON_POSITIVE) {
            mWifiConfig = mDialog.getConfig();
            if (mWifiConfig != null) {
                /**
                 * if soft AP is stopped, bring up
                 * else restart with new config
                 * TODO: update config on a running access point when framework support is added
                 */
                if (mWifiManager.getWifiApState() == WifiManagerHelper.WIFI_AP_STATE_ENABLED) {
                    mWifiManager.setWifiApEnabled(null, false);
                    mWifiManager.setWifiApEnabled(mWifiConfig, true);
                } else {
                    mWifiManager.setWifiApConfiguration(mWifiConfig);
                }
                int index = WifiApDialog.getSecurityTypeIndex(mWifiConfig);
                mCreateNetwork.setSummary(String.format(this.getString(CONFIG_SUBTEXT),
                        mWifiConfig.SSID,
                        mSecurityType[index]));
            }
        }
    }

}
