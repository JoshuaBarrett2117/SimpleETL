package com.code.pipeline.etl;

import com.code.pipeline.core.AbstractTransformerWorker;
import com.code.pipeline.core.PipeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
        timer.schedule(new CalcTimeTask(reservations, System.currentTimeMillis()), 100, 1000);
    }

    public AbstractOutputWorker(String name) {
        super(name);
        dataRowModels = new ArrayList<>(DATA_SIZE);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (dataRowModels.size() > 0) {
            logger.info("[{}]还有剩余数据，数据量[{}]，输出到目标位置", name, dataRowModels.size());
            this.out(dataRowModels);
            reservations.addAndGet(dataRowModels.size());
            dataRowModels = new ArrayList<>();
        }
    }


    @Override
    public Void doRun(IN input) throws PipeException {
        dataRowModels.add(input);
        if (dataRowModels.size() == dataSize) {
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
                long o = reservations.get();
                logger.info("已写入{}条数据，写入速度{}条/s", o, ((double) o) / (System.currentTimeMillis() - start) * 1000f);
            }
        }
    }
}
