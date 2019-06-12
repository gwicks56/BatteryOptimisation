package gwicks.com.batteryoptimisation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class LaunchKeyboardDialog extends DialogFragment {

    private static final String TAG = "LaunchKeyboardDialog";

    Context mContext;

    //public MainActivity mactivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = getActivity();

        //mactivity = activity;


    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setMessage("Sometime Android can switch off the Keyboard Permission you gave us during the EARS install. Please reallow the permission");
        builder.setTitle("Keyboard Permission has been switched off.")

                .setPositiveButton("Return Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        installKeyboard();
                    }
                })

        // Not sure we want a cancel button, so going to comment this out 10th April 2018
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        //Toast.makeText(getActivity(), "HelloHello", Toast.LENGTH_LONG).show();
//                        //mListener.onDialogNegativeClick(EmailSecureDeviceID.this);
//                        dismiss();
//                    }
//                });

        ;
        return builder.create();

    }

    public void installKeyboard(){

        final Handler handler = new Handler();

        Runnable checkOverlaySetting = new Runnable() {

            @Override
            //@TargetApi(23)
            public void run() {
                Log.d(TAG, "run: 1");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d(TAG, "run: 2");
                    //return;
                }

                // 18th Jan 2018, below works, trying to stop using the intent ( ie try back button below).
                if (isAccessibilityEnabled(mContext, "gwicks.com.batteryoptimisation/.KeyLoggerTwo")) {
                    Log.d(TAG, "run: 3");
                    //You have the permission, re-launch MainActivity
                    dismiss();

                    Log.d(TAG, "run: have the permission, move on now");
                    Intent i = new Intent(mContext, MainActivity.class);
                    Log.d(TAG, "run: 4");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(i);
                    return;
                }
                Log.d(TAG, "run: 5");

                handler.postDelayed(this, 200);
            }
        };

        Intent startSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            Log.d(TAG, "onClick: 5");
            startActivity(startSettings);
            handler.postDelayed(checkOverlaySetting, 1000);
        } catch (ActivityNotFoundException notFoundEx) {
            //weird.. the device does not have the IME setting activity. Nook?
           // Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }
}
