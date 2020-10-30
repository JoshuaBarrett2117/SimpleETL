/*
授权声明：
本源码系《Java多线程编程实战指南（设计模式篇）第2版》一书（ISBN：978-7-121-38245-1，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtp
http://www.broadview.com.cn/38245
*/

package com.code.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可停止的抽象线程。
 * <p>
 * 模式角色：Two-phaseTermination.AbstractTerminatableThread
 *
 * @author Viscent Huang
 */
public abstract class AbstractTerminatableThread extends Thread
        implements Terminatable {
    final static Logger logger = LoggerFactory.getLogger(AbstractTerminatableThread.class);
    private final boolean DEBUG = true;

    // 模式角色：Two-phaseTermination.TerminationToken
    public final TerminationToken terminationToken;

    public AbstractTerminatableThread() {
        this(new TerminationToken());
    }

    /**
     * @param terminationToken 线程间共享的线程终止标志实例
     */
    public AbstractTerminatableThread(TerminationToken terminationToken) {
        this.terminationToken = terminationToken;
        terminationToken.register(this);
    }

    /**
     * @param terminationToken 线程间共享的线程终止标志实例
     */
    public AbstractTerminatableThread(String name, TerminationToken terminationToken) {
        super(name);
        this.terminationToken = terminationToken;
        terminationToken.register(this);
    }

    /**
     * 留给子类实现其线程处理逻辑。
     *
     * @throws Exception
     */
    protected abstract void doRun() throws Exception;

    /**
     * 留给子类实现。用于实现线程停止后的一些清理动作。
     *
     * @param cause
     */
    protected void doCleanup(Exception cause) {
        // 什么也不做
    }

    /**
     * 留给子类实现。用于执行线程停止所需的操作。
     */
    protected void doTerminiate() {
        // 什么也不做
    }

    @Override
    public void run() {
        Exception ex = null;
        try {
            for (; ; ) {
                // 在执行线程的处理逻辑前先判断线程停止的标志。
                if (terminationToken.isToShutdown() && terminationToken.reservations.get() <= 0) {
                    break;
                }
                doRun();
            }

        } catch (Exception e) {
            // 使得线程能够响应interrupt调用而退出
            ex = e;
            if (e instanceof InterruptedException) {
                logger.info(String.format("[%s]线程被中断", getName()));
            } else {
                logger.error("", e);
            }
        } finally {
            try {
                doCleanup(ex);
            } finally {
                //关掉同组的其他线程，会触发一次terminate，所以terminate方法可能会被执行多次
                terminationToken.notifyThreadTermination(this);
            }
            logger.info(String.format("[%s]线程结束", getName()));
        }
    }

    @Override
    public void interrupt() {
        terminate();
    }

    /*
     * 请求停止线程。
     *
     * @see io.github.viscent.mtpattern.tpt.Terminatable#terminate()
     */
    @Override
    public void terminate() {
        terminationToken.setToShutdown(true);
        try {
            doTerminiate();
        } finally {
            // 若无待处理的任务，则试图强制终止线程
            if (terminationToken.reservations.get() <= 0) {
                logger.info("[{}]线程无待处理的任务，强制中断线程", getName());
                super.interrupt();
            }
        }
    }

}