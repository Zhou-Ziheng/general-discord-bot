import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;

public class Server {
    public JDA jda;
    public HashMap<String, Integer> userInfoMap = new HashMap<>();
    public ArrayList<UserInfo> userInfoArrayList = new ArrayList<>();
    public HashMap<Integer, String> userInfoMapReverse = new HashMap<>();
    public String fileName;
    public CSVReader in;
    public Guild guild;
    public AudioManager manager;
    private int count = 0;
    private String command = "~";
    public Color[] colors = new Color[]{Color.magenta, Color.black, Color.red, Color.pink, Color.cyan, Color.gray, Color.green, Color.lightGray, Color.yellow, Color.orange, Color.white, Color.blue};
    public Server(String id, JDA jda) throws IOException, CsvValidationException {
        fileName = System.getProperty("user.dir")+ File.separator + "ServerData" + File.separator + id+".csv";
        this.jda = jda;
        guild = jda.getGuildById(id);
        manager = guild.getAudioManager();
        System.out.println("``"+guild+"``");
        File file = new File(fileName);
        file.createNewFile();
        inputInformationFromDataCSV();
        List<Member> members = guild.getMembers();
        for (int i = 0; i <members.size(); i ++){
            if (members.get(i).getUser().isBot()) {
                continue;
            }
            if (!userInfoMap.containsKey(members.get(i).getId())){
                try {
                    newUser(members.get(i).getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public HashMap<Member, Hangman> hangmanHashMap = new HashMap<>();
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        if (!userInfoMap.containsKey(event.getAuthor().getId())){
            try {
                newUser(event.getAuthor().getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String msg = event.getMessage().getContentRaw();
        if (msg.toLowerCase().contains(command+"hangman")){
            handleHangman(event);
        }
        if (event.getAuthor().getName().equals("Yoyocube")&&msg.toLowerCase().contains("stupid")&& msg.toLowerCase().contains("bot")){
            event.getChannel().sendMessage("no u").queue();
        }
        if (msg.toLowerCase().contains(command+"penis")){
            handlePenis(event);
        }
        if (msg.contains(command+"define")){
            handleDefinition(msg, event);
        }
        if (msg.toLowerCase().contains (command+"avatar")){
            if (event.getMessage().getMentionedUsers().size()!=0){
                String image = event.getMessage().getMentionedUsers().get(0).getAvatarUrl();
                event.getChannel().sendMessage(image).queue();
            }
            else {
                String image = event.getAuthor().getAvatarUrl();
                event.getChannel().sendMessage(image).queue();
            }
        }
        if (msg.contains(command+"help")){
            event.getChannel().sendMessage("define \nhangman \nracistness \ngayness \nswearcount \ngender \ngender set `String`\n" +
                    //command+"set penis size `String` `String` \n!penis size \n" +
                    "profile").queue();
        }
        if (msg.contains(command+"racistness")||RacistChecker.checkForRacism(msg)){
            handleRacist(msg, event);
        }
        if (msg.contains(command+"gayness")||GayChecker.checkForGayness(msg)){
            System.out.println(234);
            handleGayness(msg, event);
        }
        if (msg.contains(command+"swearcount")||(SwearWordChecker.checkForSwearWord(msg)!=0)){
            handleSwearWord(msg, event);
        }
        if (msg.contains(command+"gender")||msg.contains(command+"gender")){
            handleGender(msg, event);
        }
        if (msg.toLowerCase().contains(command+"penis set")||msg.contains(command+"penis")){
            handlePenisSize(msg, event);
        }
        if (msg.contains(command+"profile")) {
            handleProfile(event);
        }
        //if (msg.contains(command+"")&&(msg.charAt(0)!='!')&&event.getMessage().getMentionedMembers().size()==0){
        //    handleCheer(event);
        //}
        handleEmotes(msg, event);
        if (msg.contains(command+"p ")){
            handlePlayMusic(msg, event);
        }
        if (msg.contains(command+"play")){
            handlePlayMusic(msg, event);
        }
        if (msg.contains(command+"stop")){
            handleStopMusic(msg,event);
        }
        if (msg.equalsIgnoreCase(command+"skip")){
            handleSkipMusic(msg,event);
        }
        if (msg.equalsIgnoreCase(command+"queue")||msg.equalsIgnoreCase(command+"q")){
            handleQueueMusic(msg,event);
        }
        if (msg.equalsIgnoreCase(command+"dc")){
            System.out.println("here");
            handleStopMusic(msg,event);
            this.manager.closeAudioConnection();
        }
        if (msg.equalsIgnoreCase(command+"pause")){
            System.out.println("here");
            handlePauseMusic(msg,event);
        }if (msg.equalsIgnoreCase(command+"start")){
            System.out.println("here");
            handleStartMusic(msg,event);
        }

    }
    public boolean isUrl(String url){
        if (url.contains("/")){
            return true;
        }
        else{
            return false;
        }
    }
    public void handleHangman(MessageReceivedEvent evt){
        Member member = evt.getMember();
        if (!hangmanHashMap.containsKey(member)||evt.getMessage().equals(command+"new game")){
            evt.getChannel().sendMessage("New game created!").queue();
            evt.getChannel().sendMessage("Commands:\n!hangman ` ` - guess a letter\n!hangman `word` - guess a word\n!hangman reveal - reveal answer").queue();
            hangmanHashMap.put(member, new Hangman());
            Hangman game = hangmanHashMap.get(member);
            evt.getChannel().sendMessage(game.getGameState()).queue();
            evt.getChannel().sendMessage("```"+game.getStringToReturn()+"```").queue();
            evt.getChannel().sendMessage("```"+game.getMessageToReturn()+("```")).queue();
        }
        else {
            Hangman game = hangmanHashMap.get(member);
            game.nextMove(evt.getMessage().getContentRaw().toLowerCase());
            evt.getChannel().sendMessage(game.getGameState()).queue();
            evt.getChannel().sendMessage("```"+game.getStringToReturn()+"```").queue();
            evt.getChannel().sendMessage("```"+game.getMessageToReturn()+("```")).queue();
            if (game.getMessageToReturn().toLowerCase().contains("game over")||game.getMessageToReturn().toLowerCase().contains("congrat")){
                hangmanHashMap.remove(member);
            }
        }
    }
    public void handlePenis(MessageReceivedEvent evt){
        evt.getChannel().sendMessage("…………………...„„-~^^~„-„„_\n" +
                "………………„-^*'' : : „'' : : : : *-„\n" +
                "…………..„-* : : :„„--/ : : : : : : : '\\\n" +
                "…………./ : : „-* . .| : : : : : : : : '|\n" +
                "……….../ : „-* . . . | : : : : : : : : |\n" +
                "………...\\„-* . . . . .| : : : : : : : :'|\n" +
                "……….../ . . . . . . '| : : : : : : : :|\n" +
                "……..../ . . . . . . . .'\\ : : : : : : : |\n" +
                "……../ . . . . . . . . . .\\ : : : : : : :|\n" +
                "……./ . . . . . . . . . . . '\\ : : : : : /\n" +
                "….../ . . . . . . . . . . . . . *-„„„„-*'\n" +
                "….'/ . . . . . . . . . . . . . . '|\n" +
                "…/ . . . . . . . ./ . . . . . . .|\n" +
                "../ . . . . . . . .'/ . . . . . . .'|\n" +
                "./ . . . . . . . . / . . . . . . .'|\n" +
                "'/ . . . . . . . . . . . . . . . .'|\n" +
                "'| . . . . . \\ . . . . . . . . . .|\n" +
                "'| . . . . . . \\„_^- „ . . . . .'|\n" +
                "'| . . . . . . . . .'\\ .\\ ./ '/ . |\n" +
                "| .\\ . . . . . . . . . \\ .'' / . '|\n" +
                "| . . . . . . . . . . / .'/ . . .|\n" +
                "| . . . . . . .| . . / ./ ./ . .|").queue();
    }
    public void handleDefinition(String msg, MessageReceivedEvent event){

        try {
            String word = msg.substring(8);
            ArrayList<String> definitionList = Dictionary.getDefinition(word);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(colors[(int)(Math.random()*colors.length)]);
            String url = "https://www.urbandictionary.com/define.php?term="+word;
            url = url.replaceAll(" ", "%20");
            eb.setTitle (word, url);
            if (definitionList == null){
                eb.addField ("Error", "No definition found!", true);
            }
            else {
                for (int i = 0; i < definitionList.size(); i ++) {
                    eb.addField((i + 1) + ". ", definitionList.get(i), false);
                }
            }
            event.getChannel().sendMessage(eb.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch(StringIndexOutOfBoundsException e){
            event.getChannel().sendMessage("Please make sure to actually enter a word to define. ").queue();
        }


    }

    public void handleRacist(String msg, MessageReceivedEvent event){
        if (msg.contains(command)&& msg.contains("racistness")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current racistness is: " + getUserInfo(event).getRacistness()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has a racistness of "+ user.getRacistness()).queue();
            }
        }
        else if(RacistChecker.checkForRacism(msg)){
            try {
                updateRacistness(1, event);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleCheer(MessageReceivedEvent event){
        if (event.getMessage().getContentRaw().equals("0!")){

            event.getChannel().sendMessage("0! = 1").queue();
        }
        else{
            MessageBuilder msg = new MessageBuilder("Whoot whoot!");
            msg.setTTS(true);
            event.getChannel().sendMessage(msg.build()).queue();
        }
    }

    public void handleGayness(String msg, MessageReceivedEvent event){
        if (msg.contains(command)&& msg.contains("gayness")) {
            System.out.println(event.getMessage().getMentionedUsers().size());
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current gayness is: " + getUserInfo(event).getGayness()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage("<@" + mentionedUserId + "> has a gayness of " + user.getGayness()).queue();
            }
        }else if (GayChecker.checkForGayness(msg)) {
            event.getMessage().addReaction("\uD83C\uDFF3️\u200D\uD83C\uDF08").queue();
            try {
                updateGayness(1, event);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSwearWord(String msg, MessageReceivedEvent event){
        if (msg.contains(command)&& msg.replace(" ", "").contains("swearcount")) {
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current swear count is: " + getUserInfo(event).getSwearCount()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has a swear count of "+ user.getSwearCount()).queue();
            }
        }else if (SwearWordChecker.checkForSwearWord(msg)!= 0) {
            try {
                updateSwearCount(SwearWordChecker.checkForSwearWord(msg), event);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleGender(String msg, MessageReceivedEvent event){
        if (msg.toLowerCase().contains(command+"gender set")) {
            try {
                updateGender(msg.substring(12), event);
                event.getChannel().sendMessage("Your gender is now " + getUserInfo(event).getGender()).queue();
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }else if(msg.contains(command+"gender")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your gender is " + getUserInfo(event).getGender()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage("Gender of <@"+mentionedUserId+"> : "+ user.getGender()).queue();
            }
        }
    }

    public void handlePenisSize(String msg, MessageReceivedEvent event){
        if (msg.toLowerCase().contains(command+"penis set")) {
            try {
                updatePenisSize(msg.substring(11), event);
                event.getChannel().sendMessage("Your penis size is(length, diameter): (" + getUserInfo(event).getPenisSize()[0]+", "+getUserInfo(event).getPenisSize()[1]+")").queue();
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }else if(msg.contains(command+"penis")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your penis size is(length, diameter): (" + getUserInfo(event).getPenisSize()[0]+", "+getUserInfo(event).getPenisSize()[1]+")").queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage("Penis size of <@"+mentionedUserId+"> is(length, diameter): (" + user.getPenisSize()[0]+", "+user.getPenisSize()[1]+")").queue();
            }
        }
    }

    public void handleProfile(MessageReceivedEvent event){
        if (event.getMessage().getContentRaw().contains("all")){
            for (int i = 0; i < userInfoArrayList.size(); i++){
                String mentionedUserId = userInfoMapReverse.get(i);
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage(user.getProfile()).queue();
            }

        }
        else {
            if (event.getMessage().getMentionedUsers().size() == 0) {
                event.getChannel().sendMessage(getUserInfo(event).getProfile()).queue();
            } else {
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoArrayList.get(userInfoMap.get(mentionedUserId));
                event.getChannel().sendMessage(user.getProfile()).queue();
            }
        }

    }

    public void handleEmotes(String msg, MessageReceivedEvent event){
        StringTokenizer st = new StringTokenizer(msg);
        while(st.hasMoreTokens()){
            String word = st.nextToken();
            if (word.toLowerCase().contains("imposter")) {
                Emote emote = event.getGuild().getEmoteById("771910811148484628");
                event.getMessage().addReaction(emote).queue();
            }
            if (word.toLowerCase().contains("kill")){

                event.getMessage().addReaction("🔪").queue();
            }
            if (word.toLowerCase().contains("crewmate")){

                Emote emote = event.getGuild().getEmoteById("771911973612421151");
                event.getMessage().addReaction(emote).queue();
            }
        }
    }

    public void handlePlayMusic(String msg, MessageReceivedEvent event){
        VoiceChannel vChannel = event.getMember().getVoiceState().getChannel();
        if (vChannel != null) {
            manager.openAudioConnection(vChannel);
            int indexOfSpace = msg.indexOf(" ");
            String url = msg.substring(indexOfSpace+1);
            if (!isUrl(url)) {
                url = "ytsearch:" + url;

            }
            System.out.println(url);
            PlayerManager.getInstance()
                    .loadAndPlay(event.getTextChannel(), url);
        }
        else{
            event.getChannel().sendMessage("Please join a voice channel! ").queue();
        }
        handleStartMusic(msg,event);
    }
    public void handleStopMusic(String msg, MessageReceivedEvent event){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();


    }
    public void handleSkipMusic(String msg, MessageReceivedEvent event){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.nextTrack();

    }
    public void handlePauseMusic(String msg, MessageReceivedEvent event){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.onPause();

    }
    public void handleStartMusic(String msg, MessageReceivedEvent event){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        musicManager.scheduler.onStart();

    }
    public void handleQueueMusic(String msg, MessageReceivedEvent event){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(colors[(int)(Math.random()*colors.length)]);
        eb.setTitle ("Queue");

        AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
        eb.addField("Currently Playing:", currentTrack.getInfo().title+ " by "+currentTrack.getInfo().author, false);

        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;


        int i = -1;
        for (AudioTrack track: queue){
            i++;
            System.out.println(track);
            eb.addField((i + 1) + ". ", track.getInfo().title+ " by "+track.getInfo().author, false);
        }
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public UserInfo getUserInfo(MessageReceivedEvent event){
        return userInfoArrayList.get(userInfoMap.get(event.getAuthor().getId()));
    }

    public void updateRacistness(int  change, MessageReceivedEvent event) throws IOException, CsvException {
        int row = userInfoMap.get(event.getAuthor().getId());
        UserInfo user =  getUserInfo(event);
        user.setRacistness(user.getRacistness()+change);

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> csvBody = reader.readAll();
        csvBody.get(row)[2]=String.valueOf(user.getRacistness());
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public void updateGayness(int  change, MessageReceivedEvent event) throws IOException, CsvException {
        int row = userInfoMap.get(event.getAuthor().getId());
        UserInfo user =  getUserInfo(event);
        user.setGayness(user.getGayness()+change);

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> csvBody = reader.readAll();
        csvBody.get(row)[1]=String.valueOf(user.getGayness());
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public void updateGender(String change, MessageReceivedEvent event) throws IOException, CsvException {
        int row = userInfoMap.get(event.getAuthor().getId());
        UserInfo user =  getUserInfo(event);
        user.setGender(change);

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> csvBody = reader.readAll();
        csvBody.get(row)[4]=change;
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public void updatePenisSize(String change, MessageReceivedEvent event) throws IOException, CsvException {
        int row = userInfoMap.get(event.getAuthor().getId());
        UserInfo user =  getUserInfo(event);
        user.setPenisSize(change.split(" "));

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> csvBody = reader.readAll();
        csvBody.get(row)[5]=change.replace(" ", ":");
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public void updateSwearCount(int  change, MessageReceivedEvent event) throws IOException, CsvException {
        int row = userInfoMap.get(event.getAuthor().getId());
        UserInfo user =  getUserInfo(event);
        user.setSwearCount(user.getSwearCount()+change);

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> csvBody = reader.readAll();
        csvBody.get(row)[3]=String.valueOf(user.getSwearCount());
        reader.close();

        CSVWriter writer = new CSVWriter(new FileWriter(fileName));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }

    public void inputInformationFromDataCSV() throws CsvValidationException, IOException {
        in = new CSVReader(new FileReader(fileName));
        String [] data = in.readNext();
        while (data!=null){
            userInfoMap.put(data[0], count);
            userInfoMapReverse.put(count, data[0]);
            String[] temp5 = data[5].split(":");
            userInfoArrayList.add(new UserInfo(jda.retrieveUserById(data[0]).complete(), Integer.parseInt(data[1]),Integer.parseInt(data[2]), Integer.parseInt(data[3]), data[4],new String[]{temp5[0], temp5[1]}));
            data = in.readNext();
            count++;
        }
        in.close();
    }

    public void validateUser(String id) throws IOException {
        if (!userInfoMap.containsKey(id)){
            newUserByMention(id);
        }
    }

    public void newUserByMention(String id) throws IOException {
        userInfoMap.put(id, count);
        userInfoMapReverse.put(count, id);
        userInfoArrayList.add(new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", new String[]{"0","0"}));

        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));

        String [] record = (id+","+0+","+0+","+0+","+"unidentified"+","+"0:0"+","+0+","+0+","+0+","+0+","+0+","+0+","+0).split(",");

        writer.writeNext(record);

        writer.close();
        count++;
    }

    public void newUser(String id) throws IOException {
        userInfoMap.put(id, count);
        userInfoMapReverse.put(count, id);
        userInfoArrayList.add(new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", new String[]{"0","0"}));
        CSVWriter writer = new CSVWriter(new FileWriter(fileName, true));

        String [] record = (id+","+0+","+0+","+0+","+"unidentified"+","+"0:0"+","+0+","+0+","+0+","+0+","+0+","+0+","+0).split(",");

        writer.writeNext(record);

        writer.close();
        count++;
    }

    public String getGuildId() {
        return guild.getId();
    }

}
