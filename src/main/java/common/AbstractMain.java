package common;

import business.dlwz.FJSDLWZDataSourceMain;
import com.code.common.dao.model.DomainElement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 9:43
 */
public abstract class AbstractMain {

    private IIteratorTranser nullDealTranser = new IIteratorTranser() {
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
        //输出
        IDataTarget target = getDataTarget(properties);
        //转换
        List<IIteratorTranser> transers = getTransers();
        if (transers != null) {
            for (IIteratorTranser transer : transers) {
                Iterator<DomainElement> nullIterator = nullDealTranser.transIterator(iterator);
                iterator = transer.transIterator(nullIterator);
            }
        }
        //开始写入
        save(target, iterator, targetTableName);
    }

    protected abstract IDataSource getDataSource(Properties properties);

    protected List<IIteratorTranser> getTransers() {
        return null;
    }

    protected abstract IDataTarget getDataTarget(Properties properties);

    private static void save(IDataTarget target, Iterator<DomainElement> iter, String tableName) {
        long count = 0;
        long allStart = System.currentTimeMillis();
        long start = allStart;
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
                System.out.println("保存速度[" + (5000f / (System.currentTimeMillis() - saveStart) * 1000) + "]条/s");
                System.out.println("速度[" + (5000f / cost * 1000) + "]条/s");
                start = System.currentTimeMillis();
                docs = new ArrayList();
            }
        }
        target.save(docs, tableName);
        long cost = System.currentTimeMillis() - allStart;
        System.out.println("结束，共" + count + "条数据");
        System.out.println("总耗时[" + cost + "]ms");
        System.out.println("总速度[" + ((double) (count % 5000) / cost * 1000) + "]条/s");
    }
}
