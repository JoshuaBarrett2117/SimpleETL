package business.dlwz.graph;

import com.code.common.dao.model.DomainElement;
import com.code.common.utils.MD5;

import java.util.UUID;

/**
 * @author liufei
 * @Description
 * @Date 2020/4/23 10:14
 */
public class IdUtil {

    public static String calcEdgeId(DomainElement e) {
        return MD5.md5(e.get("in_id").toString() + e.get("out_id") + e.get("type"));
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
