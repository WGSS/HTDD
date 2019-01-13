package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.dgut.model.Contract;

import java.util.List;

public class ContractController extends Controller{
    public void index(){
        render("contract.html");
    }

    public void showlist(){
        List<Record> contractlist = Db.find("select * from Contract");
        renderJson(contractlist);
    }
    public void edit(){
        setAttr("method", "update");
        List<Record> stafflist = Db.find("select * from Staff");
        setAttr("staff", stafflist);
        String coid = getPara();
        Record contract = Db.findById("Contract", "Coid", coid);
        setAttr("contract", contract);
        render("edit.html");
    }

    public void update(){
        Contract contract = getModel(Contract.class, "");
        contract.update();
        redirect("/contract");
    }

    @Before(Tx.class)
    public void delete(){
        String coid = getPara();
        Record contract = Db.findById("Contract", "Coid", coid);
        Db.update("delete from CP where Coid=?", coid);
        Db.update("delete from CPL where Coid=?", coid);
        Db.deleteById("Contract", "Coid", coid);
        redirect("/contract");
    }

    public void more(){
        String coid = getPara();
        Record contract = Db.findById("Contract", "Coid", coid);
        Record customer = Db.findById("Customer", "Cid", contract.getStr("Cid"));
        Record staff = Db.findById("Staff", "Sid", contract.getStr("Sid"));
        List<Record> cplist = Db.find("select CP.*,Pname,Pprice from CP,Product where CP.Pid=Product.Pid and Coid=?", coid);
        try {
            setAttr("cname", customer.getStr("Cname"));
        }catch (NullPointerException e){
            setAttr("cname", "");
        }
        try {
            setAttr("sname", staff.getStr("Sname"));
        }catch (NullPointerException e){
            setAttr("sname", "");
        }
        setAttr("cp", cplist);
        setAttr("contract", contract);
        render("more.html");
    }
    public void add(){
        setAttr("method", "save");
        render("edit.html");
    }

    public void save(){
        int maxid;
        Contract contract = getModel(Contract.class, "");
        String max = Db.queryStr("select MAX(Coid) from Contract");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String coid = String.format("%09d", maxid);
        contract.set("Coid", coid).set("Cid", null).set("Sid", null).save();
        redirect("/contract");
    }
}
