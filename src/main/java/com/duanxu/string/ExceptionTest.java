package com.duanxu.string;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Description:
 * date: 2021/8/26 21:12
 * Package: com.duanxu.string
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ExceptionTest {

    public static void main(String[] args) {

        try (Scanner scanner1 = new Scanner(new File("test.txt"));) {
            while (scanner1.hasNext()) {
                System.out.println(scanner1.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(new File("test.txt"));
            while (scanner2.hasNext()) {
                System.out.println(scanner2.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner2 != null) {
                scanner2.close();
            }
        }

        try {
            FileInputStream sdsd = new FileInputStream("sdsd");
            // 正常退出虚拟机，如果这句代码在异常代码之后执行，那么finally中的代码依然会执行
            System.exit(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.out.println("我是finally代码块中的代码");
        }
    }

}
