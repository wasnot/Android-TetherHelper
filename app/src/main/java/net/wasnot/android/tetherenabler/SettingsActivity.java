package net.wasnot.android.tetherenabler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.wasnot.android.tetherenabler.utils.AppUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceClickListener {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        addPreferencesFromResource(R.xml.settings);
        Preference aboutApp = findPreference(getString(R.string.about_app_key));
        if (aboutApp != null) {
            aboutApp.setOnPreferenceClickListener(this);
        }
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
//            ApplicationErrorReport report = new ApplicationErrorReport();
//            report.packageName = report.processName = getApplication().getPackageName();
//            report.time = System.currentTimeMillis();
//            report.type = ApplicationErrorReport.TYPE_CRASH;
//            report.systemApp = false;
            Intent i = new Intent(Intent.ACTION_APP_ERROR);
//            i.putExtra(Intent.EXTRA_BUG_REPORT, report);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(PreferenceUtil.IS_FIRST, true)) {
            showDialog(0);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.about_app_key))) {
//            Intent i = new Intent(this, TestActivity.class);
//            startActivity(i);
            showDialog(0);
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.about_app_title);
            View v = getLayoutInflater().inflate(R.layout.dialog_about_app, null);
            ((TextView) v.findViewById(R.id.appVersionText)).setText(
                    getString(R.string.about_app_app_version) + AppUtil.getVersionName(this));
            b.setView(v);
            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Context con = getApplicationContext();
                    SharedPreferences.Editor edit = PreferenceManager
                            .getDefaultSharedPreferences(con).edit();
                    edit.putBoolean(PreferenceUtil.IS_FIRST, false);
                    edit.putString(PreferenceUtil.APP_VERSION_NAME, AppUtil.getVersionName(con));
                    edit.apply();
                }
            });
            return b.create();
        }
        return super.onCreateDialog(id);
    }
}