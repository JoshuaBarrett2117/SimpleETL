package common;

import com.code.common.dao.model.DomainElement;

import java.util.List;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/26 10:08
 */
public interface IDataTarget {
    boolean save(List<DomainElement> docs, String indexName);
    boolean saveOrUpdate(List<DomainElement> docs, String indexName);
}
