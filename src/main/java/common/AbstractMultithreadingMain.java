package common;

import business.algorithm.dlwz.FJSDLWZDataSourceMain;
import  dao.core.model.DomainElement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 9:43
 */
public abstract class AbstractMultithreadingMain {
    private int threadCount = 10;

    public AbstractMultithreadingMain(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * 执行算法的线程池
     */
    private static final ThreadPoolExecutor pool =
            new ThreadPoolExecutor(5, 10,
                    1000, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(20),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.AbortPolicy());

    private BlockingQueue<DomainElement> queue = new ArrayBlockingQueue<>(5000);

    private IIteratorTranslator nullDealTranser = new IIteratorTranslator() {
        @Override
        public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
            return new Iterator<DomainElement>() {
                boolean isOut = true;
                DomainElement next;

                @Override
                public boolean hasNext() {
                    if (!isOut) {
                        return true;
                    }
                    while (iterator.hasNext()) {
                        next = iterator.next();
                        if (next == null) {
                            continue;
                        } else {
                            isOut = false;
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public DomainElement next() {
                    isOut = true;
                    return next;
                }
            };
        }
    };


    public void deal(IDataSource.Exp sourceSql, String targetTableName) {
        Properties properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    FJSDLWZDataSourceMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IDataSource source = getDataSource(properties);

        Iterator<DomainElement> iterator = source.iterator(sourceSql);

        for (int i = 0; i < threadCount; i++) {
            exec(targetTableName, properties, iterator);
        }


        while (iterator.hasNext()) {
            try {
                queue.put(iterator.next());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void exec(String targetTableName, Properties properties, Iterator<DomainElement> iterator) {
        pool.execute(() -> {
            Iterator<DomainElement> iter = new Iterator<DomainElement>() {
                boolean isOut = true;
                DomainElement take;

                @Override
                public boolean hasNext() {
                    if (!isOut) {
                        return true;
                    }
                    try {
                        take = queue.poll(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return take != null;
                }

                @Override
                public DomainElement next() {
                    return take;
                }
            };
            //输出
            IDataTarget target = getDataTarget(properties);
            //转换
            List<IIteratorTranslator> transers = getTransers();

            if (transers != null) {
                for (IIteratorTranslator transer : transers) {
                    Iterator<DomainElement> nullIterator = nullDealTranser.transIterator(iter);
                    iter = transer.transIterator(nullIterator);
                }
            }
            //开始写入
            save(target, iter, targetTableName);

        });
    }

    protected abstract IDataSource getDataSource(Properties properties);

    protected List<IIteratorTranslator> getTransers() {
        return null;
    }

    protected abstract IDataTarget getDataTarget(Properties properties);

    private static void save(IDataTarget target, Iterator<DomainElement> iter, String tableName) {
        int count = 0;
        long start = System.currentTimeMillis();
        List<DomainElement> docs = new ArrayList();
        while (iter.hasNext()) {
            DomainElement next = iter.next();
            //删除数据时用
            if (next == null) {
                continue;
            }
            docs.add(next);
            if (++count % 5000 == 0) {
                long saveStart = System.currentTimeMillis();
                target.save(docs, tableName);
                long cost = System.currentTimeMillis() - start;
                System.out.println(Thread.currentThread().getName() + "保存速度[" + (5000f / (System.currentTimeMillis() - saveStart) * 1000) + "]条/s");
                System.out.println(Thread.currentThread().getName() + "速度[" + (5000f / cost * 1000) + "]条/s");
                start = System.currentTimeMillis();
                docs = new ArrayList();
            }
        }
        target.save(docs, tableName);
        long cost = System.currentTimeMillis() - start;
        System.out.println(Thread.currentThread().getName() + "结束，共" + count + "条数据");
        System.out.println(Thread.currentThread().getName() + "总耗时[" + cost + "]ms");
        System.out.println(Thread.currentThread().getName() + "总速度[" + ((double) (count % 5000) / cost * 1000) + "]条/s");
    }
}
