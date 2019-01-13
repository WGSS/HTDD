package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class CpController extends Controller{

    public void index(){
        render("cp.html");
    }

    @Before(Tx.class)
    public void next(){
        String cid = getPara("Cid");
        String sid = getPara("Sid");
        String cototal = getPara("Cototal");
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String time = ldt.format(format);
        int maxid;
        String max = Db.queryStr("select max(Coid) from Contract");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String coid = String.format("%09d", maxid);
        Record contract = new Record().set("Coid",coid).set("Cotime", time).set("Cototal",cototal).set("Cstatus", "未审核").set("Cid",cid).set("Sid",sid);
        Db.save("Contract", "Coid", contract);
        Record cp = new Record().set("Coid", coid);
        HashMap<String,Integer> cart = getSessionAttr("cart");
        cart.forEach((k, v)->{
            cp.set("Pid", k);
            cp.set("Num", v);
            Db.save("CP", "Coid,Pid", cp);
            Record p = Db.findById("product","Pid",k);
            Integer n = p.getInt("Pnumber");
            p.set("Pnumber",n-v);
            Db.update("product", "Pid", p);
        });
        setSessionAttr("cart", null);
        render("ok.html");
    }

    public void showlist(){
        List<Record> productlist = Db.find("select Product.*,Wname,Classname,Suppliername from Product,Class,Warehouse,Supplier " +
                "where Product.Classid=Class.Classid and Product.Wid=Warehouse.Wid and Product.Supplierid=Supplier.Supplierid");
        renderJson(productlist);
    }

    public void add(){
        HashMap<String,Integer> cplist = null;
        cplist = getSessionAttr("cart");
        if(cplist==null){
            cplist = new HashMap<String, Integer>();
            cplist.put(getPara("Pid"), getParaToInt("quantity"));
        }else{
            cplist.put(getPara("Pid"), getParaToInt("quantity"));
        }
        setSessionAttr("cart", cplist);
        renderJson();
    }

    public void cart(){
        HashMap<String,Integer> cplist = getSessionAttr("cart");
        if(cplist==null || cplist.isEmpty())
            render("empty.html");
        else {
            List<Record> cusomer = Db.find("select Cid,Cname from Customer");
            List<Record> staff = Db.find("select Sid,Sname from Staff");
            setAttr("customer", cusomer);
            setAttr("staff",staff);
            setAttr("cart", cplist);
            render("cart.html");
        }
    }

    public void minus(){
        String pid = getPara();
        HashMap<String,Integer> cart = getSessionAttr("cart");
        if(cart.get(pid)>=2)
            cart.put(pid, cart.get(pid)-1);
        setSessionAttr("cart", cart);
        redirect("/cp/cart");
    }

    public void addition(){
        String pid = getPara();
        HashMap<String,Integer> cart = getSessionAttr("cart");
        cart.put(pid, cart.get(pid)+1);
        setSessionAttr("cart", cart);
        redirect("/cp/cart");
    }

    public void remove(){
        String pid = getPara();
        HashMap<String,Integer> cart = getSessionAttr("cart");
        cart.remove(pid);
        setSessionAttr("cart", cart);
        redirect("/cp/cart");
    }

    public void clear(){
        setSessionAttr("cart", null);
        redirect("/cp");
    }
}
