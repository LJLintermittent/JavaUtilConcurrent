package com.duanxu.jdk8.interfacedefault;

/**
 * Description:
 * date: 2021/9/1 23:05
 * Package: com.duanxu.jdk8.interfacedefault
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public interface Login {

    void login(String username, String password);

    default void sout() {
        System.out.println("接口默认方法实现");
    }

}
