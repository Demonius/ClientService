package by.lykashenko.clientservice;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import by.lykashenko.clientservice.BD.AlarmNotification;
import by.lykashenko.clientservice.BD.Authorisation;
import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.Dialog.DialogFragmentRegistration;
import by.lykashenko.clientservice.Dialog.DialogFragmentUpdate;
import by.lykashenko.clientservice.Fragments.AddClientFragment;
import by.lykashenko.clientservice.Fragments.EditClientFragment;
import by.lykashenko.clientservice.Fragments.LoginFragment;
import by.lykashenko.clientservice.Fragments.SpisokClientFragment;

/**
 * Created by Дмитрий on 03.06.16.
 */
public class MainActivity extends AppCompatActivity implements LoginFragment.DialogAddUserListener, DialogFragmentRegistration.SaveUserListener,
        LoginFragment.LoginPressButtonListener, AddClientFragment.DialogAddClientListener,
        SpisokClientFragment.DialogPopupClientListener, EditClientFragment.CloseEditClientListener,
        DialogFragmentUpdate.DialogEditListener{
    private static final String LOG_TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private Integer STATE_ADD_USER = 0;
    private String phoneNumber;
    public Integer BOOT_STATE = 0;
    private Integer state;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutMain);

//        toolbar = (Toolbar) findViewById(R.id.toolbarmain);

        //диалог permission
        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        Intent intent = getIntent();

        phoneNumber = intent.getStringExtra("phoneNumber");
        BOOT_STATE = intent.getIntExtra("boot", 0);
        Log.d(LOG_TAG, "number main = " + phoneNumber);
        state = intent.getIntExtra("addclient", 0);
        sharedPreferences = getSharedPreferences(LoginFragment.PREFERENCES_NAME, Context.MODE_PRIVATE);
        Integer state_id = sharedPreferences.getInt(LoginFragment.STATE_LOGIN, 0);
        Log.d(LOG_TAG, "state_id main (login) = " + Integer.toString(state_id));
        Log.d(LOG_TAG, "state main (add_user) = " + Integer.toString(state));

        if (state == 1) {
            if (state_id == 1) {

                startAddClient(phoneNumber);
                STATE_ADD_USER = 0;

            } else {
                startLoginFragment();
                STATE_ADD_USER = 1;
            }
        } else {
            startLoginFragment();
            STATE_ADD_USER = 0;
        }

        //активация базы данных
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("Manager.db").setModelClasses(Authorisation.class, Clients.class, AlarmNotification.class).create();
        ActiveAndroid.initialize(dbConfiguration);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                // TODO Auto-generated method stub
                if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                    finish();

            }
        });


    }

    private void startLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.add(R.id.frameLayout, loginFragment, "fragment_login");
        Bundle bundle = new Bundle();
        bundle.putInt("state", STATE_ADD_USER);
        bundle.putInt("boot", BOOT_STATE);
        loginFragment.setArguments(bundle);
        ftrans.commit();
        Log.d(LOG_TAG, "start login_fragment");
//        BOOT_STATE = 0;
    }

    @Override
    public void onDialogAddUser(Integer state) {
        if (state == 1) {
            DialogFragmentRegistration dialogRegistration = new DialogFragmentRegistration();
            dialogRegistration.show(getSupportFragmentManager(), "Registration");
            Log.d(LOG_TAG, "регистрация");
        }
    }

    @Override
    public void onSaveUser(ContentValues infoClient) {
        ActiveAndroid.beginTransaction();
        Authorisation autorisation = new Authorisation(infoClient.get("login").toString(), infoClient.get("passwords").toString(), infoClient.getAsInteger("function"));
        autorisation.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        Toast.makeText(this, "Пользователь добавлен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginPressButton(Integer state) {
        switch (state) {
            case 1:
                Log.d(LOG_TAG, "такой пользователь есть");
                startSpisokClients();
                break;
            case 2:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.err_user), Snackbar.LENGTH_SHORT).show();
                break;
            case 3:
                startAddClient(phoneNumber);
                break;
        }
    }

    private void startAddClient(String phoneNumber) {
        AddClientFragment clientFragment = new AddClientFragment();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("phonenumber", phoneNumber);
        clientFragment.setArguments(bundle);
        ftrans.replace(R.id.frameLayout, clientFragment);
        ftrans.commit();


    }

    private void startEditClient(Long id_client) {
        EditClientFragment editFragment = new EditClientFragment();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putLong("id_client", id_client);
        editFragment.setArguments(bundle);
        ftrans.replace(R.id.frameLayout, editFragment);
        ftrans.commit();


    }

    private void startSpisokClients() {
        SpisokClientFragment clientFragment = new SpisokClientFragment();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.frameLayout, clientFragment);
        ftrans.commit();


    }

    @Override
    public void onDialogAddClient(Integer state) {
        if (state == 1) {
            finish();
        }
        SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
        setAlarmNotification.setAlarmNotification(this, phoneNumber);
    }


    @Override
    public void onDialogPopupClient(Integer state, Long id) {
        switch (state){
            case 1:
                startEditClient(id);
                break;
            case 2:
                Log.d(LOG_TAG,"Delete from clients where _id = "+Long.toString(id));
                new Delete().from(Clients.class).where("_id = ?", id).execute();
                new Delete().from(AlarmNotification.class).where("id_alarm = ?", Long.toString(id)).execute();
                SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
                setAlarmNotification.deleteAlarmNotification(this, Long.toString(id));
                break;
        }
    }

    @Override
    public void onCloseEditClient(Integer state) {
        if (state == 1){
            startSpisokClients();
        }
    }

    @Override
    public void onDialogEditClient(Integer state, String phoneNumber) {

        List<Clients> list = new Select().from(Clients.class).where("phonenumber = ?", phoneNumber).execute();
        Long id_client = list.get(0).getId();


        if (state == 0){
            ActiveAndroid.beginTransaction();
            Clients clients = Clients.load(Clients.class, id_client);
            clients.timeset = System.currentTimeMillis();
            clients.state = 2;
            clients.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            startEditClient(list.get(0).getId());
        }

        if (state == 1){
            ActiveAndroid.beginTransaction();
            Clients clients = Clients.load(Clients.class, id_client);
            clients.alarmset = Long.valueOf(0);
            clients.save();
            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();
            SetAlarmNotification setAlarmNotification = new SetAlarmNotification();
            setAlarmNotification.deleteAlarmNotification(this, Long.toString(id_client));
            finish();
        }
    }

}
