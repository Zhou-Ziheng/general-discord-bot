import java.util.ArrayList;
import java.util.Arrays;
public class GayChecker {
    private static ArrayList<String> gayWords = new ArrayList<>(Arrays.asList(
            "gay", "lesbian", "rainbow", "homo", "bisexual", "pansexual"));

    public static boolean checkForGayness(String str){
        int counter = 0;
        for (int j = 0 ; j < gayWords.size(); j++)
            if (str.toLowerCase().contains(gayWords.get(j))){
                return true;
            }
        return false;
    }
}
