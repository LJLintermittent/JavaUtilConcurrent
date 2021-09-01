package com.duanxu.jdk8.functionalInterface;

/**
 * Description:
 * date: 2021/9/1 23:36
 * Package: com.duanxu.jdk8.functionalInterface
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class FunctionalInterfaceTest {
    public static void main(String[] args) {
        /*
           不管是抽象类还是接口，都可以通过匿名内部类的方式来实现，不能通过抽象类或者接口来直接创建对象，
           对于这种实现的理解：我们创建了一个对象，这个对象它实现了这个接口并且重写了接口的抽象方法，然后将这个对象返回
           然后使用这个接口的引用来指向这个对象
           函数式接口：
           函数式接口的概念是指仅仅包含一个抽象方法，但是可以有多个非抽象方法，也就是默认实现方法
           像这样的接口，可以被隐式的转换为lambda表达式，但是需要注意，这里说到函数式接口只能有一个抽象方法，观察compartor接口我发现
           有两个抽象方法，一个是compare，一个是equals方法。为什么这里没有满足规定，是因为，还有一条规定，说如果定义的抽象方法是
           超类object的public方法，那么在这个函数式接口中不被计算入抽象方法的个数中

         */
    }
}
