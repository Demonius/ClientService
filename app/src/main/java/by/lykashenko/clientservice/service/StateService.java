package by.lykashenko.clientservice.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class StateService extends Service {
    private String LOG_TAG = "state_service";

//    private BroadcastReceiver callState;
    public StateService() {
    }

    public void onCreate() {
        super.onCreate();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
//        filter.addAction("android.intent.action.PHONE_STATE");
//        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        filter.addAction(android.telephony.TelephonyManager.EXTRA_STATE);
//        registerReceiver(callState, filter);

    }

    public void onDestroy() {
        super.onDestroy();
//       unregisterReceiver(callState);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

}
