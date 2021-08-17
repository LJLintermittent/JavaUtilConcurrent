package com.duanxu.thread;

/**
 * Description:
 * date: 2021/8/17 22:12
 * Package: com.duanxu.thread
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
public class User {

    private String name;

    private Integer age;

    public User() {

    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

