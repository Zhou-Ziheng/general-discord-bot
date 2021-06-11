import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class SwearWordChecker {
    private static ArrayList<String> swearWords = new ArrayList<>(Arrays.asList(
            "fuck", "shit", "nig","cunt","bitch","waste yute","asshole","bastard","pussy"
            , "dickhead", "damn", "fag", "asad", "crap"));

    public static int checkForSwearWord(String str){
        int counter = 0;
        for (int j = 0 ; j < swearWords.size(); j++)
            if (str.toLowerCase().contains(swearWords.get(j))){
                counter++;
            }

        return counter;//returns the number of swear words
    }
}
