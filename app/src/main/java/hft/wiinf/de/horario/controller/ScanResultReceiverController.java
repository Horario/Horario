package hft.wiinf.de.horario.controller;

/**
 * This interface checks if there is a positive scan result and returns corresponding variables to
 * the QRScanFragment class
 */
public interface ScanResultReceiverController {

    public void scanResultData(String codeFormat, String codeContent);

    public void scanResultData(NoScanResultExceptionController noScanData);
}
