package by.lykashenko.clientservice.BD;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Дмитрий on 26.05.16.
 */

@Table(name = "Clients", id = "_id")
public class Clients extends Model {

    @Column(name = "user_id")
    public String user_id;

    @Column(name = "client")
    public String client;

    @Column(name = "phonenumber")
    public String phone;

    @Column(name = "timeset")
    public Long timeset;

    @Column(name = "noteset")
    public String note;

    @Column(name = "alarmset")
    public Long alarmset;

    @Column(name= "state")
    public Integer state;

    public Clients(){
        super();
    }

    public Clients(String user_id,String client, String phone, Long timeset, String note, Long alarmset, Integer state){
        this.user_id=user_id;
        this.client=client;
        this.phone=phone;
        this.timeset=timeset;
        this.note=note;
        this.alarmset=alarmset;
        this.state=state;
    }

}
