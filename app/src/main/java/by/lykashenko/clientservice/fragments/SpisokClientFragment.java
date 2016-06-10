package by.lykashenko.clientservice.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
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

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private List<Clients> getAllClient;
    private String LOG_TAG = "fragmentspisok";
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spisok, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbarspisok);
        toolbar.setTitle(getResources().getString(R.string.spisok_client));

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayoutSpisok);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_client);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);
        getAllClient = GetAllClient();
        if (getAllClient.isEmpty()){
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
            m_list_client=getAllClient;
        }

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            public TextView nameClient, phoneClient;
            public TextView timeCall, dateCall, alarmCall;

            public CardView cv;

            public PersonViewHolder (final View item_view){
                super(item_view);
                nameClient = (TextView)item_view.findViewById(R.id.textNameClient);
                phoneClient = (TextView)item_view.findViewById((R.id.textPhoneNumber));
                timeCall = (TextView) item_view.findViewById(R.id.timeCall);
                dateCall = (TextView) item_view.findViewById(R.id.dateCall);
                alarmCall = (TextView) item_view.findViewById(R.id.alarmCall);
                cv = (CardView) item_view.findViewById(R.id.card_view_clients);

                item_view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Integer position_click = getAdapterPosition();


                        Log.d("manager", "position click = "+Integer.toString(position_click));

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
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            final String time_output = timeFormat.format(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
            final String date_output = dateFormat.format(date);

            holder.timeCall.setText(time_output);
            holder.dateCall.setText(date_output);

            Long setAlarm = m_list_client.get(position).alarmset;
            if ((setAlarm-System.currentTimeMillis())<=600000&&(setAlarm-System.currentTimeMillis())>0){
//                holder.cv.setBackgroundColor(getResources().getColor(R.color.backgroundRed));
                holder.cv.setCardBackgroundColor(getResources().getColor(R.color.backgroundRed));
            }
            if ((setAlarm-System.currentTimeMillis())<1800000&&(setAlarm-System.currentTimeMillis())>600000){
                holder.cv.setCardBackgroundColor(getResources().getColor(R.color.backgroundYellow));
            }
            Date alarmDate = new Date(setAlarm);
            SimpleDateFormat alarmDateTime = new SimpleDateFormat("HH:mm dd-MMMM-yyyy");
            final String alarm_output = alarmDateTime.format(alarmDate);

            holder.alarmCall.setText("Напомнить в "+alarm_output);


        }

        @Override
        public int getItemCount() {
            return m_list_client.size();
        }
    }

    private List<Clients> GetAllClient() {

        List<Clients> list = new Select().from(Clients.class).execute();

        Integer i = 0;
        while (i <= list.size()-1){
            Long currentTime = System.currentTimeMillis();
                if ((currentTime-list.get(i).alarmset)>300000){
                ActiveAndroid.beginTransaction();
                Clients clients = Clients.load(Clients.class, list.get(i).getId());
                clients.user_id = "10";
                clients.save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
//                new Delete().from(Clients.class).where("_id = ?", list.get(i).getId()).execute();
            }
            i=i+1;
        }
        list = new Select().from(Clients.class).where("user_id = ?", "1").orderBy("alarmset").execute();



        return list;
    }
}
