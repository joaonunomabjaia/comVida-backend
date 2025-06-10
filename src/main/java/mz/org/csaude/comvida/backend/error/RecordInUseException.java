package mz.org.csaude.comvida.backend.error;

public class RecordInUseException extends RuntimeException {

    public RecordInUseException() {
        super();
    }

    public RecordInUseException(String message) {
        super(message);
    }

    public RecordInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
