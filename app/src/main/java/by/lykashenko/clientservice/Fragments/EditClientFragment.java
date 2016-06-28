package by.lykashenko.clientservice.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.R;
import by.lykashenko.clientservice.SetAlarmNotification;

/**
 * Created by Дмитрий on 15.06.16.
 */
public class EditClientFragment extends Fragment {

    public interface CloseEditClientListener {
        void onCloseEditClient(Integer state);
    }
    private static final String LOG_TAG = "Edit_client";
    private EditText number, fio, note;
    private TextView dateEdit, timeEdit;
    private View view;
    private Long id_client;
    private List<Clients> list;
    private Toolbar toolbar;
    private Long alarmSetNotification;
    private FloatingActionButton fadeditClient;
    private Long alarmTime, setTimeAlarm;
    private SimpleDateFormat simpleDateFormat, simpleTimeFormat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.fragment_edit_client, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbareditclient);
        toolbar.setTitle(getResources().getString(R.string.editClient));

        id_client = getArguments().getLong("id_client");
        list = new Select().from(Clients.class).where("_id = ?", id_client).execute();

        fio = (EditText) view.findViewById(R.id.editTextFioEdit);
        number = (EditText) view.findViewById(R.id.editTextPhoneEdit);
        note = (EditText) view.findViewById(R.id.editTextNoteEdit);

        timeEdit = (TextView) view.findViewById(R.id.textViewTimeAlarm);
        dateEdit = (TextView) view.findViewById(R.id.textViewDateAlarm);



        fio.setText(list.get(0).client.toString());
        number.setText(list.get(0).phone.toString());
        note.setText(list.get(0).note.toString());

        final Long time_date = list.get(0).alarmset;
        final Date date = new Date(time_date);


            simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
            simpleTimeFormat = new SimpleDateFormat("HH:mm");

            timeEdit.setText(simpleTimeFormat.format(date));
            dateEdit.setText(simpleDateFormat.format(date));



        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        Date time_set = calendar.getTime();
                        timeEdit.setText(simpleTimeFormat.format(time_set));

                    }
                }, date.getHours(), date.getMinutes(), true);
                timePickerDialog.show();


            }
        });
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date_set = calendar.getTime();
                        dateEdit.setText(simpleDateFormat.format(date_set));
                    }
                }, Integer.parseInt((new SimpleDateFormat("yyyyy")).format(date)), Integer.parseInt((new SimpleDateFormat("MM")).format(date)), Integer.parseInt((new SimpleDateFormat("dd")).format(date)));
                datePickerDialog.show();
            }
        });

        fadeditClient = (FloatingActionButton) view.findViewById(R.id.buttonFloatEditOk);
        fadeditClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //обновление данных о клиенте
                ActiveAndroid.beginTransaction();
                Clients clients = Clients.load(Clients.class, id_client);
                clients.client = fio.getText().toString();
                clients.phone = number.getText().toString();
                clients.note = note.getText().toString();

                StringBuilder builder = new StringBuilder().append(timeEdit.getText().toString()).append(" ").append(dateEdit.getText().toString());
                String date_time = builder.toString();
                Log.d(LOG_TAG, "строка даты и времени = "+date_time);
                SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("HH:mm dd-MMMM-yyyyy", Locale.getDefault());
                Date time_set = null;
                try {
                    time_set = simpleDateTimeFormat.parse(date_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                clients.alarmset = time_set.getTime();

                Log.d(LOG_TAG, "время напоминания"+Long.toString(time_set.getTime()-System.currentTimeMillis()));

                clients.save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();

                //обновление напомнинания
                SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
                setAlarmNotification.deleteAlarmNotification(getActivity(), Long.toString(id_client));
                setAlarmNotification.setAlarmNotification(getActivity(), number.getText().toString());

                CloseEditClientListener closeEditClientListener = (CloseEditClientListener) getActivity();
                closeEditClientListener.onCloseEditClient(1);

            }
        });

        return view;
    }

}
