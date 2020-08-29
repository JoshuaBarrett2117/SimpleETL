
package utils;

import dao.jdbc.util.LogUtil;

import java.util.Collection;
import java.util.Map;

public abstract class Assert {
    /**
     * 断言表达式是否为true
     *
     * @param expression 要断言的表达式
     * @param message    　抛异常以后的消息
     * @throws IllegalArgumentException 如果不是true
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式是否为true,有默认消息
     *
     * @param expression 要断言的表达式
     * @throws IllegalArgumentException 如果不是true
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[断言失败] - 参数必须是 true");
    }

    /**
     * 断言表达式是否为0
     *
     * @param message 　抛异常以后的消息
     * @throws IllegalArgumentException 如果不是空
     */
    public static void isZero(long num, String message) {
        if (num != 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式是否为空,有默认消息
     *
     * @throws IllegalArgumentException 如果不是空
     */
    public static void isZero(long num) {
        isZero(num, "[断言失败] - 参数值必须是0");
    }

    /**
     * 断言表达式是否为非空
     *
     * @param message 　抛异常以后的消息
     * @throws IllegalArgumentException 如果不是非空
     */
    public static void notZero(long num, String message) {
        if (num == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式是否为非空,有默认消息
     *
     * @throws IllegalArgumentException 如果不是非空
     */
    public static void notZero(long num) {
        notZero(num, "[断言失败] - 参数必须非0");
    }


    /**
     * 断言表达式是否为空
     *
     * @param message 　抛异常以后的消息
     * @throws IllegalArgumentException 如果不是空
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式是否为空,有默认消息
     *
     * @throws IllegalArgumentException 如果不是空
     */
    public static void isNull(Object object) {
        isNull(object, "[断言失败] - 参数必须是 null");
    }


    /**
     * 断言表达式是否为非空
     *
     * @param message 　抛异常以后的消息
     * @throws IllegalArgumentException 如果不是非空
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式是否为非空,有默认消息
     *
     * @throws IllegalArgumentException 如果不是非空
     */
    public static void notNull(Object object) {
        notNull(object, "[断言失败] - 参数必须是非空对象");
    }


    /**
     * 断言表达式是否为非空,有默认消息
     *
     * @throws IllegalArgumentException 如果不是非空
     */
    public static void notNull(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            notNull(objects[i], "[断言失败] - 参数[" + i + "]必须是非空对象");
        }
    }

    /**
     * 断言text是否为有内容字符串
     *
     * @param text    要断言的字符串
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果是空串或null对象
     */
    public static void hasLength(String text, String message) {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言text是否为有内容字符串,有默认消息
     *
     * @param text 要断言的字符串
     * @throws IllegalArgumentException 如果是空串或null对象
     */
    public static void hasLength(String text) {
        hasLength(text,
                "[断言失败] - 参数必须是非空字符串");
    }

    /**
     * 断言text是否为有内容,如果都是空白字符也将抛异常
     *
     * @param text    要断言的字符串
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果是空串或null对象，或所有内容为空白字符
     */
    public static void hasText(String text, String message) {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言text是否为有内容,如果都是空白字符也将抛异常,有默认消息
     *
     * @param text 要断言的字符串
     * @throws IllegalArgumentException 如果是空串或null对象，或所有内容为空白字符
     */
    public static void hasText(String text) {
        hasText(text,
                "[断言失败] - text必须是有内容的,有内容指不是全部为空白字符的字符串");
    }


    /**
     * 断言数组是否为空数组
     *
     * @param array   要断言的数租
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果是空数组或者null
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言数组是否为空数组,有默认消息
     *
     * @param array 要断言的数租
     * @throws IllegalArgumentException 如果是空数组或者null
     */
    public static void notEmpty(Object[] array) {
        notEmpty(array, "[断言失败] - 参数数组必须是非空数组");
    }

    /**
     * 断言集合是否为空集合对象
     *
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果是空集合对象或者null
     */
    public static void notEmpty(Collection collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言集合是否为空集合对象,有默认消息
     *
     * @throws IllegalArgumentException 如果是空集合对象或者null
     */
    public static void notEmpty(Collection collection) {
        notEmpty(collection,
                "[断言失败] - 集合参数必须是有内容的");
    }

    /**
     * 断言Map是否为空对象
     *
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果是空集合对象或者null
     */
    public static void notEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言Map是否为空对象,有默认消息
     *
     * @throws IllegalArgumentException 如果是空集合对象或者null
     */
    public static void notEmpty(Map map) {
        notEmpty(map, "[断言失败] - Map参数必须是有内容的");
    }


    /**
     * 断言指定的 Object 是否与此 type 所表示的对象赋值兼容
     *
     * @param obj 用于检查的对象
     * @throws IllegalArgumentException 如果不兼容
     */
    public static void isInstanceOf(Class clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    /**
     * 断言指定的 Object 是否与此 type 所表示的对象赋值兼容
     *
     * @param obj     用于检查的对象
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果不兼容
     */
    public static void isInstanceOf(Class type, Object obj, String message) {
        notNull(type, "类型对象不可为空");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message +
                    "Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
                    "] must be an instance of " + type);
        }
    }


    /**
     * 断言一个class对象调用newInstance，是否成功
     *
     * @param type
     * @throws IllegalArgumentException 如果不成功
     */
    public static void isNewInstanceOf(Class type) {
        isNewInstanceOf(type, "");
    }

    /**
     * 断言两个类型是否相同
     *
     * @param type1
     * @param type2
     */
    public static void isSameType(Class type1, Class type2, String message) {
        notNull(type1, "类型对象不可为空");
        notNull(type2, "类型对象不可为空");
        if (!type1.equals(type2)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言两个类型是否相同
     *
     * @param type1
     * @param type2
     */
    public static void isSameType(Class type1, Class type2) {
        isSameType(type1, type2, "必须是相同类型");
    }

    /**
     * 断言一个class对象调用newInstance，是否成功
     *
     * @param type
     * @param message 抛异常以后的消息
     * @throws IllegalArgumentException 如果不成功
     */
    public static void isNewInstanceOf(Class type, String message) {
        notNull(type, "类型对象不可为空");
        try {
            type.newInstance();
        } catch (InstantiationException e) {

            throw new IllegalArgumentException(message +
                    "Class [" + type + "] must be can new instance ");
        } catch (IllegalAccessException e) {

            throw new IllegalArgumentException(message +
                    "Class [" + type + "] must be can new instance ");
        }
    }

    /**
     * 断言superType.isAssignableFrom(subType)是否为true
     *
     * @param superType 超类
     * @param subType   子类
     * @throws IllegalArgumentException 如果调用该方法为false
     */
    public static void isAssignable(Class superType, Class subType) {
        isAssignable(superType, subType, "");
    }

    /**
     * 断言superType.isAssignableFrom(subType)是否为true
     *
     * @param superType 超类
     * @param subType   子类
     * @param message   抛异常以后的消息
     * @throws IllegalArgumentException 如果调用该方法为false
     */
    public static void isAssignable(Class superType, Class subType, String message) {
        notNull(superType, "超类的类对象必须为非空");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }


    /**
     * 断言一个对象所属状态值是否为true
     *
     * @param expression 要断言的状态表达式
     * @param message    抛异常以后的消息
     * @throws IllegalStateException 如果表达式为false
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertEquals(Object o1, String o2, String message) {
        if (!o1.equals(o2)) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertEquals(Object o1, String o2) {
        if (!o1.equals(o2)) {
            throw new IllegalStateException("必须是相同对象");
        }
    }


    /**
     * 断言一个对象所属状态值是否为true,有默认消息
     *
     * @param expression 要断言的状态表达式
     * @throws IllegalStateException 如果表达式为false
     */
    public static void state(boolean expression) {
        state(expression, "[断言失败] - 状态表达式必须式true");
    }

    public static void logic(boolean expression, String message) {
        if (!expression) {
            throw new RuntimeException(message);
        }
    }

    public static void logic(boolean expression) {
        logic(expression, "[断言失败] - 逻辑表达式必须是true");
    }

    public static void fail(String message) {
        throw new RuntimeException(message);
    }

    public static void fail(String message, Throwable e) {
        throw new RuntimeException(LogUtil.exceptionMsg(message, e), e);
    }
}
