package com.pgi.convergence.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.convergence.core.R;
import com.pgi.convergence.listeners.PermissionAskListener;
import com.pgi.convergence.persistance.SharedPreferencesManager;

import java.lang.ref.WeakReference;



/**
 * Utility for handling app permissions above Marshmallow in entire application
 * <p>
 * Created by surbhidhingra on 16-11-17.
 */

public class PermissionUtil {

    /**
     * Check if version is marshmallow and above.
     * Used in deciding to ask runtime permission
     *
     * @return os build version
     */
    public static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ContextCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public static void checkPermission(WeakReference<Activity> context, String permission, PermissionAskListener listener) {

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.
                getInstance();

        // If permission is not granted
        if (shouldAskPermission(context.get(), permission)) {

            // If permission denied previously
            if (ActivityCompat.shouldShowRequestPermissionRationale(context.get(), permission)) {
                listener.onPermissionPreviouslyDenied(permission);
            } else {

                // Permission denied or first time requested
                if (sharedPreferencesManager.isFirstTimeAskingPermission(permission)) {
                    sharedPreferencesManager.firstTimeAskingPermission(permission, false);
                    listener.onNeedPermission(permission);
                } else {

                    // Handle the feature without permission or ask user to manually allow permission
                    listener.onPermissionDisabled(permission);
                }
            }
        } else {
            listener.onPermissionGranted(permission);
        }
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void showRationalAlert(String message, final Context context, final String permission, final PermissionAskListener listener) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.alert_view, null);
        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_alert_title);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(tvTitle)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.rationale_alert_option_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPermissionAllowedFromRationale(permission);
                    }
                })
                .setNegativeButton(R.string.rationale_alert_option_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onPermissionDeniedFromRationale(permission);
                        dialog.dismiss();
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.cancel_btn_text_color));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.alert_title_color));
            }
        });
        alertDialog.show();
    }

    public static void showAppNativeSettings(WeakReference<Context> context) {

        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.get().getPackageName()));
        context.get().startActivity(intent);
    }
}
