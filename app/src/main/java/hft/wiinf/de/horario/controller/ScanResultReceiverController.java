package hft.wiinf.de.horario.controller;

public interface ScanResultReceiverController {

    public void scanResultData(String codeFormat, String codeContent);

    public void scanResultData(NoScanResultExceptionController noScanData);
}
