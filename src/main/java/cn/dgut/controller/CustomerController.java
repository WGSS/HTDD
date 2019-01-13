package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.dgut.model.Customer;

import java.util.List;

public class CustomerController extends Controller {

    public void index(){
        render("customer.html");
    }

    public void showlist(){
        List<Record> customerlist = Db.find("select * from Customer");
        renderJson(customerlist);
    }
    public void edit(){
        setAttr("method", "update");
        String cid = getPara();
        Record customer = Db.findById("Customer", "Cid", cid);
        setAttr("customer", customer);
        render("edit.html");
    }

    public void update(){
        Customer customer = getModel(Customer.class, "");
        customer.update();
        redirect("/customer");
    }

    @Before(Tx.class)
    public void delete(){
        String cid = getPara();
        Record customer = Db.findById("Customer", "Cid", cid);
        Db.update("update Contract set Cid=NULL where Cid=?", cid);
        Db.deleteById("Customer", "Cid", cid);
        Db.update("delete from Login where Username=?", customer.getStr("Username"));
        redirect("/customer");
    }

    public void add(){
        setAttr("method", "save");
        render("edit.html");
    }

    public void save(){
        int maxid;
        Customer customer = getModel(Customer.class, "");
        String max = Db.queryStr("select MAX(Cid) from Customer");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String cid = String.format("%06d", maxid);
        customer.set("Cid", cid).set("Username", null).save();
        redirect("/customer");
    }
}
