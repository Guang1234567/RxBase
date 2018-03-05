package com.gg.rxbase.thread;

import android.os.Looper;

import java.util.concurrent.Executor;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Guang1234567
 * @date 2017/7/14 13:53
 */

public class RxSchedulers {

    public static Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    public static Scheduler io() {
        return Schedulers.io();
    }

    public static Scheduler single() {
        return Schedulers.single();
    }

    public static Scheduler newThread() {
        return Schedulers.newThread();
    }

    public static Scheduler computation() {
        return Schedulers.computation();
    }

    public static Scheduler trampoline() {
        return Schedulers.trampoline();
    }

    public static Scheduler from(Looper looper) {
        return AndroidSchedulers.from(looper);
    }

    public static Scheduler from(Executor executor) {
        return Schedulers.from(executor);
    }
}
