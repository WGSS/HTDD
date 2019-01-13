package cn.dgut.controller;

import cn.dgut.model.Login;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

@Clear
public class LoginController extends Controller{

    public void index(){
        setAttr("msg", "");
        render("login.html");
    }

    public void validatelogin(){
        Login login = getModel(Login.class);
        Record sqllogin = Db.findById("Login","Username",login.getStr("Username")); //根据用户名得到数据库中的对象
        if(sqllogin!=null) { //该用户存在
            String sqlpasscode = sqllogin.getStr("Passcode"); //获取数据库存储的加密过的密码
            boolean sqlisadmin = sqllogin.getBoolean("Isadmin"); //获取数据库中身份信息
            boolean isadmin = login.getBoolean("Isadmin"); //输入的身份信息
            String passcode = login.getStr("Passcode"); //使用SHA256对输入的密码进行加密
            if(passcode.equals(sqlpasscode) && sqlisadmin==isadmin){ //密码验证 && 身份验证
                redirect("/"); //登录成功页面跳转
                setSessionAttr("user", login.getStr("Username"));
                return;
            }
        }
            render("login.html");
    }
    public void add(){
        Login login = getModel(Login.class);
        Record sqllogin = Db.findById("Login","Username",login.getStr("Username")); //鏍规嵁鐢ㄦ埛鍚嶅緱鍒版暟鎹簱涓殑瀵硅薄
        if(sqllogin!=null) {
            render("login.html");
            return;
                       }

            Record user = new Record().set("Username", login.getStr("Username")).set("Passcode", login.getStr("Passcode")).set("Isadmin",login.getBoolean("Isadmin"));
            Db.save("login", user);
            redirect("/");
            setSessionAttr("user", login.getStr("Username"));
            return;
        }

    public void exit(){
        setSessionAttr("user", null);
        redirect("/");
    }
}
