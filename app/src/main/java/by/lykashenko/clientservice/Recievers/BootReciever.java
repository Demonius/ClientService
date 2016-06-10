package by.lykashenko.clientservice.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import by.lykashenko.clientservice.MainActivity;

public class BootReciever extends BroadcastReceiver {
    public BootReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            Intent activivtyIntent = new Intent(context, MainActivity.class);
            activivtyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(context,"Программа запущена", Toast.LENGTH_SHORT).show();
            context.startActivity(activivtyIntent);
        }
    }
}
