package com.code.tooltrans.common;


import com.code.common.dao.core.model.DomainElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:02
 */
public interface IDataSource {
    Iterator<DomainElement> iterator(Exp sql);

    DomainElement queryForObject(Exp sql);

    class Exp {
        public Exp(String exp) {
            this.exp = exp;
        }

        private String exp;
        private List<String> tableNames;

        public String getExp() {
            return exp;
        }

        public void setExp(String exp) {
            this.exp = exp;
        }

        public List<String> getTableNames() {
            return tableNames;
        }

        public void setTableNames(List<String> tableNames) {
            this.tableNames = tableNames;
        }

        public void addTableName(String tableName) {
            if (tableNames == null) {
                tableNames = new ArrayList<>();
            }
            tableNames.add(tableName);
        }
    }
}
