package hft.wiinf.de.horario.controller;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hft.wiinf.de.horario.model.Event;
import hft.wiinf.de.horario.model.Invitation;
import hft.wiinf.de.horario.model.Person;

public class InvitationController {
    public static void saveInvitation(@NonNull Invitation invitation){
        if (invitation.getInvitation() != null){
            invitation.save();
        }
    }
    public static void deleteInvitation(@NonNull Invitation invitation){
        invitation.delete();
    }

    public static List<Invitation> getAllInvitations(){
        return new Select().from(Invitation.class).orderBy("dateReceived").execute();
    }
    public static boolean alreadyInvited(@NonNull Invitation invitation){
        List<Invitation> invitations = new Select().from(Invitation.class).where("invitation = ?", invitation.getInvitation()).execute();
        return invitations.size() > 0;
    }
    public static int getNumberOfInvitations(){
        deleteExpiredInvitations();
        return new Select().from(Invitation.class).count();
    }

    public static boolean eventAlreadySaved(@NonNull Invitation invitation){
        List<Person> creator = new Select().from(Person.class).where("phoneNumber = ?", invitation.getCreatorPhoneNumber()).execute();
        if(creator.size() != 0) {
            List<Event> events = new Select().from(Event.class).where("creator = ?", creator.get(0).getId()).and("creatorEventId = ?", invitation.getCreatorEventId()).execute();
            return events.size() > 0;
        }
        return false;
    }

    public static Invitation getInvitationById(String id){
        List<Model> list = new Select().from(Invitation.class).where("id = ?", id).execute();
        return list.size() > 0 ? (Invitation) list.get(0) : null;
    }

    public static void deleteExpiredInvitations(){
        List<Invitation> invitations = getAllInvitations();
        Date now = new Date();
        for(Invitation invitation : invitations){
            String dateString = invitation.getStartTime() + " " + invitation.getStartDate();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            try {
                Date eventDate = format.parse(dateString);
                if(now.after(eventDate)){
                    invitation.delete();
                }
            }catch(ParseException e){
                e.printStackTrace();
            }
        }
    }
}
