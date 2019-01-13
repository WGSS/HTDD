package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;


public class WarehouseController extends Controller {

    public void index(){
        List<Record> warelist = Db.find("select * from Warehouse");
        setAttr("warehouse", warelist);
        render("warehouse.html");
    }

    public void edit(){
        String wid = getPara();
        Record warehouse = Db.findById("Warehouse", "Wid", wid);
        if(warehouse!=null) {
            setAttr("warehouse", warehouse);
            render("edit.html");
        }
        else {
            renderError(404);
        }
    }

    public void update(){
        String wid = getPara("wid");
        String wname = getPara("wname");
        String wlocation = getPara("wlocation");
        String wpeople = getPara("wpeople");
        String wphone = getPara("wphone");
        Record warehouse = new Record().set("Wid", wid).set("Wname", wname).set("Wlocation", wlocation).set("Wpeople", wpeople).set("Wphone", wphone);
        Db.update("Warehouse", "Wid", warehouse);
        redirect("/warehouse");
    }

    @Before(Tx.class)
    public void delete(){
        String wid = getPara();
        Record warehouse = Db.findById("Warehouse", "Wid", wid);
        if(warehouse!=null){
            Db.update("update Product set Wid=NULL where Wid=?", wid);
            Db.delete("Warehouse", "Wid",warehouse);
            redirect("/warehouse");
        }
        else {
            renderError(404);
        }
    }

    public void add(){
        int maxid;
        String max = Db.queryStr("select max(Wid) from Warehouse");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String wid = String.format("%03d", maxid);
        String wname = getPara("wname");
        String wlocation = getPara("wlocation");
        String wpeople = getPara("wpeople");
        String wphone = getPara("wphone");
        Record warehouse = new Record().set("Wid", wid).set("Wname", wname).set("Wlocation", wlocation).set("Wpeople", wpeople).set("Wphone", wphone);
        Db.save("Warehouse", "Wid", warehouse);
        redirect("/warehouse");
    }
}
