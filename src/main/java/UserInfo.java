import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.*;
public class UserInfo{
    private User user;
    private int messageCount;
    private int interactions;
    private int twitchAddiction;
    private String gender;
    private String pronouns;

    public UserInfo(User user, int messageCount, int interactions, int twitchAddiction, String gender, String pronouns){
        this.user = user;
        this.messageCount = messageCount;
        this.interactions = interactions;
        this.twitchAddiction = twitchAddiction;
        this.gender = gender;
        this.pronouns = pronouns;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getTwitchAddiction() {
        return twitchAddiction;
    }

    public void setTwitchAddiction(int twitchAddiction) {
        this.twitchAddiction = twitchAddiction;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getInteractions() {
        return interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getProfile(){
        return ("<@"+user.getId()+">'s profile\n```User Name: "+user.getName()+"\nGender: "+ gender + "\nTotal Messages: "+messageCount+"\nInterations: "+interactions+"\nTwitch Words: "+twitchAddiction+"```\n");
    }
}
