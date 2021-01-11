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

import java.util.concurrent.TimeUnit;

/**
 * Pipe的抽象实现类。 该类会调用其子类实现的doProcess方法对输入元素进行处理。并将相应的输出作为 下一个Pipe实例的输入。
 *
 * @param <IN>  输入类型
 * @param <OUT> 输出类型
 * @author Viscent Huang
 */
public abstract class AbstractPipe<IN, OUT> implements Pipe<IN, OUT> {
    protected volatile Pipe<?, ?> nextPipe = null;
    protected volatile PipeContext pipeCtx;
    protected String name;

    public AbstractPipe(String name) {
        this.name = name;
    }

    @Override
    public void init(PipeContext pipeCtx) {
        this.pipeCtx = pipeCtx;

    }

    @Override
    public void setNextPipe(Pipe<?, ?> nextPipe) {
        this.nextPipe = nextPipe;

    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        // 什么也不做
    }

    public String getName() {
        return name;
    }
}