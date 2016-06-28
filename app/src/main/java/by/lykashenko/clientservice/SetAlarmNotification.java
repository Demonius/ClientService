package by.lykashenko.clientservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import by.lykashenko.clientservice.BD.AlarmNotification;
import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.Fragments.AddClientFragment;
import by.lykashenko.clientservice.Recievers.AlarmCardReciever;

/**
 * Created by Дмитрий on 12.06.16.
 */
public class SetAlarmNotification {

    private final static String LOG_TAG = "addAlarm";


    public void setAlarmNotification(Context context, String phone){
        //берём данные из базы для клиента
        List<Clients> list = new Select().from(Clients.class).where("phonenumber = ?",phone).execute();

        String client_id =list.get(0).getId().toString();
        String fio = list.get(0).client.toString();
        Long alarmSet = list.get(0).alarmset;

        //Создаём для него событие notification
        setAlarm(context, client_id,fio, phone,alarmSet);


        //заносим notification в свою базу
        ActiveAndroid.beginTransaction();
        AlarmNotification alarmNotification = new AlarmNotification(client_id, fio, phone, alarmSet);
        alarmNotification.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();

        Log.d(LOG_TAG, "Создано оповещение с id = " + client_id);
    }

    private void setAlarm(Context context, String client_id, String fio, String phone, Long alarmSet) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmCardReciever.class);
        //передаём в Broadcast переменные
        intent.putExtra(AddClientFragment.CLIENT_FIO, fio);
        intent.putExtra(AddClientFragment.CLIENT_PHONE, phone);
        intent.putExtra(AddClientFragment.CLIENT_ID, client_id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(client_id), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        //запускаем alarm
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmSet, pendingIntent);
        Log.d(LOG_TAG, "Напоминание для alarm_id = "+client_id+" создано");
    }

    public void setAlarmNotificationFromBd(Context context){
        List<AlarmNotification> list = new Select().from(AlarmNotification.class).execute();
        Integer i =0;
        while  (i<=(list.size()-1)){
            String client_id =list.get(i).idAlarm.toString();
            String fio = list.get(i).fio.toString();
            Long alarmSet = list.get(i).alarmSet;
            if ((System.currentTimeMillis() - alarmSet) > 600000){
                alarmSet=System.currentTimeMillis();
            }
            String phone = list.get(i).phone.toString();
            setAlarm(context,client_id,fio,phone,alarmSet);
            Log.d(LOG_TAG, "Напоминание для alarm_id = "+client_id+" пересоздано");
            i=i+1;
        }

    }
    public void deleteAlarmNotification(Context context,String client_id){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmCardReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(client_id), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        Log.d(LOG_TAG, "Напоминание с id = "+client_id+" отмененно");

        new Delete().from(AlarmNotification.class).where("id_alarm = ?", client_id).execute();
    }
}
