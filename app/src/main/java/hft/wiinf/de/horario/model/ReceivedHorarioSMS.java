package hft.wiinf.de.horario.model;

/**
 * a SMS that is for horario
 */
public class ReceivedHorarioSMS {

    /**
     * The Phonenumber of the sms.
     */
    String phonenumber;
    /**
     * if the event is accepted or rejected.
     */
    boolean acceptance;
    /**
     * The event id in the creator's table.
     */
    int creatorEventId;
    /**
     * The Excuse if the event was rejected.
     */
    String excuse;
    /**
     * The Name of the sender.
     */
    String name;

    /**
     * Instantiates a new Received horario sms.
     *
     * @param phonenumber    the phonenumber of the sender. the database does not checks if this number exists or has a right format
     * @param acceptance     if the event was accepted
     * @param creatorEventId the event ID in the creator's database
     * @param excuse         the excuse if the event was rejected
     * @param name           the name of the sender
     */
    public ReceivedHorarioSMS(String phonenumber, boolean acceptance, int creatorEventId, String excuse, String name) {
        this.phonenumber = phonenumber;
        this.acceptance = acceptance;
        this.creatorEventId = creatorEventId;
        this.excuse = excuse;
        this.name = name;

    }

    /**
     * Instantiates a new Received horario sms. ONLY FOR INTERNAL REASONS
     */
    public ReceivedHorarioSMS() {
    }

    /**
     * Gets phonenumber of the sender.
     *
     * @return the phonenumber of the sender. the database does not checks if this number exists or has a right format
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * Sets phonenumber of the sender. the database does not checks if this number exists or has a right format
     *
     * @param phonenumber the phonenumber of the sender
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * if the event was accepted.
     *
     * @return if the event was accepted.
     */
    public boolean isAcceptance() {
        return acceptance;
    }

    /**
     * Sets if the event was accepted.
     *
     * @param acceptance if the event was accepted.
     */
    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }

    /**
     * Gets the id of the event in the creato's table.
     *
     * @return the the id of the event in the creato's table.
     */
    public int getCreatorEventId() {
        return creatorEventId;
    }

    /**
     * Sets the id of the event in the creato's table.
     *
     * @param creatorEventId the the id of the event in the creato's table.
     */
    public void setCreatorEventId(int creatorEventId) {
        this.creatorEventId = creatorEventId;
    }

    /**
     * Gets excuse.
     *
     * @return the excuse
     */
    public String getExcuse() {
        return excuse;
    }

    /**
     * Sets the excuse if the event was rejected.
     *
     * @param excuse the the excuse if the event was rejected.
     */
    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    /**
     * Gets name of the sender.
     *
     * @return the name of the sender
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the sender.
     *
     * @param name the name of the sender
     */
    public void setName(String name) {
        this.name = name;
    }
}
