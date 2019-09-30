package hft.wiinf.de.horario.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * represents an Invitation the User received via some medium but hasn't responded to yet
 */
@Table(name = "invitations")
public class Invitation extends Model {
    @Column
    private String invitation;

    @Column
    private Date dateReceived;

    public Invitation(String invitation, Date dateReceived) {
        this.invitation = invitation;
        this.dateReceived = dateReceived;
    }
    public Invitation(){
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(@NonNull String invitation) {
        this.invitation = invitation;
    }

    public String getCreatorEventId(){
        return invitation.split(" \\| ")[0];
    }

    public String getStartDate(){
        return invitation.split(" \\| ")[1];
    }

    public String getEndDate(){
        return invitation.split(" \\| ")[2];
    }

    public String getStartTime(){
        return invitation.split(" \\| ")[3];
    }

    public String getEndTime(){
        return invitation.split(" \\| ")[4];
    }

    public String getRepetition(){
        return invitation.split(" \\| ")[5];
    }

    public String getTitle(){
        return invitation.split(" \\| ")[6];
    }

    public String getPlace(){
        return invitation.split(" \\| ")[7];
    }

    public String getDescription(){
        return invitation.split(" \\| ")[8];
    }

    public String getCreatorName(){
        return invitation.split(" \\| ")[9];
    }

    public String getCreatorPhoneNumber(){
        return invitation.split(" \\| ")[10];
    }


}

