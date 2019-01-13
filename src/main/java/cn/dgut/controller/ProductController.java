package cn.dgut.controller;

import cn.dgut.model.Product;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;

public class ProductController extends Controller {

    public void index(){
        render("product.html");
    }

    public void showlist(){
        List<Record> productlist = Db.find("select Product.*,Suppliername,Classname,Wname from Product,Supplier,Class,Warehouse where " +
                "Product.Supplierid=Supplier.Supplierid and Product.Classid=Class.Classid and Product.Wid=Warehouse.Wid");
        renderJson(productlist);
    }

    public void edit(){
        setAttr("method", "update");
        String pid = getPara();
        Record product = Db.findById("Product", "Pid", pid);
        List<Record> warehouselist = Db.find("select * from Warehouse");
        List<Record> classselist = Db.find("select * from Class");
        List<Record> supplierlist = Db.find("select * from Supplier");
        setAttr("supplier", supplierlist);
        setAttr("class", classselist);
        setAttr("warehouse",warehouselist);
        setAttr("product", product);
        render("edit.html");
    }

    public void update(){
        Product product = getModel(Product.class, "");
        product.update();
        redirect("/product");
    }

    @Before(Tx.class)
    public void delete(){
        String pid = getPara();
        Record product = Db.findById("Product", "Pid", pid);
        Db.update("delete from CP where Pid=?", pid);
        Db.update("delete from CPL where Pid=?", pid);
        Db.deleteById("Product", "Pid", pid);
        redirect("/product");
    }

    public void add(){
        List<Record> warehouselist = Db.find("select * from Warehouse");
        List<Record> classselist = Db.find("select * from Class");
        List<Record> supplierlist = Db.find("select * from Supplier");
        setAttr("supplier", supplierlist);
        setAttr("class", classselist);
        setAttr("warehouse",warehouselist);
        setAttr("method", "save");
        render("edit.html");
    }

    public void save(){
        int maxid;
        Product product = getModel(Product.class, "");
        String max = Db.queryStr("select MAX(Pid) from Product");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String pid = String.format("%09d", maxid);
        product.set("Pid", pid).save();
        redirect("/product");
    }
}
