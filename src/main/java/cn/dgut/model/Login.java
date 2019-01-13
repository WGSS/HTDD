package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Login extends Model<Login>{
    public static final Login login = new Login();
    public static final Login dao = new Login().dao();
}
