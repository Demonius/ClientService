package by.lykashenko.clientservice.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import by.lykashenko.clientservice.R;

/**
 * Created by Дмитрий on 12.06.16.
 */
public class DialogFragmentUpdate extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private final String LOG_TAG = "dialogUpdate";

    private TextView editClient, deleteClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setCanceledOnTouchOutside(false);
        View v = inflater.inflate(R.layout.update_client, null);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbarUpdateClient);
        toolbar.setTitle(R.string.titleUpdateClient);

        editClient = (TextView) v.findViewById(R.id.textViewUpdateClient);
        deleteClient = (TextView) v.findViewById(R.id.textViewDeleteClient);

        editClient.setOnClickListener(this);
        deleteClient.setOnClickListener(this);



        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.textViewUpdateClient:
                Toast.makeText(getActivity(),"Редактировать данные", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textViewDeleteClient:
                Toast.makeText(getActivity(),"Удалить данные", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
