package by.lykashenko.clientservice.Fragments;

import android.content.Context;
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
import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.Dialog.DialogFragmentUpdate;
import by.lykashenko.clientservice.R;

public class AddClientFragment extends Fragment {

    public static final String CLIENT_FIO = "client_fio";
    public static final String CLIENT_PHONE = "client_phone";
    public static final String CLIENT_ID = "client_id";
    private String phoneNumber;
    private String LOG_TAG = "addClient";
    private TextView number, fio, note, date_time;
    private String[] alarmSet;
    private Spinner spinnerAlarmSet;
    private Toolbar toolbar;
    private FloatingActionButton fadAddClient;
    private SharedPreferences sharedPreferences;
    private Integer positionSpinner;
    private Long alarmTime;
    private View view;

    public interface DialogAddClientListener {
        void onDialogAddClient(Integer state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmSet = getResources().getStringArray(R.array.timeAlarmSet);

        phoneNumber = getArguments().getString("phonenumber");
        Log.d(LOG_TAG,"number = "+phoneNumber);
        //проверка есть ли такой номер в базе
        List<Clients> list = new Select().from(Clients.class).where("phonenumber = ?",phoneNumber).execute();

        if (list.isEmpty()) { //если нет в базе такого номера

            view = inflater.inflate(R.layout.fragment_add_client, container, false);
            toolbar = (Toolbar) view.findViewById(R.id.toolbaraddclient);
            toolbar.setTitle(getResources().getString(R.string.add_client));
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });
            //записи
            number = (TextView) view.findViewById(R.id.input_phone);
            fio = (TextView) view.findViewById(R.id.input_name);
            note = (TextView) view.findViewById(R.id.input_note);
            date_time = (TextView) view.findViewById(R.id.input_time_date);

            number.setText(phoneNumber);

            //выпадающий список напоминаний
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, alarmSet);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAlarmSet = (Spinner) view.findViewById(R.id.spinner_alarm);
            spinnerAlarmSet.setAdapter(adapter);
            spinnerAlarmSet.setPrompt(getResources().getString(R.string.alarm_set));
            spinnerAlarmSet.setSelection(1);
            spinnerAlarmSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    positionSpinner = position;
                    Toast.makeText(getActivity(), "Повторить через " + alarmSet[position], Toast.LENGTH_SHORT).show();
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


                    }

                    Clients clients = new Clients(user_id, fio.getText().toString(), phoneNumber, current_time, note.getText().toString(), alarmTime, 0);
                    clients.save();
                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();
                    Toast.makeText(getActivity(), getString(R.string.msg_client_add), Toast.LENGTH_SHORT).show();

                    DialogAddClientListener dialogAddClientListener = (DialogAddClientListener) getActivity();
                    dialogAddClientListener.onDialogAddClient(1);
                    Log.d(LOG_TAG, "Добавлена запись в базу clients");
                }
            });

        }else{
            DialogFragmentUpdate dialogUpdate = new DialogFragmentUpdate();
            Bundle bundle = new Bundle();
            bundle.putString("phone", phoneNumber);
            dialogUpdate.setArguments(bundle);
            dialogUpdate.show(getActivity().getSupportFragmentManager(),"Update");
            Log.d(LOG_TAG, "выбор действий");
        }
    return view;
    }

}
