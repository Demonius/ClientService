package by.lykashenko.clientservice.Dialog;

import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import by.lykashenko.clientservice.R;

/**
 * Created by Дмитрий on 02.06.16.
 */
public class DialogFragmentRegistration extends android.support.v4.app.DialogFragment {

    private String[] function = {"менеджер","старший менеджер","начальник отдела","администратор"};
    private TextInputLayout input_login_add_layout, input_passwords_add_layout;
    public TextView input_login_add, input_passwords_add;
    private TextView save, cancel;
    private Spinner spinnerFunction;
    private Toolbar toolbar;
    private ContentValues cvUser;

    public interface SaveUserListener {
        void onSaveUser(ContentValues infoClient);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setCanceledOnTouchOutside(false);
        View v = inflater.inflate(R.layout.add_user, null);

        toolbar = (Toolbar) v.findViewById(R.id.toolbaradduser);
        toolbar.setTitle(getResources().getString(R.string.user_add));

        input_login_add_layout = (TextInputLayout) v.findViewById(R.id.input_layout_login_add);
        input_passwords_add_layout = (TextInputLayout) v.findViewById(R.id.input_layout_passwords_add);
        input_login_add = (TextView) v.findViewById(R.id.input_login_add);
        input_passwords_add = (TextView) v.findViewById(R.id.input_passwords_add);

        spinnerFunction = (Spinner) v.findViewById(R.id.spinnerFunction);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, function);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFunction.setAdapter(adapter);
        spinnerFunction.setPrompt("Выберите вашу должность");
        spinnerFunction.setSelection(0);

        save = (TextView) v.findViewById(R.id.textViewSave);
        cancel = (TextView) v.findViewById(R.id.textViewCancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((validateLogin()==true)&&(validatePasswords()==true)) {

                    cvUser = new ContentValues();
                    cvUser.put("login", input_login_add.getText().toString());
                    cvUser.put("passwords",input_passwords_add.getText().toString());
                    cvUser.put("function", spinnerFunction.getSelectedItemPosition());
                    SaveUserListener listener = (SaveUserListener) getActivity();
                    listener.onSaveUser(cvUser);
                    dismiss();
                }
            }
        });


        return v;
    }
    private boolean validateLogin() {
        if (input_login_add.getText().toString().trim().isEmpty()){
            input_login_add_layout.setError(getString(R.string.err_msg_nick));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                input_login_add.setShowSoftInputOnFocus(true);
            }
            return false;

        }else{
            input_login_add_layout.setErrorEnabled(false);
            return true;
        }

    }
    private boolean validatePasswords() {
        if (input_passwords_add.getText().toString().trim().isEmpty()){
            input_passwords_add_layout.setError(getString(R.string.err_msg_nick));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                input_passwords_add.setShowSoftInputOnFocus(true);
            }
            return false;

        }else{
            input_passwords_add_layout.setErrorEnabled(false);
            return true;
        }

    }

}
