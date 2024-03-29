
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.net.HttpURLConnection;
import java.net.URI;

import java.io.*;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter{
    static String URLAddress = "http://143.244.203.14:8000/";
    //static String URLAddress = "http://127.0.0.1:8000/";

    static JDA jda;
    static HashMap<String, Server> ServerMap = new HashMap<>();
    static boolean importServerList = false; //Forces the program to import data when executed

    public static void main(String[] args) throws LoginException {
        String token = args[0];
        jda = JDABuilder.createDefault(token)
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
                .enableIntents(GatewayIntent.GUILD_MEMBERS).build();//Builds JDA
        jda.getPresence().setStatus(OnlineStatus.ONLINE);//Sets the bot to online
        jda.getPresence().setActivity(Activity.playing("Try !hangman"));//Current status


        jda.addEventListener(new Main());//Adds a event listener


    }
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!importServerList) {//if server is not imported, import server
            inputInformationFromDataCSV();
            importServerList = true;
        }

        if (event.getAuthor().isBot()) {//if the message received is a bot, then return;
            return;
        }

        if (!ServerMap.containsKey(event.getGuild().getId())) {//if this is a new server
                newServer(event); //add the server
        }
        getServer(event).onMessageReceived(event);//Forwards the message to the correct server obj to be processed

    }

    public static Server getServer(MessageReceivedEvent event){
        return ServerMap.get(event.getGuild().getId());//Returns a server obj from guild id
    }


    public static void inputInformationFromDataCSV() {
        //reads the data from an .csv file when the program is executed
        String responseBody = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URLAddress+"login/servers/"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();

        }catch(Exception e){
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(responseBody);
        JSONArray array = obj.getJSONArray("data");

        for (int i = 0; i < array.length(); i++) {
            JSONObject entry = array.getJSONObject(i);
            String id = entry.getString("server_id");
            String name = entry.getString("Name");
            try {
                ServerMap.put(id, new Server(id, jda));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        importServerList = true;
    }

    public static void newServer(MessageReceivedEvent event){
        String payload = "{\"server_id\":\""+ event.getGuild().getId()+"\",\"Name\":\""+event.getGuild().getName()+"\"}";
        String requestUrl = URLAddress + "login/servers/";
        System.out.println(payload);
        Requests.sendPostRequest(requestUrl, payload);

        ServerMap.put(event.getGuild().getId(), new Server(event.getGuild().getId(), jda));


    }

}
