package com.duanxu.vm.classloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Description:
 * date: 2021/9/1 12:42
 * Package: com.duanxu.vm.classloader
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class MyClassLoader extends ClassLoader {

    private String classpath;

    public MyClassLoader(String classpath) {
        this.classpath = classpath;
    }

    /**
     * 不破坏双亲委派机制，如果想自定义类加载器，那么重写findclass方法就ok了
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = new byte[0];
        String originName = name;
        FileInputStream inputStream = null;
        try {
            name = name.replaceAll("\\.", "/");
            String url = classpath + "/" + name + ".class";
            System.out.println(url);
            inputStream = new FileInputStream(url);
            int len = inputStream.available();
            data = new byte[len];
            inputStream.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //讲一个字节数组转换为class对象
        return defineClass(originName, data, 0, data.length);
    }
}
