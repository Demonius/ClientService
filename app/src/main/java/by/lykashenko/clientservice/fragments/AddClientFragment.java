package by.lykashenko.clientservice.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.R;
import by.lykashenko.clientservice.Recievers.AlarmCardReciever;

public class AddClientFragment extends Fragment {

    public static final String CLIENT_FIO = "client_fio";
    public static final String CLIENT_PHONE = "client_phone";
    public static final String CLIENT_ID = "client_id";
    private String phoneNumber;
    private String LOG_TAG = "addClient";
    private TextView number, fio, note, date_time;
    private String[] alarmSet = {"1 мин", "5 мин","10 мин","30 мин","1 час","никогда"};
    private Spinner spinnerAlarmSet;
    private Toolbar toolbar;
    private FloatingActionButton fadAddClient;
    private SharedPreferences sharedPreferences;
    private Integer positionSpinner;
    private Long alarmTime;

    public interface DialogAddClientListener {
        void onDialogAddClient(Integer state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_add_client, container, false);

        //заголовок окна
        toolbar = (Toolbar) view.findViewById(R.id.toolbaradd);
        toolbar.setTitle(getResources().getString(R.string.add_client));
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                finish();
            }
        });

        //записи
        number = (TextView) view.findViewById(R.id.input_phone);
        fio = (TextView) view.findViewById(R.id.input_name);
        note = (TextView) view.findViewById(R.id.input_note);
        date_time = (TextView) view.findViewById(R.id.input_time_date);

        phoneNumber = getArguments().getString("phonenumber");
        Log.d(LOG_TAG,"number = "+phoneNumber);
        number.setText(phoneNumber);

        //выпадающий список напоминаний
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, alarmSet);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlarmSet = (Spinner) view.findViewById(R.id.spinner_alarm);
        spinnerAlarmSet.setAdapter(adapter);
        spinnerAlarmSet.setPrompt(getResources().getString(R.string.alarm_set));
        spinnerAlarmSet.setSelection(4);
        spinnerAlarmSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionSpinner = position;
                Toast.makeText(getActivity(), "Повторить через "+alarmSet[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //получение даты и времени звонка

        Calendar calendar_date_time = Calendar.getInstance();
        final long current_time = calendar_date_time.getTimeInMillis();
        Date date = new Date(current_time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");
        final String date_time_output = dateFormat.format(date);

        date_time.setEnabled(false);
        date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        date_time.setText(date_time_output);

        //кнопка добавления в базу клиентов
        fadAddClient = (FloatingActionButton) view.findViewById(R.id.buttonFloatAddOk);

        fadAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveAndroid.beginTransaction();
                sharedPreferences = getActivity().getSharedPreferences(LoginFragment.PREFERENCES_NAME, Context.MODE_PRIVATE);
                String user_id = sharedPreferences.getString(LoginFragment.ID_USER_TAG, "");
                if (positionSpinner < 5) {
                    alarmTime = current_time - current_time;
                    switch (positionSpinner) {
                        case 0:
                            alarmTime = current_time + 1 * 60 * 1000;
                            break;
                        case 1:
                            alarmTime = current_time + 5 * 60 * 1000;
                            break;
                        case 2:
                            alarmTime = current_time + 10 * 60 * 1000;
                            break;
                        case 3:
                            alarmTime = current_time + 30 * 60 * 1000;
                            break;
                        case 4:
                            alarmTime = current_time + 60 * 60 * 1000;
                            break;
                    }


                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity(), AlarmCardReciever.class);
                    intent.putExtra(CLIENT_FIO, fio.getText().toString());
                    Log.d(LOG_TAG,"alarm_fio"+fio.getText().toString());
                    intent.putExtra(CLIENT_PHONE, phoneNumber);
                    intent.putExtra(CLIENT_ID, user_id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), Integer.parseInt(user_id), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.cancel(pendingIntent);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

                }


                Clients clients = new Clients(user_id,fio.getText().toString(),phoneNumber, current_time, note.getText().toString(), alarmTime);
                clients.save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
                Toast.makeText(getActivity(), getString(R.string.msg_client_add), Toast.LENGTH_SHORT).show();
                DialogAddClientListener dialogAddClientListener = (DialogAddClientListener) getActivity();
                dialogAddClientListener.onDialogAddClient(1);
                Log.d(LOG_TAG, "Добавлена запись в базу clients");
            }
        });
    return view;
    }

}
