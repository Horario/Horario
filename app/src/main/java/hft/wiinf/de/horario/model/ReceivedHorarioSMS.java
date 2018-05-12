package hft.wiinf.de.horario.model;

public class ReceivedHorarioSMS {

    String phonenumber;
    boolean acceptance;
    int creatorEventId;
    String excuse;
    String name;

    public ReceivedHorarioSMS (String phonenumber, boolean acceptance, int creatorEventId, String excuse, String name){
        this.phonenumber = phonenumber;
        this.acceptance = acceptance;
        this.creatorEventId = creatorEventId;
        this.excuse = excuse;
        this.name = name;

    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public boolean isAcceptance() {
        return acceptance;
    }

    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }

    public int getCreatorEventId() {
        return creatorEventId;
    }

    public void setCreatorEventId(int creatorEventId) {
        this.creatorEventId = creatorEventId;
    }

    public String getExcuse() {
        return excuse;
    }

    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
