package org.hadron.springframwork;

/**
 * @author ZhuZhiQiang
 * @Date 2023/6/24
 **/
public class BeanDefinition {

    private Class type;
    private String scope;
    private Boolean isLazy = false;


    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getLazy() {
        return isLazy;
    }

    public void setLazy(Boolean lazy) {
        isLazy = lazy;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "type=" + type +
                ", scope='" + scope + '\'' +
                ", isLazy=" + isLazy +
                '}';
    }
}
