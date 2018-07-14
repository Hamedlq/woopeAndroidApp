package ir.woope.woopeapp.models;

import java.util.List;

/**
 * Created by Hamed on 6/10/2018.
 */

public class ApiResponse {

    private List<String> error;
    private String message;

    public ApiResponse() {

    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
