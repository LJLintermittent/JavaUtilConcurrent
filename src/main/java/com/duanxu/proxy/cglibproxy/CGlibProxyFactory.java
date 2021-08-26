package com.duanxu.proxy.cglibproxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * Description:
 * date: 2021/8/26 23:51
 * Package: com.duanxu.proxy.cglibproxy
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class CGlibProxyFactory {

    public static Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader());
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CGlibProxy());
        return enhancer.create();
    }

}
