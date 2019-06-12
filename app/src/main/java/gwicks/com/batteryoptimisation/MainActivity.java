package gwicks.com.batteryoptimisation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Log.d(TAG, "onCreate: the power batter status is: " + checkPowerBattery());
        //installBatteryFix();

        //openPowerSettings(this);
        //logInstalledAccessiblityServices(this);
        //checkKeyboardSettings(this);


    }

    private void openPowerSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        //intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        context.startActivity(intent);
    }



    public void checkKeyboardSettings(Context mContext){
        if (isAccessibilityEnabled(mContext, "gwicks.com.batteryoptimisation/.KeyLoggerTwo")){
            Log.d(TAG, "checkKeyboardSettings: yes");
        }else{
            Log.d(TAG, "checkKeyboardSettings: no");
           // installKeyboard();
            launchKeyboardDialog();
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

    public static void logInstalledAccessiblityServices(Context context){
        Log.d(TAG, "logInstalledAccessiblityServices: start");

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo service : runningServices) {
            Log.i(TAG, service.getId());
        }
        Log.d(TAG, "logInstalledAccessiblityServices: end");
    }



    public void launchKeyboardDialog(){

        DialogFragment newFragment = new LaunchKeyboardDialog();
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "keyboard");
    }


    public Boolean checkPowerBattery(){
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean status = pm.isIgnoringBatteryOptimizations(packageName);

        return status;

    }

    public void installBatteryFix(){

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
                if (checkPowerBattery()) {
                    Log.d(TAG, "run: 3");
                    //You have the permission, re-launch MainActivity
                    //dismiss();

                    Log.d(TAG, "run: you have the permission, lauching next");

//                    Log.d(TAG, "run: have the permission, move on now");
//                    Intent i = new Intent(mContext, MainActivity.class);
//                    Log.d(TAG, "run: 4");
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    mContext.startActivity(i);
                    return;
                }
                Log.d(TAG, "run: 5");

                handler.postDelayed(this, 200);
            }
        };

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        //intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
       // context.startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        this.startActivity(intent)
        try {
            Log.d(TAG, "onClick: 5");
            startActivity(intent);
            //startActivity(startSettings);
            handler.postDelayed(checkOverlaySetting, 1000);
        } catch (ActivityNotFoundException notFoundEx) {
            //weird.. the device does not have the IME setting activity. Nook?
            // Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }

}
