package com.code.tooltrans.common;


import com.code.common.dao.core.model.DataRowModel;

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
    private IDataTarget target;
    private IDataSource source;

    protected static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    AbstractMain.class.getResourceAsStream("/prop.properties"),
                    Charset.forName("GBK"));
            properties.load(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deal(IDataSource.Exp sourceSql, String targetTableName) {
        Iterator<DataRowModel> iterator = warpTranslator(sourceSql);
        if (target != null) {
            start(target, iterator, targetTableName);
        }
    }

    /**
     * 包装多个处理器形成迭代器管道
     *
     * @param sourceSql 数据源入参表达式
     * @return 最上层迭代器
     */
    public Iterator<DataRowModel> warpTranslator(IDataSource.Exp sourceSql) {
        //构建数据源和目标数据源
        source = buildDataSource(properties);
        target = buildDataTarget(properties);
        Iterator<DataRowModel> iterator = source.iterator(sourceSql);
        //获取转换流程
        List<IIteratorTranslator> translators = getTranslators();
        if (translators != null) {
            for (IIteratorTranslator translator : translators) {
                Iterator<DataRowModel> nullIterator = new NullDealTranslator().transIterator(iterator);
                iterator = translator.transIterator(nullIterator);
            }
        }
        return iterator;
    }

    /**
     * 构建数据源
     *
     * @param properties 配置信息
     * @return
     */
    protected abstract IDataSource buildDataSource(Properties properties);

    /**
     * 构建转换流程，子类需要重写该方法实现添加子流程
     *
     * @return
     */
    protected List<IIteratorTranslator> getTranslators() {
        return null;
    }

    /**
     * 构建目标数据
     *
     * @param properties 配置信息
     * @return
     */
    protected abstract IDataTarget buildDataTarget(Properties properties);

    //执行流程
    private static void start(IDataTarget target, Iterator<DataRowModel> iter, String tableName) {
        long count = 0;
        long allStart = System.currentTimeMillis();
        long start = allStart;
        List<DataRowModel> docs = new ArrayList();
        while (iter.hasNext()) {
            DataRowModel next = iter.next();
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
        System.out.println("正在关闭target");
        target.close();
        System.out.println("结束，共" + count + "条数据");
        System.out.println("总耗时[" + cost + "]ms");
        System.out.println("总速度[" + ((double) (count % 5000) / cost * 1000) + "]条/s");
    }

    public IDataTarget getTarget() {
        return target;
    }

    public IDataSource getSource() {
        return source;
    }

    class NullDealTranslator implements IIteratorTranslator {
        @Override
        public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
            return new Iterator<DataRowModel>() {
                boolean isOut = true;
                DataRowModel next;

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
                public DataRowModel next() {
                    isOut = true;
                    return next;
                }
            };
        }
    }
}
