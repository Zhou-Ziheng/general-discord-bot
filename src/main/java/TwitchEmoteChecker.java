import java.util.ArrayList;
import java.util.Arrays;

public class TwitchEmoteChecker {
    private static ArrayList<String> twitchEmotes = new ArrayList<>(Arrays.asList("pog", "kappa", "4head", "lul", "megalul"
            , "bop", "pogchamp", "pjsalt", "trihard", "gaypride", "blessrng", "hahaa", "forsen5g", "homylol", "<3", "poki",
             "peepo", "pepe", "pogu", "xqc"
            ));

    public static int checkForTwitchEmote(String str){
        int counter = 0;
        for (int j = 0 ; j < twitchEmotes.size(); j++)
            if (str.toLowerCase().contains(twitchEmotes.get(j))){
                counter++;
            }

        return counter;//returns the number of swear words
    }
}
