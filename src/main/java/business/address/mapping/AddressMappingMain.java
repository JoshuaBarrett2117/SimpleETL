package business.address.mapping;

import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranslator;
import common.source.elasticsearch.ElasticsearchSource;
import common.source.excel.ExcelFileSource;
import common.source.excel.ExcelFileSourceBuilder;
import common.target.TextFileTarget;
import dao.core.model.DomainElement;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class AddressMappingMain extends AbstractMain {
    public static void main(String[] args) {
        AddressMappingMain excelToEsMain = new AddressMappingMain();
        Properties properties = new Properties();
        properties.setProperty("esIp", "192.168.120.192");
        properties.setProperty("esPort", "9800");
        AddressMappingMain.properties = properties;
        excelToEsMain.deal(null, null);
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        ExcelFileSource address = ExcelFileSourceBuilder
                .anExcelFileSource("C:\\Users\\joshua\\Desktop\\地址映射\\样例数据提取的地址.xlsx")
                .isFirstRowsAreColumns(true)
                .sheetIndices(Arrays.asList(1))
                .columnNames(Arrays.asList("ADDRESS"))
                .build();
        return address;
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(
                new IIteratorTranslator() {
                    @Override
                    public Iterator<DomainElement> transIterator(Iterator<DomainElement> iterator) {
                        IDataSource esSource = new ElasticsearchSource("192.168.120.192", "9800");
                        return new QueryEsIterator(iterator, esSource);
                    }
                }
        );
    }

    class QueryEsIterator implements Iterator<DomainElement> {
        private Iterator<DomainElement> iterator;
        private IDataSource esSource;

        public QueryEsIterator(Iterator<DomainElement> iterator, IDataSource esSource) {
            this.iterator = iterator;
            this.esSource = esSource;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DomainElement next() {
            DomainElement next = iterator.next();
            Object address = next.get("ADDRESS");
            if (address == null) {
                return null;
            } else {
                String s = address.toString();
                IDataSource.Exp sql = new IDataSource.Exp("{\n" +
                        "  \"size\": 1, \n" +
                        "  \"query\": {\n" +
                        "    \"match\": {\n" +
                        "      \"DETAIL_ADDRESS\":{\n" +
                        "        \"query\":  \"" + s + "\",\n" +
                        "        \"minimum_should_match\": 5\n" +
                        "        }\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"_source\": [\"ID\",\"DETAIL_ADDRESS\",\"PNAME\",\"ADNAME\"]\n" +
                        "}");
                sql.addTableName("address_mapping_test");
                DomainElement domainElement = esSource.queryForObject(sql);
                if (domainElement != null) {
                    domainElement.addProperties("text", s +
                            "," + domainElement.get("PNAME") + domainElement.get("ADNAME") +
                            "," + domainElement.get("DETAIL_ADDRESS") + "," + domainElement.get("ID") +
                            "," + domainElement.getScore()
                    );
                }
                return domainElement;
            }
        }
    }


    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget("C:\\Users\\joshua\\Desktop\\地址映射\\映射结果.csv", "text");
    }
}
