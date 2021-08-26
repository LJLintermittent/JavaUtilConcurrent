package com.duanxu.proxy.staticproxy;

/**
 * Description:
 * date: 2021/8/26 23:33
 * Package: com.duanxu.proxy.staticproxy
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class StaticProxy implements SendMsmService {

    private final SendMsmService sendMsmService;

    public StaticProxy(SendMsmService sendMsmService) {
        this.sendMsmService = sendMsmService;
    }

    @Override
    public void send(String content) {
        System.out.println("send方法的前置处理");
        sendMsmService.send(content);
        System.out.println("send方法的后置处理");
    }
}
