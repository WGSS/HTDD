package cn.dgut.controller;

import cn.dgut.model.Staff;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;

public class StaffController extends Controller{

    public void index(){
        render("staff.html");
    }

    public void showlist(){
        List<Record> stafflist = Db.find("select * from Staff");
        renderJson(stafflist);
    }
    public void edit(){
        setAttr("method", "update");
        String sid = getPara();
        Record staff = Db.findById("Staff", "Sid", sid);
        setAttr("staff", staff);
        render("edit.html");
    }

    public void update(){
        Staff staff = getModel(Staff.class, "");
        staff.update();
        redirect("/staff");
    }

    @Before(Tx.class)
    public void delete(){
        String sid = getPara();
        Record staff = Db.findById("Staff", "Sid", sid);
        Db.update("update Contract set Sid=NULL where Sid=?", sid);
        Db.deleteById("Staff", "Sid", sid);
        Db.update("delete from Login where Username=?", staff.getStr("Username"));
        redirect("/staff");
    }

    public void add(){
        setAttr("method", "save");
        render("edit.html");
    }

    public void save(){
        int maxid;
        Staff staff = getModel(Staff.class, "");
        String max = Db.queryStr("select MAX(Sid) from Staff");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String sid = String.format("%06d", maxid);
        staff.set("Sid", sid).set("Username", null).save();
        redirect("/staff");
    }
}
