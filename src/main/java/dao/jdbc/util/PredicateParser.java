package dao.jdbc.util;


import dao.core.condition.NestedCondition;
import dao.jdbc.operator.Software;
import dao.core.query.Compare;
import dao.core.query.Contains;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * @author liufei
 * @date 2019/3/20 11:04
 */
public class PredicateParser {
    public static void main(String[] args) {
        NestedCondition condition = NestedCondition.build()
                .addParam(
                        NestedCondition.build()
                                .addParam("a", 2).addOperator(NestedCondition.Op.AND)
                                .addParam("b", 2).addOperator(NestedCondition.Op.OR)
                )
                .addParam("c", "2").addOperator(NestedCondition.Op.AND);
        Software software = new Software();
        software.setCode("oracle");
        System.out.println(buildSqlCondition(condition, software));
    }

    public static String buildSqlCondition(NestedCondition condition, Software software) {
        return parseCondition(condition, software);
    }

    public static String parseCondition(NestedCondition condition, Software software) {
        StringBuffer sb = new StringBuffer();
        List<NestedCondition> conditions = condition.getConditions();
        for (int i = 0; i < conditions.size(); i++) {
            NestedCondition cdin = conditions.get(i);
            if (i != 0) {
                switch (cdin.getOp()) {
                    case OR:
                        sb.append(" or ");
                        break;
                    case NOT:
                        sb.append(" not ");
                        break;
                    case AND:
                        sb.append(" and ");
                        break;
                    default:
                        sb.append(" and ");
                        break;
                }
            }
            //有子条件
            if (cdin.getConditions().size() != 0) {
                sb.append("(");
                sb.append(parseCondition(cdin, software));
                sb.append(")");
            } else {
                sb.append(switchPredicate(cdin, software));
            }
        }
        return sb.toString();
    }

    /**
     * 设置比较逻辑
     *
     * @param cdin 条件，携带有比较条件，和键值对
     * @return
     */
    public static String switchPredicate(NestedCondition cdin, Software software) {
        BiPredicate predicate = cdin.getPredicate();
        if (predicate instanceof Compare) {
            return switchCompare(cdin, (Compare) predicate, software);
        } else if (predicate instanceof Contains) {
            return switchContains(cdin, (Contains) predicate);
        } else {
            throw new IllegalArgumentException("逻辑参数非法");
        }
    }

    public static String switchContains(NestedCondition cdin, Contains predicate) {
        switch (predicate) {
            case like:
                return cdin.getKey() + " like '%" + cdin.getValue() + "%'";
            case startWith:
                return cdin.getKey() + " like '" + cdin.getValue() + "%'";
            case endWith:
                return cdin.getKey() + " like '%" + cdin.getValue() + "'";
            default:
                return cdin.getKey() + " = '" + cdin.getValue() + "'";
        }
    }

    public static String switchCompare(NestedCondition cdin, Compare predicate1, Software software) {
        String value = DataParser.parse(cdin.getValue(), software);
        switch (predicate1) {
            case eq:
                return cdin.getKey() + " = " + value;
            case neq:
                return cdin.getKey() + " != " + value;
//            case with:
//                return cdin.getKey() + " != " + value;
            case gte:
                return cdin.getKey() + " >= " + value;
            case lte:
                return cdin.getKey() + " <= " + value;
            case gt:
                return cdin.getKey() + " > " + value;
            case lt:
                return cdin.getKey() + " < " + value;
            default:
                return cdin.getKey() + " = " + value;
        }
    }

}
