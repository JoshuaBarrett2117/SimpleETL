package com.code.tooltrans.business.address.mapping;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.common.AbstractMain;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.IIteratorTranslator;
import com.code.tooltrans.common.source.elasticsearch.ElasticsearchSource;
import com.code.tooltrans.common.source.excel.ExcelFileSource;
import com.code.tooltrans.common.source.excel.ExcelFileSourceBuilder;
import com.code.tooltrans.common.target.text.TextFileTarget;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class AddressMappingFromStandardizationMain extends AbstractMain {
    public static void main(String[] args) {
        AddressMappingFromStandardizationMain excelToEsMain = new AddressMappingFromStandardizationMain();
        Properties properties = new Properties();
        properties.setProperty("esIp", "192.168.120.192");
        properties.setProperty("esPort", "9800");
        AddressMappingFromStandardizationMain.properties = properties;
        excelToEsMain.deal(null, null);
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        ExcelFileSource address = ExcelFileSourceBuilder
                .anExcelFileSource("C:\\Users\\joshua\\Desktop\\地址映射\\高德天津地理数据标准化.xlsx")
                .isFirstRowsAreColumns(true)
                .sheetIndices(Arrays.asList(1))
                .columnNames(Arrays.asList("STANDARD"))
                .build();
        return address;
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new IIteratorTranslator() {
                    @Override
                    public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
                        IDataSource esSource = new ElasticsearchSource("192.168.125.5", "9400");
                        return new QueryEsIterator(iterator, esSource);
                    }
                }
        );
    }

    class QueryEsIterator implements Iterator<DataRowModel> {
        private Iterator<DataRowModel> iterator;
        private IDataSource esSource;

        public QueryEsIterator(Iterator<DataRowModel> iterator, IDataSource esSource) {
            this.iterator = iterator;
            this.esSource = esSource;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DataRowModel next() {
            DataRowModel next = iterator.next();
            Object address = next.get("STANDARD");
            if (address == null) {
                return null;
            } else {
                String s = address.toString();
                IDataSource.Exp sql = new IDataSource.Exp(" {\n" +
                        "  \"query\": {\n" +
                        "    \"multi_match\": {\n" +
                        "      \"query\": \"" + s + "\",\n" +
                        "      \"fields\": [\n" +
                        "        \"shimc^3\",\n" +
                        "        \"qxmc^2\",\n" +
                        "        \"bzdm\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
                sql.addTableName("t_rh_wz_dx_dtdlwz_di");
                DataRowModel dataRowModel = esSource.queryForObject(sql);
                if (dataRowModel != null) {
                    dataRowModel.addProperties("text", s + "," + dataRowModel.get("bzdm") );
                }
                return dataRowModel;
            }
        }
    }


    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\映射结果.csv", "text");
    }
}
