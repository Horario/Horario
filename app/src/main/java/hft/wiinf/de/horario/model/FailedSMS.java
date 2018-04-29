package hft.wiinf.de.horario.model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "FailedSMS")
public class FailedSMS extends Model {

    @Column(name = "message")
    String message = "";
    @Column(name = "phoneNo")
    String phoneNo = "";
    @Column(name = "creatorID")
    int creatorID;
    @Column(name = "accepted")
    boolean accepted;

    public FailedSMS() {
    }

    public FailedSMS(String message, String phoneNo, int creatorID, boolean accepted) {
        this.message = message;
        this.phoneNo = phoneNo;
        this.creatorID = creatorID;
        this.accepted = accepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
