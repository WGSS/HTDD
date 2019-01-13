package cn.dgut.model;

import com.jfinal.plugin.activerecord.Model;

public class Product extends Model<Product> {
    public static final Product product = new Product();
    public static final Product dao = new Product().dao();
}
