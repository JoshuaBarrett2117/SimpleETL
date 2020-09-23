package com.code.tooltrans.business.address.mapping;

import com.code.common.dao.core.model.DataRowModel;
import com.code.tooltrans.business.address.mapping.standardization.AddressStandardizationTrans;
import com.code.tooltrans.common.IDataSource;
import com.code.tooltrans.common.IDataTarget;
import com.code.tooltrans.common.IIteratorTranslator;
import com.code.tooltrans.common.source.elasticsearch.ElasticsearchSource;
import com.code.tooltrans.common.target.text.TextFileTarget;

import java.util.Iterator;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class AddressMappingFromAllDataMain extends AddressStandardizationTrans {
    public static void main(String[] args) {
        AddressMappingFromAllDataMain addressMappingFromAllDataMain = new AddressMappingFromAllDataMain();
        Properties properties = new Properties();
        properties.setProperty("esIp", "192.168.120.192");
        properties.setProperty("esPort", "9800");
        AddressMappingFromAllDataMain.properties = properties;
        addressMappingFromAllDataMain.deal(null, null);
    }

    @Override
    protected IIteratorTranslator lastStandardizationTrans() {
        return new IIteratorTranslator() {
            @Override
            public Iterator<DataRowModel> transIterator(Iterator<DataRowModel> iterator) {
                IDataSource esSource = new ElasticsearchSource("192.168.125.5", "9400");
                return new QueryEsIterator(iterator, esSource);
            }
        };
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
                        "        \"qxmc^2\",\n" +
                        "        \"bzdm\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
                sql.addTableName("t_rh_wz_dx_dtdlwz_di_hanlp");
                DataRowModel dataRowModel = esSource.queryForObject(sql);
                if (dataRowModel != null) {
                    dataRowModel.addProperties("text", next.get("ID") + "," + next.get("SRC") + "," + dataRowModel.get("bzdm") + "," + next.get("TYPE"));
                }
                return dataRowModel;
            }
        }
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\映射结果，第3版，第2版基础上添加自定义词典.csv", "text");
    }
}
