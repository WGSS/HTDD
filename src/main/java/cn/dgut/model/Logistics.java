package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Logistics extends Model<Logistics> {
    public static final Logistics logistics = new Logistics();
    public static final Logistics dao = new Logistics().dao();
}
