package org.hadron.springframwork;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhuZhiQiang
 * @Date 2023/6/24
 **/
public class HadronApplicationContext {

    private Class ConfigClass;
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, Object> singletonObjects = new HashMap<>();

    public HadronApplicationContext(Class configClass) {
        this.ConfigClass = configClass;

        // 扫描包
        scan(configClass);

        // 创建单例非懒加载的bean
        initSingletonNotLazyBean();

        for (String s : singletonObjects.keySet()) {
            System.out.println("已初始化单例Bean:" + s);
        }

    }

    private void initSingletonNotLazyBean() {

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope()) && !beanDefinition.getLazy()) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }

    }


    private void scan(Class configClass) {

        // 1. 获取扫描的包路径
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            // 简易版加载类
            ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String packagePath = annotation.value();
            packagePath = packagePath.replace(".", "/");
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(packagePath);
            File file = new File(resource.getFile());
            List<File> files = new ArrayList<>();
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.isDirectory()) {
                        for (File f1 : f.listFiles()) {
                            if (!f1.isDirectory()) {
                                files.add(f1);
                            }
                        }
                    } else {
                        files.add(f);
                    }
                }
            }

            for (File classFile : files) {
                String absolutePath = classFile.getAbsolutePath();
                String className = absolutePath.substring(absolutePath.indexOf("org"), absolutePath.indexOf(".class")).replace("\\", ".");
                System.out.println(className);
                try {
                    Class<?> aClass = classLoader.loadClass(className);
                    if (aClass.isAnnotationPresent(Component.class)) {
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(aClass);
                        beanDefinition.setLazy(aClass.isAnnotationPresent(Lazy.class));
                        if (aClass.isAnnotationPresent(Scope.class)) {
                            beanDefinition.setScope(aClass.getAnnotation(Scope.class).value());
                        } else {
                            // 默认单例
                            beanDefinition.setScope("singleton");
                        }
                        String beanName = aClass.getAnnotation(Component.class).value();
                        if (beanName.isEmpty()) {
                            beanName = Introspector.decapitalize(aClass.getSimpleName());
                        }
                        System.out.println(beanName + ":" + beanDefinition);
                        beanDefinitionMap.put(beanName, beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new NullPointerException();
        }

        if ("singleton".equals(beanDefinition.getScope())) {
            Object result = singletonObjects.get(beanName);
            if (result == null) {
                result = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, result);
            }

            return result;
        } else {
            return createBean(beanName, beanDefinition);
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {

        Class clazz = beanDefinition.getType();

        try {
            Object o = clazz.newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(field.getName());
                    field.setAccessible(true);
                    field.set(o, bean);
                }
            }

            if (o instanceof BeanNameAware) {
                ((BeanNameAware) o).setBeanName(beanName);
            }

            return o;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return null;
    }

}

