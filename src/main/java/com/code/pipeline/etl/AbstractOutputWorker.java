package com.code.pipeline.etl;

import com.code.common.dao.core.model.DataRowModel;
import com.code.pipeline.core.AbstractTransformerWorker;
import com.code.pipeline.core.AbstractWorker;
import com.code.pipeline.core.PipeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件名称: AbstractOutputPipe.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2020/10/28     		@author(创建:创建文件)
 * ====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 * @author liufei
 */
public abstract class AbstractOutputWorker<IN> extends AbstractTransformerWorker<IN, Void> implements IOutputWorker<IN> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractOutputWorker.class);
    protected static final int DATA_SIZE = 5000;
    protected int dataSize = DATA_SIZE;
    protected List<IN> dataRowModels;
    public final static AtomicLong reservations = new AtomicLong(0);
    private static final Timer timer = new Timer();

    static {
        timer.schedule(new CalcTimeTask(reservations, System.currentTimeMillis()), 1000, 1000);
    }

    public AbstractOutputWorker(String name) {
        super(name);
        dataRowModels = new ArrayList<>(DATA_SIZE);
    }

    @Override
    public void shutdown(long timeout, TimeUnit unit) {
        super.shutdown(timeout, unit);
        if (dataRowModels.size() > 0) {
            long start = System.currentTimeMillis();
            this.out(dataRowModels);
            reservations.addAndGet(dataSize);
        }
    }


    @Override
    public Void doRun(IN input) throws PipeException {
        dataRowModels.add(input);
        if (dataRowModels.size() > dataSize) {
            this.out(dataRowModels);
            reservations.addAndGet(dataSize);
            dataRowModels = new ArrayList<>(dataSize);
        }
        return null;
    }


    protected abstract void out(List<IN> dataRowModels);

    static class CalcTimeTask extends TimerTask {
        private static final Logger logger = LoggerFactory.getLogger(CalcTimeTask.class);
        final AtomicLong reservations;
        final long start;

        public CalcTimeTask(AtomicLong reservations, long start) {
            this.reservations = reservations;
            this.start = start;
        }

        @Override
        public void run() {
            if (logger.isInfoEnabled()) {
                logger.info("写入速度{}条/s", ((double) reservations.get()) / (System.currentTimeMillis() - start) * 1000f);
            }
        }
    }
}
