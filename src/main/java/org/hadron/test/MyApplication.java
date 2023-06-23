package org.hadron.test;

import org.hadron.springframwork.Component;
import org.hadron.springframwork.ComponentScan;
import org.hadron.springframwork.HadronApplicationContext;
import org.hadron.springframwork.Lazy;
import org.hadron.springframwork.Scope;
import org.hadron.test.service.UserService;

/**
 * @author ZhuZhiQiang
 * @Date 2023/6/24
 **/
@ComponentScan("org.hadron.test")
public class MyApplication {


    public static void main(String[] args) {

        HadronApplicationContext hadronApplicationContext = new HadronApplicationContext(MyApplication.class);

        UserService userService = (UserService) hadronApplicationContext.getBean("userService");
        userService.test();

    }
}
