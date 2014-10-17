package net.wasnot.android.tetherenabler.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by akihiroaida on 2014/10/17.
 */
public class AppUtil {

    /**
     * バージョン名を取得する
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        String versionName = null;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
