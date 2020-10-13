public enum ErrorCode {
    SUCCESS(0, "成功"),
    NOT_LOGIN(1, "尚未登录"),
    UNRESOLVED_REQUEST(2, "无法解析的请求"),
    N_PARAMETER_ERROR(3, "请求的参数个数错误"),
    USER_NAME_NOT_EXIST(4, "用户名不存在"),
    USER_NAME_OCCUPIED(5, "用户名已被占用"),
    BAIDU_ERROR(6, "百度服务器未响应"),
    BAIDU_ERROR_RET(7, "百度服务器给出了异常的返回"),
    FACE_NOT_MATCH(8, "人脸不匹配"),
    FACE_NOT_EXIST(9, "未检测到人脸"),
    IO_EXCEPTION(10, "服务器发生了IO异常"),
    WEAK_USER_NAME(11, "用户名长度过短"),
    WEAK_PASSWD(12, "密码长度过短"),
    USERNAME_PASSWD_NOT_MATCH(13, "用户名和密码不匹配");

    private Integer code;

    private String msg;

    ErrorCode(Integer code, String mesg) {
        this.code = code;
        this.msg = mesg;

    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}