
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

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
   // static String URLAddress = "https://thisisamazingdamn.herokuapp.com/";
    static String URLAddress = "http://127.0.0.1:8000/";
    static JDA jda; //global variable jda
    static HashMap<String, Server> ServerMap = new HashMap<>();//gets the index of a certain element aka the row number in files from id
    static int count = 0;//Keeps a count everytime a server added
    static boolean importServerList = false;//Allows the program to import data when executed

    public static void main(String[] Args) throws LoginException{
        jda = JDABuilder.createDefault("Nzk5MTA4MjM2MzI1MTU4OTQy.X_-xig.gO6R0Ph6kieh7WAIkhI9I14i_EQ")
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL) // ignored if chunking enabled
                .enableIntents(GatewayIntent.GUILD_MEMBERS).build();//Builds JDA
        jda.getPresence().setStatus(OnlineStatus.ONLINE);//Sets the bot to online
        jda.getPresence().setActivity(Activity.playing("Try !hangman"));//Current status


        jda.addEventListener(new Main());//Adds a event listener


    }
    /*0: UserID
      1: gayness
      2: racistness
      3: swearcount
      4: gender*/
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!importServerList){//if server is not imported, import server

            try {
                inputInformationFromDataCSV(event);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (event.getAuthor().isBot()) {//if the message received is a bot, then return;
            return;
        }
        if (!ServerMap.containsKey(event.getGuild().getId())){//checks of the server of the message is already in the list
            try {
                newServer(event);//if not, then add the server
                System.out.println("HGAEFADAWDAWDAWD");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            getServer(event).onMessageReceived(event);//Forwards the message to the correct server obj to be processed
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void getasdr(){
        System.out.println("Treov");
    }

    public static Server getServer(MessageReceivedEvent event){
        return ServerMap.get(event.getGuild().getId());//Returns a server obj from guild id
    }


    public static void inputInformationFromDataCSV(MessageReceivedEvent event) throws IOException, InterruptedException {
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
                System.out.println("here");
                String requestUrl=URLAddress+"login/servers/delete/"+id+"/";
                Requests.sendDeleteRequest(requestUrl);
            }
        }
        importServerList = true;
    }


    public static void newServer(MessageReceivedEvent event) throws IOException, InterruptedException {
        String payload="{\"server_id\":\""+ event.getGuild().getId()+"\",\"Name\":\""+event.getGuild().getName()+"\"}";
        String requestUrl=URLAddress+"login/servers/";
        Requests.sendPostRequest(requestUrl, payload);

        ServerMap.put(event.getGuild().getId(), new Server(event.getGuild().getId(), jda));


    }

}
