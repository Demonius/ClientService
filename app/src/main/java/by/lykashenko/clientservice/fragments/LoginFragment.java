package by.lykashenko.clientservice.fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

import by.lykashenko.clientservice.BD.Authorisation;
import by.lykashenko.clientservice.R;


public class LoginFragment extends Fragment {


    private String LOG_TAG = "loginFragment";
    private TextView login_input, passwords_input;
    private Button loginButton;
    private TextView registration;
    private TextInputLayout input_login_layout, input_passwords_layout;
    private DialogFragment dialogRegistration;
    private CheckBox checkBoxLoginState;
    private SharedPreferences sharedPreferences;
    public static final String LOGIN_TAG = "login";
    public static final String PASSWORDS_TAG = "passwords";
    public static final String ID_USER_TAG = "id";
    public static final String PREFERENCES_NAME= "login_check";
    public static final String STATE_LOGIN = "login_state";
    private Integer state;
    private Integer save_preferences = 0;



    public interface DialogAddUserListener {
        void onDialogAddUser(Integer state);
    }

    public interface LoginPressButtonListener{
        void onLoginPressButton(Integer state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        state = getArguments().getInt("state");
        //инициализация элементов
        input_login_layout = (TextInputLayout) view.findViewById(R.id.input_layout_login);
        input_passwords_layout = (TextInputLayout) view.findViewById(R.id.input_layout_passwords);
        login_input = (TextView) view.findViewById(R.id.input_login);
        passwords_input= (TextView) view.findViewById(R.id.input_passwords);
        loginButton = (Button) view.findViewById(R.id.buttonLogin);
        registration = (TextView) view.findViewById(R.id.register);
        checkBoxLoginState = (CheckBox) view.findViewById(R.id.checkBoxStateLogin);


        login_input.setText("Admin");
        passwords_input.setText("admin");
        login_input.setEnabled(false);
        passwords_input.setEnabled(false);
        checkBoxLoginState.setEnabled(false);




        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Integer state_id = sharedPreferences.getInt(STATE_LOGIN, 0);

        if (state_id == 1) {
            login_input.setText(sharedPreferences.getString(LOGIN_TAG, ""));
            passwords_input.setText(sharedPreferences.getString(PASSWORDS_TAG, ""));
            checkBoxLoginState.setChecked(true);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginProgram();

        }else{
//            login_input.setText(sharedPreferences.getString(LOGIN_TAG, ""));
//            checkBoxLoginState.setChecked(false);
        }
        if (save_preferences == 0){
            SavePreferences();
            save_preferences = 1;
        }

        checkBoxLoginState.setChecked(true);
        checkBoxLoginState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (validateLogin() && validatePasswords()) {

                        SavePreferences();
                        Toast.makeText(getActivity(),getResources().getString(R.string.true_chk_login),Toast.LENGTH_LONG).show();


                    }else{
                        Toast.makeText(getActivity(),getResources().getString(R.string.err_chk_login),Toast.LENGTH_LONG).show();
                        checkBoxLoginState.setChecked(false);
                    }

                }else{
                    passwords_input.setText("");
                    sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(STATE_LOGIN, 0);
                    editor.apply();
                    Toast.makeText(getActivity(),getResources().getString(R.string.false_chk_login),Toast.LENGTH_LONG).show();
                }
            }
        });

//        registration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogAddUserListener dialogAddUserListener = (DialogAddUserListener) getActivity();
//                dialogAddUserListener.onDialogAddUser(1);
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginProgram();
            }
        });


        return view;
    }

    private void SavePreferences() {
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STATE_LOGIN, 1);
        editor.putString(LOGIN_TAG, login_input.getText().toString());
        editor.putString(PASSWORDS_TAG, passwords_input.getText().toString());
        editor.putString(ID_USER_TAG, getUserId(login_input.getText().toString()));
        editor.apply();
    }

    private void loginProgram() {
        if (validateLogin() && validatePasswords()) {
            String Login = login_input.getText().toString();
            String Passwords = passwords_input.getText().toString();

            if (getUser(Login)) {
                if (state == 1) {
                    LoginPressButtonListener loginPressButtonListener = (LoginPressButtonListener) getActivity();
                    loginPressButtonListener.onLoginPressButton(3);
                }else{
                    LoginPressButtonListener loginPressButtonListener = (LoginPressButtonListener) getActivity();
                    loginPressButtonListener.onLoginPressButton(1);
                }
            } else {
                LoginPressButtonListener loginPressButtonListener = (LoginPressButtonListener) getActivity();
                loginPressButtonListener.onLoginPressButton(2);
            }
        }
    }

    private String getUserId(String login) {
        String id = "0";
        List<Authorisation> user = new Select().from(Authorisation.class).where("login = ?", login).execute();
        if (user!=null){
            id = Long.toString(user.get(0).getId());
        }


        return id;
    }

    private boolean getUser(String text) {
        Boolean state;
        if ((new Select().from(Authorisation.class).where("login = ?", text).execute())!=null){
            state = true;
        }else{
            state = false;
        }

        return state;
    }

    private boolean validateLogin() {
        if (login_input.getText().toString().trim().isEmpty()){
            input_login_layout.setError(getString(R.string.err_msg_nick));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                login_input.setShowSoftInputOnFocus(true);
            }
            return false;

        }else{
            input_login_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validatePasswords() {
        if (passwords_input.getText().toString().trim().isEmpty()){
            input_passwords_layout.setError(getString(R.string.err_msg_passwords));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                passwords_input.setShowSoftInputOnFocus(true);
            }
            return false;

        }else{
            input_passwords_layout.setErrorEnabled(false);
            return true;
        }
    }
}
