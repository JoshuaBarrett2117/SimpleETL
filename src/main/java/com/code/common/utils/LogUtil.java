package com.code.common.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class LogUtil {

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    public static String exceptionMsg(String msg, Throwable e) {
        return msg + ",异常消息[" + getStackTrace(e) + "]";
    }

    public static String exceptionSimpleMsg(String msg, Throwable e) {
        return msg + ",异常消息[" + e.getLocalizedMessage() + "]";
    }

    /**
     * 获取计算程序运行的时钟
     *
     * @return clock
     */
    public static Clock getClock() {
        return new Clock() {
            /**开始时间*/
            private long startTime = System.nanoTime();

            /**
             * 消耗时间[毫秒]
             * @return 消耗时间
             */
            @Override
            public long consume() {
                return LogUtil.consume(startTime, System.nanoTime());
            }

            /**
             * 结束时间
             */
            @Override
            public long getEnd() {
                return System.nanoTime();
            }

            /**
             * 开始时间
             */
            @Override
            public long getStart() {
                return startTime;
            }

            @Override
            public long consumeSecond() {
                return LogUtil.consumeSecond(startTime, System.nanoTime());
            }
        };
    }


    /**
     * 消耗时间[纳秒]
     *
     * @return 消耗时间 秒
     */
    public static long consumeNano(long startTime, long endTime) {
        return (endTime - startTime) / 1000000000 + 1;
    }

    /**
     * 消耗时间[微秒]
     *
     * @return 消耗时间 秒
     */
    public static long consume(long startTime, long endTime) {
        return (endTime - startTime) / 1000000 + 1;
    }

    /**
     * 消耗时间[耗秒]
     *
     * @return 消耗时间 秒
     */
    public static long consumeSecond(long startTime, long endTime) {
        return (endTime - startTime) / 1000 + 1;
    }

    /**
     * getRootCause
     * 获取异常的原始异常，用于反馈异常的真实原因。
     *
     * @param ex 异常信息
     * @return 原始异常，最底层异常
     * @author 林小松
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * 递归寻找根异常异常信息
     *
     * @param ex 异常
     * @return 异常信息
     */
    public static String traceRootExceptionInfo(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.toString();
    }

    /**
     * 递归寻找引起异常的所有异常信息
     *
     * @param ex 异常
     * @return 异常信息
     */
    public static String tarceExceptionInfo(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Throwable cause = ex;
        sb.insert(0, cause);
        while (cause.getCause() != null) {
            cause = cause.getCause();
            sb.insert(0, cause.toString());
        }
        return sb.toString();
    }

    public static interface Clock {


        /**
         * 返回程序消耗的毫秒数
         *
         * @return 消耗毫秒数
         */
        long consume();

        /**
         * 返回程序消耗的秒数
         *
         * @return 消耗秒数
         */
        long consumeSecond();

        /**
         * 返回开始时间
         *
         * @return 开始时间
         * @author:lwei
         */
        long getStart();

        /**
         * 返回结束时间
         *
         * @return 结束时间
         * @author:lwei
         */
        long getEnd();
    }

    public static void main(String[] args) {
        try {
            Assert.fail("dd");
        } catch (Throwable e) {
            System.out.println(exceptionMsg("aa", e));
            ;
        }
    }

}
