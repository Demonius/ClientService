package by.lykashenko.clientservice.BD;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Дмитрий on 12.06.16.
 */

@Table(name = "Alarm", id = "_id")
public class AlarmNotification extends Model {

    @Column(name = "id_alarm")
    public String idAlarm;

    @Column(name = "fio")
    public String fio;

    @Column(name = "phone")
    public String phone;

    @Column(name = "alarm_set")
    public Long alarmSet;

    public AlarmNotification(){
        super();
    }

    public AlarmNotification(String idAlarm, String fio, String phone, Long alarmSet){
        this.idAlarm=idAlarm;
        this.fio=fio;
        this.phone=phone;
        this.alarmSet=alarmSet;
    }
}