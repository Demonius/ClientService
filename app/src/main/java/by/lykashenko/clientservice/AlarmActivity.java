package by.lykashenko.clientservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import by.lykashenko.clientservice.BD.Clients;

public class AlarmActivity extends AppCompatActivity {


    private static final String LOG_TAG = "alarmnotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String editClient;
        editClient = intent.getStringExtra("editClient");
        String editClientPhone = intent.getStringExtra("editClientNumber");
        Log.d(LOG_TAG, "id editClient = "+editClient+"number ="+editClientPhone);




        if (Integer.parseInt(editClient) != 0){
            Long currentTime = System.currentTimeMillis();
            SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
            setAlarmNotification.deleteAlarmNotification(this, editClient);
            ActiveAndroid.beginTransaction();
            Clients clients = Clients.load(Clients.class, Integer.parseInt(editClient));
            clients.alarmset = currentTime+600000;
            clients.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            setAlarmNotification.setAlarmNotification(this, editClientPhone);
            Toast.makeText(this,"Напоминание отложено на 10 мин",Toast.LENGTH_SHORT).show();

        }
        finish();

    }
}
