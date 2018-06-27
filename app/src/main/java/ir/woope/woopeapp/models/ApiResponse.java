package ir.woope.woopeapp.models;

/**
 * Created by Hamed on 6/10/2018.
 */

public class ApiResponse<T> {

    private String error;
    private T message;

    public ApiResponse() {

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
