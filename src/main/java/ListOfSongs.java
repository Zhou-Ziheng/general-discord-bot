import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
public class ListOfSongs {

    private static String[] songBank= new String[]{"never gonna give you up", "despacito", "all of you", "senorita", "faded",
            "shake it Off",  "one dance",  "thunder",  "counting stars",  "ymca",  "senorita",  "perfect",  "bohemian rhapsody"
    , "the box", "sorry", "levitating", "firework", "hello", "uptown funk", "shape of you", "my heart will go on", "eye of the tiger", "royals",
    "all about that bass", "happy", "chandelier", "cheap thrills", "bad guy", "can't stop the feeling", "sugar", "africa", "viva la vida",
    "radioactive", "someone like you", "story of my life", "what makes you beautiful", "toosie slide", "alexander hamilton", "satisfied", "let it go"
    , "all i want for christmas is you"};

    public static String getSong() {
        Random rand = new Random();
        int num = rand.nextInt(songBank.length);
        return songBank[num];
    }

    public static void randomSong(MessageReceivedEvent event, Server server) {
        server.handlePlayMusic(Server.command+"p "+ListOfSongs.getSong(), event, false);
    }
}
