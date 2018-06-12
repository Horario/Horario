package hft.wiinf.de.horario.controller;

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