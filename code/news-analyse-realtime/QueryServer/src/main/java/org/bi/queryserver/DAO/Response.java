package org.bi.queryserver.DAO;

public class Response<T> {
    private boolean isSuccess;
    private T response;
    private String message;

    public Response() {
    }

    public Response(boolean isSuccess, T response, String message) {
        this.isSuccess = isSuccess;
        this.response = response;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> Response<T> success(T res, String msg) {
        return new Response<>(true, res, msg);
    }

    public static <T> Response<T> fail(String msg) {
        return new Response<>(false, null ,msg);
    }
}
