import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

class Dictionary{
    public static ArrayList<String> getDefinition(String word) throws IOException, InterruptedException {
        String url = "https://api.urbandictionary.com/v0/define?term="+word.replaceAll(" ", "%20");

        HttpResponse<String> response = Requests.sendGetRequest(url);
        return(parse(response.body()));
    }

    public static ArrayList<String> parse(String responseBody){
        JSONObject obj = new JSONObject(responseBody);
        JSONArray definitionArray = obj.getJSONArray("list");
        ArrayList<String> defs = new ArrayList<>();
        try {
            for (int i = 0; i < 3; i++) {
                JSONObject entry = definitionArray.getJSONObject(i);
                String definition = entry.getString("definition");
                definition = definition.replaceAll("\\[", "").replaceAll("\\]", "");
                defs.add(definition);
            }
        }catch(Exception e){
        }
        return defs;
    }
}