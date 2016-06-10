package by.lykashenko.clientservice.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import by.lykashenko.clientservice.MainActivity;

public class PhoneStateBroadcastReciever extends BroadcastReceiver {
    public String phoneNumber;
    private String phone_state;
    private Integer login_state;
    private String LOG_TAG = "telephone";

    public PhoneStateBroadcastReciever() {
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

                phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    //телефон звонит, получаем входящий номер
                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.d("telephone", "calling state = 1");
                } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    //телефон находится в режиме звонка (набор номера / разговор)
                    Log.d("telephone", "calling state = 2");
                } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.d("telephone", "calling state = 3, tel. number: " + phoneNumber);

                    Intent intent1 = new Intent(context, MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("phoneNumber", phoneNumber);
                    intent1.putExtra("addclient", 1);
                    context.startActivity(intent1);
                    //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
                }

                if (phone_state.equals(TelephonyManager.CALL_STATE_RINGING)) {
                    Log.d(LOG_TAG, "zvonim");
                }

//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
