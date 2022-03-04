import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class GuessingGame {
    public String serverId;
    public AudioManager manager;
    public String song;
    public Server server;
    public int counter;
    public GuessingGame(AudioManager manager, String serverId, MessageReceivedEvent event, Server server){
        this.serverId = serverId;
        this.manager = manager;
        this.server = server;
        counter = 0;
        song = ListOfSongs.getSong();
        System.out.println("here");
    }
    public void playMusic (MessageReceivedEvent event) {
        server.handleStopMusic("", event);
        server.handlePlayMusic(Server.command+"p "+song, event, true);
    }
    public Boolean verifyGuess(String msg, MessageReceivedEvent event){
        counter++;
        if (msg.toLowerCase().equals(song)) {
            event.getChannel().sendMessage("<@"+event.getAuthor().getId()+"> Got it").queue();
            if (counter == 1) {
                event.getChannel().sendMessage("Correct after " + counter + " guess!").queue();
            } else {
                event.getChannel().sendMessage("Correct after " + counter + " guesses!").queue();
            }
            server.handleStopMusic(msg, event);
            return true;
        } else {
            event.getChannel().sendMessage("Wrong!").queue();
            return false;
        }
    }
}