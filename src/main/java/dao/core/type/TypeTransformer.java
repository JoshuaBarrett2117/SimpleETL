package dao.core.type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author liufei
 * @date 2019/5/29 14:56
 */
public class TypeTransformer {

//    public static void main(String[] args) {
//        transform("s");
//    }

    public static Object transform(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> aClass = value.getClass();
        String name = aClass.getName();
        try {
            if ("oracle.sql.TIMESTAMP".equals(name)) {
                Class oracleTimeStamp = value.getClass();
                Method getDateValue = oracleTimeStamp.getMethod("timestampValue");
                return getDateValue.invoke(value);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return value;
    }
}
