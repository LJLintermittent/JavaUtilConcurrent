package com.duanxu.async;

import java.util.concurrent.CompletableFuture;

/**
 * Description:
 * date: 2021/8/17 23:42
 * Package: com.duanxu.async
 *
 * @author 李佳乐
 * @email 18066550996@163.com
 */
@SuppressWarnings("all")
public class CompletableFutureTest {

    public static void main(String[] args) {
        CompletableFuture completableFuture = new CompletableFuture();
        //runAsync异步启动一个任务，也就是线程，但是不接受入参，同时也没有返回值
        CompletableFuture<Void> future = completableFuture.runAsync(() -> {
            System.out.println("run");
        });
        //supplyAsync异步启动一个任务，不接受入参，但是有返回值
        CompletableFuture<Integer> future1 = completableFuture.supplyAsync(() -> {
            int i = 0;
            i = i + 10;
            return i;
        });
        //whenCompleteAsync处理异步任务的结果  void accept(T t, U u);可以拿到异常信息，有入参，无出参
        //exceptionally()可以拿到异常信息并进行处理
        future1.whenCompleteAsync((t, u) -> {
            System.out.println("执行结果：" + t + "异常信息：" + u);
        }).exceptionally((t) -> {
            return null;
        });
        // void accept(T t);线程串行化
//        future.thenAcceptAsync();
        //Runnable 上一个任务完成以后，继续下一个任务的执行，使用Runnable，还可以使用自己的线程池Executor，但是无返回值
//        future.thenRunAsync();
        //R apply(T t); 既能拿到上一步的返回结果，自己执行完以后也有返回值
        future.thenApplyAsync(res -> {
            return null;
        });
        //thenCombine：组合两个future，获取两个future的返回结果，并返回当前任务的返回值
//        future.thenCombine()
        //thenAcceptBoth:组合两个future，获取两个future的返回结果，然后处理接下来的任务，没有返回结果
//        future.thenAcceptBoth()
        //runAfterBoth:组合两个future，不需要获取future的结果，只需要两个future处理完以后，再处理该任务
//        future.runAfterBoth()
        //两个任务，只要有一个任务完成，就执行下一个任务
        //runAfterEitherAsync:不感知结果，自己没有返回值
//        future.runAfterEitherAsync()
        //acceptEitherAsync：感知结果，自己没有返回值
//        future.acceptEitherAsync()
        //applyToEitherAsync：感知结果，自己有返回值
//        future.applyToEitherAsync()
        //所有任务都做完了，才继续执行下面的代码
//        future.allOf()
        //只要有一个任务做完了，就往下执行
//        future.anyOf()
    }

}

