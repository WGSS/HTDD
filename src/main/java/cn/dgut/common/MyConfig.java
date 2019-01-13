package cn.dgut.common;

import cn.dgut.controller.*;
import cn.dgut.model.*;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import cn.dgut.Interceptor.UserInterceptor;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.template.Engine;


public class MyConfig extends JFinalConfig{
    @Override
    public void configConstant(Constants constants) {
        PropKit.use("a_little_config.txt");
        constants.setDevMode(true);
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add("/", IndexController.class, "/");
        routes.add("/login", LoginController.class, "/");
        routes.add("/warehouse", WarehouseController.class, "/warehouse");
        routes.add("/class", ClassController.class, "/class");
        routes.add("/supplier", SupplierController.class, "/supplier");
        routes.add("/customer", CustomerController.class, "/customer");
        routes.add("/staff", StaffController.class, "/staff");
        routes.add("/contract", ContractController.class, "/contract");
        routes.add("/cp", CpController.class, "/cp");
        routes.add("/cpl", CplController.class, "/cpl");
        routes.add("/product",ProductController.class, "/product");
        routes.add("/logistics", LogisticsController.class, "/logistics");
    }

    @Override
    public void configEngine(Engine engine) {
        engine.addSharedFunction("/common/_basetemp.html");
        engine.addSharedFunction("/common/_navtemp.html");
        engine.addSharedMethod(new com.jfinal.plugin.activerecord.Db());
    }

    @Override
    public void configPlugin(Plugins plugins) {
        DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
        plugins.add(dp);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.setDialect(new SqlServerDialect());
        arp.addMapping("Login", "Username", Login.class);
        arp.addMapping("Customer", "Cid", Customer.class);
        arp.addMapping("Staff", "Sid", Staff.class);
        arp.addMapping("Contract", "Coid", Contract.class);
        arp.addMapping("Logistics", "Lid", Logistics.class);
        arp.addMapping("Product","Pid", Product.class);
        plugins.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.addGlobalActionInterceptor(new UserInterceptor());
        interceptors.add(new SessionInViewInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new ContextPathHandler("ctx")); //虚拟路径ctx确保上下文一致
    }

    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 8080, "/");
    }
}
