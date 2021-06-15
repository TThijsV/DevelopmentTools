package mv.development.logger;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Logger {

    public enum LogLevel { TRACE, DEBUG, NOTICE, INFO, WARNING, ERROR };
    public static LogLevel activeLogLevel = LogLevel.TRACE;

    static DateTimeFormatter DATE_TIME_FORMATTER_MIN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getTime() {
        LocalDateTime now = LocalDateTime.now();
        return DATE_TIME_FORMATTER.format(now);
    }

    public static String getTimeMin() {
        LocalDateTime now = LocalDateTime.now();
        return DATE_TIME_FORMATTER_MIN.format(now);
    }

    public static void log(LogLevel level, Object ...objs ) {
        String pattern = "\\d+\\.\\d\\d\\d\\d+";
        if (level.ordinal() >= Logger.activeLogLevel.ordinal()) {
            String[] strings = new String[objs.length];
            int counter = 0;
            for (Object obj: objs) {
                String tmp = obj.toString();
                if (tmp.matches(pattern)) {
                    String[] tmps = tmp.split("\\.");
                    tmp = tmps[0] + "." + tmps[1].substring(0, 3);
                }
                strings[counter++] = tmp;
            }

            String line = getTime() + " ";
            for (int i = 0; i< strings.length - 1; ++i) {
                line += strings[i] + " ";
            }
            line += strings[strings.length -1];
            System.out.println(line);
        }
    }

    public static void error(Object ...objs) {
        log(LogLevel.ERROR, objs);
    }
    public static void debug(Object ...objs) {
        log(LogLevel.DEBUG, objs);
    }
    public static void warn(Object ...objs) { log(LogLevel.WARNING, objs); }
    public static void info(Object ...objs) {
        log(LogLevel.INFO, objs);
    }
    public static void notice(Object ...objs) {
        log(LogLevel.NOTICE, objs);
    }
    public static void trace(Object ...objs) {
        log(LogLevel.TRACE, objs);
    }

}

