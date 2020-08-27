package dao.jdbc.operator;

/**
 * @author liufei
 * @date 2019/5/14 10:42
 */
public class ColumnMeta {
    /**
     * 字段类型
     */
    private int columnType;
    /**
     * 字段索引
     */
    private int columnIndex;
    /**
     * 字段名
     */
    private String columnName;
    /**
     * 字段类型名称
     */
    private String columnTypeName;

    public ColumnMeta(int columnIndex, int columnType, String columnName, String columnTypeName) {
        super();
        this.columnType = columnType;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.columnTypeName = columnTypeName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }
}