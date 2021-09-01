package com.duanxu.jdk8.stream;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * date: 2021/9/2 0:00
 * Package: com.duanxu.jdk8.stream
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class StreamTest {

    public static void main(String[] args) {

        /*
          stream可以利用上一组集合的操作结果，stream操作分为中间操作和最终结果操作，最终结果操作是为了返回一组特定的计算结果
          而中间操作返回stream本身，这样子就可以把多个操作串起来，同时stream的创建需要指定一个数据源，collection接口下的实现类可以
          作为数据源，Map接口下的是无法创建stream的
         */
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.stream().filter(num -> {
            return num > 0;
        }).forEach(System.out::println);
        Map<String, Integer> map = new HashMap<>(16);
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        list.stream().map((x) -> {
            return x + 1;
        }).forEach(System.out::println);
    }

}
