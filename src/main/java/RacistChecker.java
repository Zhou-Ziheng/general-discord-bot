import java.util.ArrayList;
import java.util.Arrays;

public class RacistChecker {
    private static ArrayList<String> racistWords = new ArrayList<>(Arrays.asList(
            "abc", "nig", "ching chong", "chingchong", "jap","laowai"));

    public static boolean checkForRacism(String str){
        int counter = 0;
        for (int j = 0 ; j < racistWords.size(); j++)
            if (str.toLowerCase().contains(racistWords.get(j))){
                return true;
            }
        return false;
    }
}
