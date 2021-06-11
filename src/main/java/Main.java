
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

    public static void main(String[] Args) throws LoginException, IOException, CsvValidationException {
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
            } catch (CsvValidationException | IOException e) {
                e.printStackTrace();
            }
        }
        if (event.getAuthor().isBot()) {//if the message received is a bot, then return;
            return;
        }
        if (!ServerMap.containsKey(event.getGuild().getId())){//checks of the server of the message is already in the list
            try {
                newServer(event);//if not, then add the server
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
        getServer(event).onMessageReceived(event);//Forwards the message to the correct server obj to be processed

    }

    public static Server getServer(MessageReceivedEvent event){
        return ServerArrayList.get(ServerMap.get(event.getGuild().getId()));//Returns a server obj from guild id
    }


    public static void inputInformationFromDataCSV(MessageReceivedEvent event) throws CsvValidationException, IOException {
        //reads the data from an .csv file when the program is executed
        in = new CSVReader(new FileReader(fileName));//opens the csv file
        String [] data = in.readNext();//reads a line and parse into a String array by ','
        while (data!=null){//if the line contains data
            //add data to lists, arrays, and maps
            //count is the index of each server and it matches between the three lists
            ServerMap.put(data[0], count);
            ServerMapReverse.put(count, data[0]);
            ServerArrayList.add(new Server(data[0], jda));//instantiate a new server obj using the id
            data = in.readNext();
            count++;
        }
        in.close();
        importServerList = true;
    }


    public static void newServer(MessageReceivedEvent event) throws IOException, CsvValidationException {
        ServerMap.put(event.getGuild().getId(), count);

        ServerMapReverse.put(count, event.getGuild().getId());

        ServerArrayList.add(new Server(event.getGuild().getId(), jda));

        String csv = fileName;
        CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

        String [] record = (event.getGuild().getId()+","+event.getGuild().getName()).split(",");

        writer.writeNext(record);

        writer.close();
        count++;
    }


}
