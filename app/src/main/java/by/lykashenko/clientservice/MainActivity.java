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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.concurrent.TimeUnit;

import by.lykashenko.clientservice.BD.Authorisation;
import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.Dialog.DialogFragmentRegistration;
import by.lykashenko.clientservice.fragments.AddClientFragment;
import by.lykashenko.clientservice.fragments.LoginFragment;
import by.lykashenko.clientservice.fragments.SpisokClientFragment;

/**
 * Created by Дмитрий on 03.06.16.
 */
public class MainActivity extends AppCompatActivity implements LoginFragment.DialogAddUserListener, DialogFragmentRegistration.SaveUserListener,
        LoginFragment.LoginPressButtonListener, AddClientFragment.DialogAddClientListener{
    private static final String LOG_TAG = "MainActivity";
    private SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private Integer STATE_ADD_USER = 0;
    private String phoneNumber;
    private Integer state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutMain);

        //диалог permission
        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        Log.d(LOG_TAG, "number main = "+phoneNumber);
        state = intent.getIntExtra("addclient", 0);
        sharedPreferences = getSharedPreferences(LoginFragment.PREFERENCES_NAME, Context.MODE_PRIVATE);
        Integer state_id = sharedPreferences.getInt(LoginFragment.STATE_LOGIN, 0);
        Log.d(LOG_TAG, "state_id main (login) = "+Integer.toString(state_id));
        Log.d(LOG_TAG, "state main (add_user) = "+ Integer.toString(state));

        if (state == 1) {
            if (state_id == 1) {

                startAddClient(phoneNumber);
                STATE_ADD_USER = 0;

            } else {
                startLoginFragment();
                STATE_ADD_USER = 1;
            }
        }else{
            startLoginFragment();
            STATE_ADD_USER=0;
        }

        //активация базы данных
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("Manager.db").setModelClasses(Authorisation.class, Clients.class).create();
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
        loginFragment.setArguments(bundle);
        ftrans.commit();
        Log.d(LOG_TAG, "start login_fragment");
    }

    @Override
    public void onDialogAddUser(Integer state) {
        if (state == 1){
            DialogFragmentRegistration dialogRegistration = new DialogFragmentRegistration();
            dialogRegistration.show(getSupportFragmentManager(),"Registration");
            Log.d(LOG_TAG, "регистрация");
        }
    }

    @Override
    public void onSaveUser(ContentValues infoClient) {
        ActiveAndroid.beginTransaction();
        Authorisation autorisation = new Authorisation(infoClient.get("login").toString(),infoClient.get("passwords").toString(),infoClient.getAsInteger("function"));
        autorisation.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        Toast.makeText(this,"Пользователь добавлен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginPressButton(Integer state) {
        switch (state){
            case 1:
                Log.d(LOG_TAG, "такой пользователь есть");
                startSpisokClients();
                break;
            case 2:
                Snackbar.make(coordinatorLayout,getResources().getString(R.string.err_user), Snackbar.LENGTH_SHORT).show();
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

    private void startSpisokClients() {
        SpisokClientFragment clientFragment = new SpisokClientFragment();
        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.frameLayout, clientFragment);
        ftrans.commit();
    }

    @Override
    public void onDialogAddClient(Integer state) {
        if (state == 1){
            finish();
        }
    }
}
