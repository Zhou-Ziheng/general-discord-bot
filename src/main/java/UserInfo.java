import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.*;
public class UserInfo{
    private User user;
    private int gayness;
    private int racistness;
    private int swearCount;
    private String gender;
    private String[] penisSize = new String[2];

    public UserInfo(User user, int gayness, int racistness, int swearWords, String gender, String[] penisSize){
        this.user = user;
        this.gayness = gayness;
        this.racistness = racistness;
        this.swearCount = swearWords;
        this.gender = gender;
        this.penisSize = penisSize;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getSwearCount() {
        return swearCount;
    }

    public void setSwearCount(int swearCount) {
        this.swearCount = swearCount;
    }

    public int getGayness() {
        return gayness;
    }

    public void setGayness(int gayness) {
        this.gayness = gayness;
    }

    public int getRacistness() {
        return racistness;
    }

    public void setRacistness(int racistness) {
        this.racistness = racistness;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String[] getPenisSize() {
        return penisSize;
    }

    public void setPenisSize(String[] penisSize) {
        this.penisSize = penisSize;
    }

    public String getProfile(){
        return ("<@"+user.getId()+">'s profile\n```User Name: "+user.getName()+"\nGender: "+ gender + "\nGayness: "+gayness+"\nRacistness: "+racistness+"\nSwear Count: "+swearCount+"```\n");
    }
}
