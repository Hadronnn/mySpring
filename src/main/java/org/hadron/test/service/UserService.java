package org.hadron.test.service;

import org.hadron.springframwork.Autowired;
import org.hadron.springframwork.BeanNameAware;
import org.hadron.springframwork.Component;

/**
 * @author ZhuZhiQiang
 * @Date 2023/6/24
 **/
@Component
public class UserService implements BeanNameAware {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println("UserService.test");
        orderService.test();
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("BeanNameAware: " + beanName);
    }
}
