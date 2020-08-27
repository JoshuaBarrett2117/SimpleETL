package business.address.mapping;

import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.source.excel.ExcelFileSourceBuilder;
import common.target.ElasticsearchTarget;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author liufei
 * @Description
 * @Date 2020/5/9 14:07
 */
public class ExcelToEsMain extends AbstractMain {
    public static void main(String[] args) {
        ExcelToEsMain excelToEsMain = new ExcelToEsMain();
        Properties properties = new Properties();
        properties.setProperty("esIp", "192.168.120.192");
        properties.setProperty("esPort", "9800");
        ExcelToEsMain.properties = properties;
        excelToEsMain.deal(null, "address_mapping_test");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return ExcelFileSourceBuilder
                .anExcelFileSource("C:\\Users\\joshua\\Desktop\\地址映射\\高德地点数据截取.xlsx")
                .isFirstRowsAreColumns(true)
                .sheetIndices(Arrays.asList(1))
                .columnNames(Arrays.asList("ADDRESS", "DETAIL_ADDRESS", "ID", "PCODE", "PNAME", "CITYCODE", "CITYNAME", "ADCODE", "ADNAME"))
                .build()
                ;
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new ElasticsearchTarget(properties);
//        return new ConsoleTarget();
    }
}
