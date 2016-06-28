package by.lykashenko.clientservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import by.lykashenko.clientservice.BD.Clients;

public class AlarmCloseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "alarmclose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
                String closeClient;
        closeClient = intent.getStringExtra("closeClient");
        Log.d(LOG_TAG, "id editClient = "+closeClient);


        if (Integer.parseInt(closeClient) != 0){
            ActiveAndroid.beginTransaction();
            Clients clients = Clients.load(Clients.class, Integer.parseInt(closeClient));
            clients.alarmset = Long.valueOf(0);
            clients.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
            setAlarmNotification.deleteAlarmNotification(this, closeClient);

        }
        finish();
    }
}
