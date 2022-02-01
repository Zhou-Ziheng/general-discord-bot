
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.opencsv.CSVWriter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter{
    static JDA jda; //global variable jda
    static HashMap<String, Integer> ServerMap = new HashMap<>();//gets the index of a certain element aka the row number in files from id
    static ArrayList<Server> ServerArrayList = new ArrayList<>();//An array list of all the servers
    static HashMap<Integer, String> ServerMapReverse = new HashMap<>();//Get's the id from the server's index position
    static String fileName = "serverList.csv";//The file that stores the list of servers and names
    static CSVReader in;//CSV file reader declaration
    static int count = 0;//Keeps a count everytime a server added
    static boolean importServerList = false;//Allows the program to import data when executed
    static Connection conn;

    public static void main(String[] Args) throws LoginException, IOException, CsvValidationException {
        //String jdbcUrl = "jdbc:sqlite:/C:\\Users\\Sparky Fnay\\Desktop\\DisBot - Copy\\servers.db";

        //try {
        //    conn = DriverManager.getConnection(jdbcUrl);
        //} catch (SQLException e) {
        //    e.printStackTrace();
        //}

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

       /* if (!importServerList){//if server is not imported, import server
            try {
                inputInformationFromDataCSV(event);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }*/
        if (event.getAuthor().isBot()) {//if the message received is a bot, then return;
            return;
        }

       // String sql = "CREATE TABLE IF NOT EXISTS "+event.getGuild().getId()+" (id varchar(20),gayness varchar(3),racistness varchar(3),swearcount varchar(3),gender varchar(100), penissize  varchar(100) )";

        //Statement statement = null;
        //try {
        //    statement = conn.createStatement();
        //    statement.executeQuery(sql);
        //} catch (SQLException throwables) {
        //    throwables.printStackTrace();
        //}
        if (!ServerMap.containsKey(event.getGuild().getId())){
            try {
                newServer(event);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvValidationException e) {
                e.printStackTrace();
            }
        }
         
        getServer(event).onMessageReceived(event);//Forwards the message to the correct server obj to be processed

    }

    public static Server getServer(MessageReceivedEvent event){
        return ServerArrayList.get(ServerMap.get(event.getGuild().getId()));//Returns a server obj from guild id
    }





    public static void newServer(MessageReceivedEvent event) throws IOException, CsvValidationException {
        ServerMap.put(event.getGuild().getId(), count);

        ServerMapReverse.put(count, event.getGuild().getId());

        ServerArrayList.add(new Server(event.getGuild().getId(), jda));


        count++;
    }


}
