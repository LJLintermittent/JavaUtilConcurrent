package com.duanxu.collection;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Description:
 * date: 2021/9/1 15:49
 * Package: com.duanxu.collection
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class CodeCollection {

    public static void main(String[] args) throws CloneNotSupportedException {
        //相比于hashmap，treeMap主要多了对集合内元素根据键来排序的能力，以及对集合内元素进行搜索的能力
        TreeMap<String, Integer> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return 0;
            }
        });
        //PriorityQueue默认是小顶堆，底层使用数组这种数据结构来构建一个二叉堆
        PriorityQueue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        queue.offer(1);
        queue.offer(2);
        queue.offer(11);
        queue.offer(99);
        System.out.println(queue);
        HashSet<Integer> set = new HashSet<>();
        //hashset在添加元素的时候会先计算值的hashcode来确定在数组中放置的位置，同时也会与其他对象的hashcode值
        //来进行对比，如果没有相等的hashcode，那就假设没有重复的值出现，如果hashcode相等了，就会调用了equlas方法来判断
        //是否真的相等，如果两者相等，那么就不会添加成功，add方法会返回false，但是其实hashset不管元素是否重复都会添加进入
        //这个add方法返回值代表的含义的是否有重复元素，为true表示没有重复元素，为false表示有重复元素
        //如果hashcode相等了，但是equlas方法判断不相等，说明不存在这个元素，但是发生了哈希碰撞，由于底层实现就是hashmap
        //所以解决哈希冲突的方式跟hashmap一样
        boolean add = set.add(1);
        set.add(2);
        set.add(1);
        System.out.println(set);
    }
}
