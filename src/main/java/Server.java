import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Server {
    public JDA jda;
    public HashMap<String, UserInfo> userInfoMap = new HashMap<>();
    public String fileName;
    public Guild guild;
    public AudioManager manager;
    private final String command = "~";
    public Color[] colors = new Color[]{Color.magenta, Color.black, Color.red, Color.pink, Color.cyan, Color.gray, Color.green, Color.lightGray, Color.yellow, Color.orange, Color.white, Color.blue};
    public Server(String id, JDA jda) throws IOException, InterruptedException {
        fileName = System.getProperty("user.dir")+ File.separator + "ServerData" + File.separator + id+".csv";
        this.jda = jda;
        guild = jda.getGuildById(id);
        manager = guild.getAudioManager();
        inputInformationFromDataCSV(id);
        List<Member> members = guild.getMembers();
        for (int i = 0; i <members.size(); i ++){
            if (members.get(i).getUser().isBot()) {
                continue;
            }
            if (!userInfoMap.containsKey(members.get(i).getId())){
                try {
                    newUser(members.get(i).getId(), id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public HashMap<Member, Hangman> hangmanHashMap = new HashMap<>();
    public void onMessageReceived(MessageReceivedEvent event) throws IOException {

        if (event.getAuthor().isBot()) {
            return;
        }

        if (!userInfoMap.containsKey(event.getAuthor().getId())){
            try {
                newUser(event.getAuthor().getId(), event.getGuild().getId());
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
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.contains("racistness")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current racistness is: " + userInfoMap.get(id).getRacistness()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has a racistness of "+ user.getRacistness()).queue();
            }
        }
        else if(RacistChecker.checkForRacism(msg)){
            updateRacistness(1, event);
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
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.contains("gayness")) {
            System.out.println(event.getMessage().getMentionedUsers().size());
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current gayness is: " + userInfoMap.get(id).getGayness()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@" + mentionedUserId + "> has a gayness of " + user.getGayness()).queue();
            }
        }else if (GayChecker.checkForGayness(msg)) {
            event.getMessage().addReaction("\uD83C\uDFF3️\u200D\uD83C\uDF08").queue();
            updateGayness(1, event);
        }
    }

    public void handleSwearWord(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.replace(" ", "").contains("swearcount")) {
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your current swear count is: " + userInfoMap.get(id).getSwearCount()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has a swear count of "+ user.getSwearCount()).queue();
            }
        }else if (SwearWordChecker.checkForSwearWord(msg)!= 0) {
            updateSwearCount(SwearWordChecker.checkForSwearWord(msg), event);

        }
    }

    public void handleGender(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.toLowerCase().contains(command+"gender set")) {

            updateGender(msg.substring(12), event);
            event.getChannel().sendMessage("Your gender is now " + userInfoMap.get(id).getGender()).queue();

        }else if(msg.contains(command+"gender")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your gender is " + userInfoMap.get(id).getGender()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("Gender of <@"+mentionedUserId+"> : "+ user.getGender()).queue();
            }
        }
    }

    public void handlePenisSize(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.toLowerCase().contains(command+"penis set")) {

            updatePenisSize(msg.substring(11), event);
            event.getChannel().sendMessage("Your penis size is(length, diameter): (" +
                    userInfoMap.get(id).getPenisSize()[0]+", "+userInfoMap.get(id).getPenisSize()[1]+")").queue();

        }else if(msg.contains(command+"penis")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your penis size is(length, diameter): (" + userInfoMap.get(id).getPenisSize()[0]+", "+userInfoMap.get(id).getPenisSize()[1]+")").queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("Penis size of <@"+mentionedUserId+"> is(length, diameter): (" + user.getPenisSize()[0]+", "+user.getPenisSize()[1]+")").queue();
            }
        }
    }

    public void handleProfile(MessageReceivedEvent event){
        if (event.getMessage().getMentionedUsers().size() == 0) {
            event.getChannel().sendMessage(userInfoMap.get(event.getAuthor().getId()).getProfile()).queue();
        } else {
            String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
            try {
                validateUser(mentionedUserId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserInfo user = userInfoMap.get(mentionedUserId);
            event.getChannel().sendMessage(user.getProfile()).queue();
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


    public void updateRacistness(int  change, MessageReceivedEvent event){
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setRacistness(user.getRacistness()+change);
        updateField(event);
    }

    public void updateGayness(int  change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setGayness(user.getGayness()+change);
        updateField(event);
    }

    public void updateGender(String change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setGender(change);
        updateField(event);
    }

    public void updatePenisSize(String change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setPenisSize(change.split(" "));
        updateField(event);
    }

    public void updateSwearCount(int change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setSwearCount(user.getSwearCount()+change);
        updateField(event);
    }

    public void updateField(MessageReceivedEvent event) {
        String server_id =  event.getGuild().getId();
        String user_id =  event.getAuthor().getId();
        String url = Main.URLAddress+"login/servers/"+server_id+"/users/"+user_id;
        UserInfo user = userInfoMap.get(user_id);
        String payload="{\"user_id\":\""+user_id+"\",\"gayness\":\""+user.getGayness()+"\",\"racistness\":\""+user.getRacistness()
                +"\",\"swear_count\":\""+user.getSwearCount()+"\",\"gender\":\""+user.getGender()+"\"," +
                "\"penis_size\":\""+user.getPenisSize()[0]+":"+user.getPenisSize()[1]+"\"}";
        Server.sendRequest(url,payload, "PUT");
        System.out.println(Server.sendRequest(url, payload, "PUT"));

    }
    public void inputInformationFromDataCSV(String server_id) {
        System.out.println(server_id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Main.URLAddress+"login/servers/"+server_id+"/"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assert response != null;
        String responseBody = response.body();
        System.out.println("gdsrgsvdugdshrulivndsrvg");
        System.out.println(responseBody);
        JSONObject obj = new JSONObject(responseBody);
        JSONArray array = obj.getJSONArray("data");

        for (int i = 0; i < array.length(); i++) {
            JSONObject user = array.getJSONObject(i);
            String user_id = user.getString("user_id");
            String[] temp5 = user.getString("penis_size").split(":");
            userInfoMap.put(user_id, new UserInfo(jda.retrieveUserById(user_id).complete(), Integer.parseInt(user.getString("gayness")),
                    Integer.parseInt(user.getString("racistness")),Integer.parseInt(user.getString("swear_count")),
                    user.getString("gender"),new String[]{temp5[0], temp5[1]}));
        }
    }

    public void validateUser(String id) throws IOException {
        if (!userInfoMap.containsKey(id)){
            newUserByMention(id);
        }
    }

    public void newUserByMention(String id) throws IOException {
        newUser(id, guild.getId());
    }

    public void newUser(String id, String server_id) throws IOException {
        userInfoMap.put(id, new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", new String[]{"0","0"}));
        //userInfoMapReverse.put(count, id);
        //userInfoArrayList.add(new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", new String[]{"0","0"}));

        System.out.println("asdfaaaaaaaaaaaaaaaaaaaaa");
        String payload="{\"user_id\":\""+id+"\",\"gayness\":\"0\",\"racistness\":\"0\",\"swear_count\":\"0\",\"gender\":\"unidentified\",\"penis_size\":\"0:0\"}";
        String requestUrl=Main.URLAddress+"login/servers/" + server_id + "/users";


        System.out.println(sendRequest(requestUrl, payload, "POST"));
    }
    public static String sendRequest(String requestUrl, String payload, String type) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(type);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
            return jsonString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    public String getGuildId() {
        return guild.getId();
    }

}
