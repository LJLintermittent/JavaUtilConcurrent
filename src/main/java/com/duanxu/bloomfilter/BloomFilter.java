package com.duanxu.bloomfilter;

import java.util.BitSet;

/**
 * Description:
 * date: 2021/9/4 2:23
 * Package: com.duanxu.bloomfilter
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class BloomFilter {

    public static void main(String[] args) {
        String s = "aaa";
        String s1 = "bb";
        BloomFilter bloomFilter = new BloomFilter();
        System.out.println(bloomFilter.contains(s));
        bloomFilter.add(s);
        bloomFilter.add(s1);
        System.out.println(bloomFilter.contains(s));
    }

    private static final int DEFAULT_SIZE = 2 << 24;

    private static final int[] SEEDS = new int[]{3, 13, 46, 71, 91, 134};

    private BitSet set = new BitSet(DEFAULT_SIZE);

    private SimpleHash[] func = new SimpleHash[SEEDS.length];

    public BloomFilter() {
        for (int i = 0; i < SEEDS.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, SEEDS[i]);
        }
    }

    public void add(Object value) {
        for (SimpleHash f : func) {
            set.set(f.hash(value), true);
        }
    }

    public boolean contains(Object value) {
        boolean res = true;
        for (SimpleHash f : func) {
            res = res && set.get(f.hash(value));
        }
        return res;
    }

    public static class SimpleHash {

        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        /**
         * 计算 hash 值
         */
        public int hash(Object value) {
            int h;
            return (value == null) ? 0 : Math.abs(seed * (cap - 1) & ((h = value.hashCode()) ^ (h >>> 16)));
        }

    }

}
