package hft.wiinf.de.horario.model;

/**
 * The type Received horario sms.
 */
public class ReceivedHorarioSMS {

    /**
     * The Phonenumber.
     */
    String phonenumber;
    /**
     * The Acceptance.
     */
    boolean acceptance;
    /**
     * The Creator event id.
     */
    int creatorEventId;
    /**
     * The Excuse.
     */
    String excuse;
    /**
     * The Name.
     */
    String name;

    /**
     * Instantiates a new Received horario sms.
     *
     * @param phonenumber    the phonenumber
     * @param acceptance     the acceptance
     * @param creatorEventId the creator event id
     * @param excuse         the excuse
     * @param name           the name
     */
    public ReceivedHorarioSMS(String phonenumber, boolean acceptance, int creatorEventId, String excuse, String name) {
        this.phonenumber = phonenumber;
        this.acceptance = acceptance;
        this.creatorEventId = creatorEventId;
        this.excuse = excuse;
        this.name = name;

    }

    /**
     * Instantiates a new Received horario sms.
     */
    public ReceivedHorarioSMS() {
    }

    /**
     * Gets phonenumber.
     *
     * @return the phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * Sets phonenumber.
     *
     * @param phonenumber the phonenumber
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * Is acceptance boolean.
     *
     * @return the boolean
     */
    public boolean isAcceptance() {
        return acceptance;
    }

    /**
     * Sets acceptance.
     *
     * @param acceptance the acceptance
     */
    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }

    /**
     * Gets creator event id.
     *
     * @return the creator event id
     */
    public int getCreatorEventId() {
        return creatorEventId;
    }

    /**
     * Sets creator event id.
     *
     * @param creatorEventId the creator event id
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
     * Sets excuse.
     *
     * @param excuse the excuse
     */
    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
