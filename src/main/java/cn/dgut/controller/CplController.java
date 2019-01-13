package cn.dgut.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import cn.dgut.model.Logistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CplController extends Controller{
    public void index(){
        render("cpl.html");
    }

    public void history(){
        String coid = getPara();
        List<Record> cpllist = Db.find("select * from CPL where Coid=?", coid);
        setAttr("cpllist", cpllist);
        setAttr("coco", coid);
    }

    public void add(){
        String coid = getPara();
        List<Record> cplist = Db.find("select Pid,Num from CP where Coid=?", coid);
        List<Record> cpllist = Db.find("select Pid,Delivernum from CPL where Coid=?", coid);
        HashMap<String, Integer> cpmap = new HashMap<String, Integer>();

        cplist.forEach(cp -> {
            cpmap.put(cp.getStr("Pid"), cp.getInt("Num"));
        });

        if(cpllist!=null) {
            HashMap<String, Integer> cplmap = new HashMap<String, Integer>();
            cpllist.forEach(cpl -> {
                if (cplmap.containsKey(cpl.getStr("Pid"))) {
                    cplmap.put(cpl.getStr("Pid"), cplmap.get(cpl.getStr("Pid")) + cpl.getInt("Delivernum"));
                } else {
                    cplmap.put(cpl.getStr("Pid"), cpl.getInt("Delivernum"));
                }
            });

            if(cpmap.equals(cplmap)){
                render("error.html");
                return;
            }
            cpmap.forEach((k,v) -> {
                if(cplmap.containsKey(k)){
                    cpmap.put(k,v-cplmap.get(k));
                }
            });


            Iterator iter = cpmap.keySet().iterator();
            while(iter.hasNext()) {
                String key = (String) iter.next();
                if (cpmap.get(key) <= 0) {
                    iter.remove();
                    cpmap.remove(key);
                }
            }
        }
        setSessionAttr("ship", cpmap);
        setAttr("coco", coid);
        setAttr("cpmap", cpmap);
    }

    public void mod(){
        String coid = getPara();
        HashMap<String, Integer> cplmap = getSessionAttr("ship");
        cplmap.forEach((v,k)->{
            if(getPara(v)!=null){
                cplmap.put(v, getParaToInt(v));
            }
        });
        setSessionAttr("ship", cplmap);
        redirect("/cpl/next/"+coid);
    }

    public void next(){
        String coid = getPara();
        setAttr("coco", coid);
        render("next.html");
    }

    @Before(Tx.class)
    public void ship(){
        String coid = getPara();
        HashMap<String, Integer> cplmap = getSessionAttr("ship");
        Logistics logistics = getModel(Logistics.class, "");
        int maxid;
        String max = Db.queryStr("select max(Lid) from Logistics");
        try {
            maxid = Integer.valueOf(max) + 1;
        }catch (NumberFormatException e){
            maxid = 1;
        }
        String lid = String.format("%09d", maxid);
        logistics.set("Lid", lid).save();
        Record cpl = new Record().set("Coid", coid).set("Lid", lid);
        cplmap.forEach((k,v) -> {
            cpl.set("Pid", k).set("Delivernum", v);
            Db.save("CPL", "Coid,Pid,Lid", cpl);
        });
        setAttr("ship", null);
        render("ok.html");
    }
}
