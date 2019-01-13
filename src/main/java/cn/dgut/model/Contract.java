package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Contract extends Model<Contract> {
    public static final Contract contract = new Contract();
    public static final Contract dao = new Contract().dao();
}
