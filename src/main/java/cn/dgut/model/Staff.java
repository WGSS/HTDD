package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Staff extends Model<Staff> {
    public static final Staff staff = new Staff();
    public static final Staff dao = new Staff().dao();
}
