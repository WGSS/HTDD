package cn.dgut.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class LogisticsController extends Controller {

    public void index(){
        render("logistics.html");
    }

    public void showlist(){
        List<Record> logisticslist = Db.find("select * from Logistics");
        renderJson(logisticslist);
    }
}

