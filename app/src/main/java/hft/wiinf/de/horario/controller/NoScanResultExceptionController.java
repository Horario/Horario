package hft.wiinf.de.horario.controller;

/**
 * This exception class is called if the user does not scan any QR code and issues an error message.
 */
public class NoScanResultExceptionController extends Exception {

    public NoScanResultExceptionController() {
    }

    public NoScanResultExceptionController(String msg) {
        super(msg);
    }

    public NoScanResultExceptionController(Throwable cause) {
        super(cause);
    }

    public NoScanResultExceptionController(String msg, Throwable cause) {
        super(msg, cause);
    }
}