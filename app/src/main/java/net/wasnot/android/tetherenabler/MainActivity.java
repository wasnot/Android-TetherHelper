package net.wasnot.android.tetherenabler;

import net.wasnot.android.tetherenabler.helper.BluetoothPanHelper;
import net.wasnot.android.tetherenabler.helper.BluetoothProfileHelper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

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

    @OnClick(R.id.button)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (mBluetoothPanHelper != null) {
                    Toast.makeText(this, "bt:" + mBluetoothPanHelper.isTetheringOn(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private BluetoothPanHelper mBluetoothPanHelper;

    private BluetoothProfile.ServiceListener mProfileServiceListener =
            new BluetoothProfile.ServiceListener() {
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    mBluetoothPanHelper = new BluetoothPanHelper(proxy);
                }

                public void onServiceDisconnected(int profile) {
                    mBluetoothPanHelper = null;
                }
            };
}
