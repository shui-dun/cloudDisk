import com.google.gson.Gson;

public class RespBean {
    private Integer code;

    private String msg;

    public RespBean(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespBean(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}