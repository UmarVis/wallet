package app.wallet.exceptions;

import org.springframework.http.converter.HttpMessageNotReadableException;

public class BadRequest extends HttpMessageNotReadableException {
    public BadRequest(String message) {
        super(message);
    }
}
