package cn.edu.sdjzu.campussecondhandtradingsystem.common;

public class Result<T> {

    private int code;

    private String message;

    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static Result<Void> success() {
        Result<Void> result = new Result<>();
        result.code = 200;
        result.message = "success";
        return result;
    }

    public static <T> Result<T> fail(String message) {
        return fail(400, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
