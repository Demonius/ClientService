package by.lykashenko.clientservice.Fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import by.lykashenko.clientservice.BD.Clients;
import by.lykashenko.clientservice.R;

/**
 * Created by Дмитрий on 03.06.16.
 */
public class SpisokClientFragment extends Fragment {

    public interface DialogPopupClientListener {
        void onDialogPopupClient(Integer state, Long id);
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private List<Clients> getAllClient = null;
    private String LOG_TAG = "fragmentspisok";
    private Toolbar toolbar;
    private Spinner spinnerSpisok;
    private Integer startFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spisok, container, false);
        startFragment = 1;

//        toolbar.setTitle(getResources().getString(R.string.spisok_client));
        toolbar = (Toolbar) view.findViewById(R.id.toolbarspisok);
        spinnerSpisok = (Spinner) view.findViewById(R.id.spinner_nav);

        String[] list = getResources().getStringArray(R.array.checkListClient);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpisok.setAdapter(adapterSpinner);
        spinnerSpisok.setSelection(0);

        spinnerSpisok.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAllClient.clear();
                getAllClient.addAll(GetAllClient(position));
                if (startFragment != 1) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    startFragment = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayoutSpisok);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_client);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        getAllClient = GetAllClient(0);
        if (getAllClient.isEmpty()) {
            coordinatorLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.no_client));
        } else {

            mAdapter = new MyAdapter(getAllClient);
            mRecyclerView.setAdapter(mAdapter);

        }

        return view;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PersonViewHolder> {

        private List<Clients> m_list_client;

        public MyAdapter(List<Clients> getAllClient) {
            m_list_client = getAllClient;
        }

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            public TextView nameClient, phoneClient;
            public TextView timeCall, dateCall, alarmCall;

            public CardView cv;

            public PersonViewHolder(final View item_view) {
                super(item_view);
                nameClient = (TextView) item_view.findViewById(R.id.textNameClient);
                phoneClient = (TextView) item_view.findViewById((R.id.textPhoneNumber));
                timeCall = (TextView) item_view.findViewById(R.id.timeCall);
                alarmCall = (TextView) item_view.findViewById(R.id.alarmCall);
                cv = (CardView) item_view.findViewById(R.id.card_view_clients);


                item_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Integer position_click = getAdapterPosition();
                        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                        popupMenu.inflate(R.menu.popup_menu_spisok);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Long id_client = m_list_client.get(position_click).getId();
                                DialogPopupClientListener dialogPopupClientListener = (DialogPopupClientListener) getActivity();
                                switch (item.getItemId()){
                                    case R.id.item_delete_client:

                                        dialogPopupClientListener.onDialogPopupClient(2, id_client);
                                        getAllClient.clear();
                                        getAllClient.addAll(GetAllClient(spinnerSpisok.getSelectedItemPosition()));
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    case R.id.item_edit_client:

                                        dialogPopupClientListener.onDialogPopupClient(1, id_client);
                                        getAllClient.clear();
                                        getAllClient.addAll(GetAllClient(spinnerSpisok.getSelectedItemPosition()));
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        return false;
                    }
                });
                item_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Integer position_click = getAdapterPosition();

                        Log.d("manager", "position click = " + Integer.toString(position_click));

                    }
                });
            }
        }

        @Override
        public MyAdapter.PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_client, parent, false);
            PersonViewHolder pvh = new PersonViewHolder(v);
            return pvh;

        }

        @Override
        public void onBindViewHolder(PersonViewHolder holder, int position) {
            holder.nameClient.setText(m_list_client.get(position).client.toString());
            holder.phoneClient.setText(m_list_client.get(position).phone.toString());

            Long timeDate = m_list_client.get(position).timeset;
            Date date = new Date(timeDate);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm dd-MMMM-yyyy");
            StringBuilder builder = new StringBuilder();
            builder.append(getResources().getString(R.string.callended)).append(" ").append(timeFormat.format(date));
            final String time_output = builder.toString();

            holder.timeCall.setText(time_output);

            Long setAlarm = m_list_client.get(position).alarmset;
            if ((setAlarm - System.currentTimeMillis()) <= 600000 && (setAlarm - System.currentTimeMillis()) > 0) {
                holder.cv.setCardBackgroundColor(getResources().getColor(R.color.backgroundRed));
            }
            if ((setAlarm - System.currentTimeMillis()) < 1800000 && (setAlarm - System.currentTimeMillis()) > 600000) {
                holder.cv.setCardBackgroundColor(getResources().getColor(R.color.backgroundYellow));
            } else {
                if ((setAlarm - System.currentTimeMillis()) > 1800000) {
                    holder.cv.setCardBackgroundColor(getResources().getColor(R.color.backgroundGreen));
                }
            }
            Date alarmDate = new Date(setAlarm);
            SimpleDateFormat alarmDateTime = new SimpleDateFormat("HH:mm dd-MMMM-yyyy");
            final String alarm_output = alarmDateTime.format(alarmDate);

            holder.alarmCall.setText("Напомнить в " + alarm_output);


        }

        @Override
        public int getItemCount() {
            return m_list_client.size();
        }
    }

    private List<Clients> GetAllClient(Integer position) {

        List<Clients> list = null;

        List<Clients> list1 = new Select().from(Clients.class).where("user_id = ? and state < ?", 1, 5).orderBy("alarmset").execute();

        Integer i = 0;
        while (i <= list1.size() - 1) {
            Long currentTime = System.currentTimeMillis();
            if ((currentTime - list1.get(i).alarmset) > 14400000) {
                ActiveAndroid.beginTransaction();
                Clients clients = Clients.load(Clients.class, list1.get(i).getId());
                clients.state = 10;
                clients.save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
            }
            i = i + 1;
        }
        switch (position) {
            case 0:
                list = new Select().from(Clients.class).where("user_id = ?", 1).orderBy("alarmset").execute();
                break;
            case 1:
                list = new Select().from(Clients.class).where("user_id = ? and state < ?", 1, 5).orderBy("alarmset").execute();
                break;
            case 2:
                list = new Select().from(Clients.class).where("user_id = ? and state = ?", 1, 5).orderBy("alarmset").execute();
                break;
            case 3:
                list = new Select().from(Clients.class).where("user_id = ? and state = ?", 1, 10).orderBy("alarmset").execute();
                break;
        }

//        mAdapter.notifyDataSetChanged();

        return list;
    }

}
