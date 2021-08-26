package com.duanxu.string;

import java.util.Arrays;

/**
 * Description:
 * date: 2021/8/26 20:42
 * Package: com.duanxu.string
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class TestString {

    public static String appendStr(String s) {
        s += "abc";
        return s;
    }

    public static StringBuilder appendSb(StringBuilder sb) {
        sb.append("abc");
        return sb;
    }

    public static void main(String[] args) {
        String s1 = new String("LJL1");
        String str = TestString.appendStr(s1);

        StringBuilder stringBuilder = new StringBuilder("LJL2");
        StringBuilder appendSb = TestString.appendSb(stringBuilder);
        System.out.println(s1);
        System.out.println(stringBuilder);

        final int[] nums = {1, 2};
        nums[1] = 100;
        System.out.println(Arrays.toString(nums));
    }

}
