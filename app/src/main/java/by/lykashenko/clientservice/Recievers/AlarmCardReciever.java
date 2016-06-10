package by.lykashenko.clientservice.Recievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import by.lykashenko.clientservice.MainActivity;
import by.lykashenko.clientservice.R;
import by.lykashenko.clientservice.fragments.AddClientFragment;

public class AlarmCardReciever extends BroadcastReceiver {

    private String client, phonenumber, id;

    public AlarmCardReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        client = intent.getStringExtra(AddClientFragment.CLIENT_FIO);
        Log.d("alarm", "alarmset_fio"+client);
        phonenumber = intent.getStringExtra(AddClientFragment.CLIENT_PHONE);
        id = intent.getStringExtra(AddClientFragment.CLIENT_ID);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phonenumber));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id),notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher).setTicker("Перезвонить "+client).setWhen(System.currentTimeMillis())
                .setContentTitle("Перезвонить "+client).setContentText(phonenumber);
        Notification notification = builder.getNotification();
        notificationManager.notify(Integer.parseInt(id), notification);


//        throw new UnsupportedOperationException("Not yet implemented");
    }
}