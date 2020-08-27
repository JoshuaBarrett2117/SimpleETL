package common.source.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析大数据量Excel工具类
 *
 * @author RobinTime
 */
public class ExcelParser {
    private static final Logger logger = LoggerFactory.getLogger(ExcelParser.class);
    /**
     * 表格默认处理器
     */
    private ISheetContentHandler contentHandler = new DefaultSheetHandler();
    /**
     * 读取数据
     */
    private List<String[]> datas = new ArrayList<>();

    /**
     * 转换表格,默认为转换第一个表格
     *
     * @param stream
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     * @throws ParseException
     */
    public ExcelParser parse(InputStream stream)
            throws InvalidFormatException, IOException, ParseException {
        return parse(stream, 1);
    }


    /**
     * @param stream
     * @param sheetId:为要遍历的sheet索引，从1开始
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     * @throws ParseException
     */
    public synchronized ExcelParser parse(InputStream stream, int sheetId)
            throws InvalidFormatException, IOException, ParseException {
        // 每次转换前都清空数据
        datas.clear();
        // 打开表格文件输入流
        OPCPackage pkg = OPCPackage.open(stream);
        try {
            // 创建表阅读器
            XSSFReader reader;
            try {
                reader = new XSSFReader(pkg);
            } catch (OpenXML4JException e) {
                logger.error("读取表格出错");
                throw new ParseException(e.fillInStackTrace());
            }

            // 转换指定单元表
            InputStream shellStream = reader.getSheet("rId" + sheetId);
            try {
                InputSource sheetSource = new InputSource(shellStream);
                StylesTable styles = reader.getStylesTable();
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
                // 设置读取出的数据
                getContentHandler().init(datas);
                // 获取转换器
                XMLReader parser = getSheetParser(styles, strings);
                parser.parse(sheetSource);
            } catch (SAXException e) {
                logger.error("读取表格出错");
                throw new ParseException(e.fillInStackTrace());
            } finally {
                shellStream.close();
            }
        } finally {
            pkg.close();

        }
        return this;

    }

    /**
     * 获取表格读取数据,获取数据前，需要先转换数据<br>
     * 此方法不会获取第一行数据
     *
     * @return 表格读取数据
     */
    public List<String[]> getDatas() {
        return getDatas(true);
    }

    /**
     * 获取表格读取数据,获取数据前，需要先转换数据
     *
     * @param dropFirstRow 删除第一行表头记录
     * @return 表格读取数据
     */
    public List<String[]> getDatas(boolean dropFirstRow) {
        if (dropFirstRow && datas.size() > 0) {
            // 删除表头
            datas.remove(0);
        }
        return datas;

    }

    /**
     * 获取读取表格的转换器
     *
     * @return 读取表格的转换器
     * @throws SAXException SAX错误
     */
    protected XMLReader getSheetParser(StylesTable styles, ReadOnlySharedStringsTable strings) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, getContentHandler(), false));
        return parser;
    }

    public ISheetContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ISheetContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    /**
     * 表格转换错误
     */
    public class ParseException extends Exception {
        private static final long serialVersionUID = -2451526411018517607L;

        public ParseException(Throwable t) {
            super("表格转换错误", t);
        }

    }

    public interface ISheetContentHandler extends SheetContentsHandler {

        /**
         * 设置转换后的数据集，用于存放转换结果
         *
         * @param datas 转换结果
         */
        void init(List<String[]> datas);
    }

    /**
     * 默认表格解析handder
     */
    class DefaultSheetHandler implements ISheetContentHandler {
        /**
         * 读取数据
         */
        private List<String[]> datas;
        private int columsLength;
        // 读取行信息
        private String[] readRow;
        private ArrayList<String> firstRow = new ArrayList<String>();

        @Override
        public void init(List<String[]> datas) {
            this.datas = datas;
        }

        @Override
        public void startRow(int rowNum) {
            if (rowNum != 0) {
                readRow = new String[columsLength];
            }
        }

        @Override
        public void endRow(int rowNum) {
            //将Excel第一行表头的列数当做数组的长度，要保证后续的行的列数不能超过这个长度，这是个约定。
            if (rowNum == 0) {
                columsLength = firstRow.size();
                readRow = firstRow.toArray(new String[firstRow.size()]);
            } else {
                readRow = firstRow.toArray(new String[columsLength]);
            }
            datas.add(readRow.clone());
            readRow = null;
            firstRow.clear();
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            int index = getCellIndex(cellReference);//转换A1,B1,C1等表格位置为真实索引位置
            try {
                firstRow.set(index, formattedValue);
            } catch (IndexOutOfBoundsException e) {
                int size = firstRow.size();
                for (int i = index - size + 1; i > 0; i--) {
                    firstRow.add(null);
                }
                firstRow.set(index, formattedValue);
            }
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
        }

        /**
         * 转换表格引用为列编号
         *
         * @param cellReference 列引用
         * @return 表格列位置，从0开始算
         */
        public int getCellIndex(String cellReference) {
            String ref = cellReference.replaceAll("\\d+", "");
            int num = 0;
            int result = 0;
            for (int i = 0; i < ref.length(); i++) {
                char ch = cellReference.charAt(ref.length() - i - 1);
                num = (int) (ch - 'A' + 1);
                num *= Math.pow(26, i);
                result += num;
            }
            return result - 1;
        }
    }

}