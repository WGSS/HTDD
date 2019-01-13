package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;

public class ClassController extends Controller {

    public void index() {
        List<Record> classlist = Db.find("select * from Class");
        setAttr("class", classlist);
        render("class.html");
    }

    public void edit() {
        String classid = getPara();
        Record classoj = Db.findById("Class", "Classid", classid);
        if (classoj != null) {
            setAttr("class", classoj);
            render("edit.html");
        } else {
            renderError(404);
        }
    }

    public void update() {
        String classid = getPara("classid");
        String classname = getPara("classname");
        Record classoj = new Record().set("Classid", classid).set("Classname", classname);
        Db.update("Class", "Classid", classoj);
        redirect("/class");
    }

    @Before(Tx.class)
    public void delete() {
        String classid = getPara();
        Record classoj = Db.findById("Class", "Classid", classid);
        if (classoj != null) {
            Db.update("update Product set Classid=NULL where Classid=?", classid);
            Db.delete("Class", "Classid", classoj);
            redirect("/class");
        } else {
            renderError(404);
        }
    }

    public void add(){
        int maxid;
        String max = Db.queryStr("select max(Classid) from Class");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String classid = String.format("%03d", maxid);
        String classname = getPara("classname");
        Record classoj = new Record().set("Classid", classid).set("Classname", classname);
        Db.save("Class", "Classid", classoj);
        redirect("/class");
    }
}
