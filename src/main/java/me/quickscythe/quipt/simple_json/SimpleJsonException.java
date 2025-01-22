package me.quickscythe.quipt.simple_json;

public class SimpleJsonException extends RuntimeException {

    private static final long serialVersionUID = 0;

    public SimpleJsonException(final String message) {
        super(message);
    }

    public SimpleJsonException(final String message, final Throwable cause) {
        super(message, cause);
    }


    public SimpleJsonException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

}
