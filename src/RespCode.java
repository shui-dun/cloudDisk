public class RespCode {
    public static int SUCCESS = 0;

    public static int NOT_LOGIN = 1;

    public static int UNRESOLVED_REQUEST = 2;

    public static int N_PARAMETER_ERROR = 3;

    public static int USER_NAME_ERROR = 4;

    public static int BAIDU_ERROR = 5;

    public static int NOT_MATCH = 6;

    public static int IO_EXCEPTION = 7;

    public static String resp(int code) {
        return String.format("{\"error_code\":%d}", code);
    }
}
