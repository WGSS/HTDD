package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Customer extends Model<Customer> {
    public static final Customer customer = new Customer();
    public static final Customer dao = new Customer().dao();
}
