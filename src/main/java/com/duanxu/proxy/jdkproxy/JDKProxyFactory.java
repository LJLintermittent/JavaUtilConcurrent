package com.duanxu.proxy.jdkproxy;

import java.lang.reflect.Proxy;

/**
 * Description:
 * date: 2021/8/26 23:01
 * Package: com.duanxu.proxy.jdkproxy
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class JDKProxyFactory {

    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new UserServiceInvocationHandler(target));
    }
}
