package com.duanxu.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Description:
 * date: 2021/8/26 21:39
 * Package: com.duanxu.object
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class ScannerTest {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = reader.readLine();
        System.out.println(s + "想去阿里巴巴");

    }


}
