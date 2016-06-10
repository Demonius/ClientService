package by.lykashenko.clientservice.BD;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Дмитрий on 26.05.16.
 */

@Table(name = "User", id = "_id")
public class Authorisation extends Model {

    @Column(name = "login")
    public String login;

    @Column(name = "passwords")
    public  String passwords;

    @Column(name = "state")
    public Integer state;

    public Authorisation(){
        super();
    }

    public Authorisation(String login, String passwords, Integer state){
        this.login = login;
        this.passwords = passwords;
        this.state = state;
    }

}
