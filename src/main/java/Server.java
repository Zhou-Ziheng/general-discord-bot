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
    public GuessingGame gg;
    public Boolean keepGuessing;
    public String[][] statisticsCommands = new String[][]{
            new String[]{"`MessageCount [@user]`", "Gets the number of messages a user has sent"},
            new String[]{"`interations [@user]`", "Gets the number of time a user interacted with this bot"},
            new String[]{"`TwitchAddiction [@user]`", "Gets the number of time a user said a twitch emote"},
            new String[]{"`pronouns`", "Gets a user's pronouns"},
            new String[]{"`set pronouns`", "Sets a user's pronnouns"},
            new String[]{"`gender [@user]`", "Gets a user's gender"},
            new String[]{"`set gender [gender]`", "Sets a user's gender"},
            new String[]{"`define [word]`", "Retrieves the definition of [word] from Urban Dictionary"},
            new String[]{"`avatar [@user]`", "Gets a user's profile picture"},
            new String[]{"`profile [@user]`", "Get's all statistics of a user"}};
    public String[][] music = new String[][]{
                    new String[]{"`play [song-title]`", "Plays [song-title]"},
                    new String[]{"`skip`", "Skip the current song"},
                    new String[]{"`dc`", "Tells the bot to disconnect"},
                    new String[]{"`queue`", "Gets the current music queue"},
                    new String[]{"`stop`", "Stops the music and clears queue"},
                    new String[]{"`random`", "Randomly plays a song"}};
    public String[][] miniGames = new String[][]{
                    new String[]{"`hangman`", "Creates a game of hangman"},
                    new String[]{"`guess`", "Plays a random song and asks the user to guess the title"},
                    new String[]{"`start guess`", "Continuously plays a random song and asks the user to guess the title"},
                    new String[]{"`stop guess`", "Stops generating random songs to guess"}};

    static final String command = "!";
    public Color[] colors = new Color[]{Color.magenta, Color.black, Color.red, Color.pink, Color.cyan, Color.gray, Color.green, Color.lightGray, Color.yellow, Color.orange, Color.white, Color.blue};
    public Server(String id, JDA jda) throws IOException, InterruptedException {
        fileName = System.getProperty("user.dir")+ File.separator + "ServerData" + File.separator + id+".csv";
        this.jda = jda;
        guild = jda.getGuildById(id);
        assert guild != null;
        keepGuessing = false;
        manager = guild.getAudioManager();
        inputInformationFromDataCSV(id);
        List<Member> members = guild.getMembers();
        for (int i = 0; i <members.size(); i ++){
            if (members.get(i).getUser().isBot()) {
                continue;
            }
            if (!userInfoMap.containsKey(members.get(i).getId())){
                newUser(members.get(i).getId(), id);
            }
        }
    }
    public HashMap<Member, Hangman> hangmanHashMap = new HashMap<>();
    public void onMessageReceived(MessageReceivedEvent event) throws IOException {

        if (event.getAuthor().isBot()) {
            return;
        }
        if (!userInfoMap.containsKey(event.getAuthor().getId())){
            newUser(event.getAuthor().getId(), event.getGuild().getId());
        }
        String msg = event.getMessage().getContentRaw().toLowerCase();
        if (gg != null && msg.toLowerCase().contains(command+"g ")) {
            updateInteractions(1, event);
            System.out.println(msg.substring(3));
            Boolean result = gg.verifyGuess(msg.substring(3), event);
            if (result) {
                gg = null;
            }
            if (keepGuessing) {
                gg = new GuessingGame(manager, guild.getId(), event, this);
                gg.playMusic(event);
            }
        }
        updateMessageCount(1, event);
        if (msg.toLowerCase().equals(command+"stop guess")) {
            updateInteractions(1, event);
            keepGuessing = false;
        }
        if (msg.toLowerCase().equals(command+"start guess")) {
            updateInteractions(1, event);
            keepGuessing = true;
            gg = new GuessingGame(manager, guild.getId(), event, this);
            gg.playMusic(event);
        }
        if (msg.toLowerCase().contains(command+"hangman")){
            updateInteractions(1, event);
            handleHangman(event);
        }
        if (msg.toLowerCase().equals(command+"random")){
            updateInteractions(1, event);
            ListOfSongs.randomSong(event, this);

        } else if (msg.toLowerCase().contains(command+"random")){
            updateInteractions(1, event);
            int iteration = Integer.parseInt(msg.substring(8));
            for (int i = 0; i < iteration; i++) {
                ListOfSongs.randomSong(event, this);
            }
        }
        if (msg.toLowerCase().contains(command+"guess")){
            updateInteractions(1, event);
            gg = new GuessingGame(manager, guild.getId(), event, this);
            gg.playMusic(event);
        }
        if (event.getAuthor().getName().equals("Yoyocube")&&msg.toLowerCase().contains("stupid")&& msg.toLowerCase().contains("bot")){
            updateInteractions(1, event);
            event.getChannel().sendMessage("no u").queue();
        }
        if (msg.contains(command+"define")){
            updateInteractions(1, event);
            handleDefinition(msg, event);
        }
        if (msg.toLowerCase().contains (command+"avatar")){
            updateInteractions(1, event);
            if (event.getMessage().getMentionedUsers().size()!=0){
                String image = event.getMessage().getMentionedUsers().get(0).getAvatarUrl();
                event.getChannel().sendMessage(image).queue();
            }
            else {
                String image = event.getAuthor().getAvatarUrl();
                event.getChannel().sendMessage(image).queue();
            }
        }
        if (msg.toLowerCase().contains(command+"help")){
            updateInteractions(1, event);
            handleHelp(event);
        }
        if (msg.toLowerCase().contains(command+"interactions")){
            updateInteractions(1, event);
            handleInteractions(msg, event);
        }
        if (msg.toLowerCase().contains(command+"messagecount")){
            updateInteractions(1, event);
            System.out.println("here");
            handleMessageCount(msg, event);
        }
        if (msg.toLowerCase().contains(command+"twitchaddiction")){
            updateInteractions(1, event);
            handleTwitchAddiction(msg, event);
        }
        if (msg.toLowerCase().contains(command+"gender")){
            updateInteractions(1, event);
            handleGender(msg, event);
        }
        if (msg.toLowerCase().contains(command+"set pronouns")||msg.contains(command+"pronouns")){
            updateInteractions(1, event);
            handlePronouns(msg, event);
        }
        if (msg.toLowerCase().contains(command+"profile")) {
            updateInteractions(1, event);
            handleProfile(event);
        }
        handleEmotes(msg, event);
        if (msg.toLowerCase().contains(command+"p ")){
            updateInteractions(1, event);
            handlePlayMusic(msg, event, false);
        }
        if (msg.toLowerCase().contains(command+"play")){
            updateInteractions(1, event);
            System.out.println("here");
            handlePlayMusic(msg, event, false);
        }
        if (msg.equalsIgnoreCase(command+"stop")){
            updateInteractions(1, event);
            handleStopMusic(msg,event);
        }
        if (msg.equalsIgnoreCase(command+"skip")){
            updateInteractions(1, event);
            handleSkipMusic(msg,event);
        }
        if (msg.equalsIgnoreCase(command+"queue")||msg.equalsIgnoreCase(command+"q")){
            updateInteractions(1, event);
            handleQueueMusic(msg,event, false);
        }
        if (msg.equalsIgnoreCase(command+"dc")){
            updateInteractions(1, event);
            System.out.println("here");
            handleStopMusic(msg,event);
            this.manager.closeAudioConnection();
        }
        if (msg.equalsIgnoreCase(command+"pause")){
            updateInteractions(1, event);
            System.out.println("here");
            handlePauseMusic(msg,event);
        }if (msg.equalsIgnoreCase(command+"start")){
            updateInteractions(1, event);
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
            evt.getChannel().sendMessage("New game created!\n"+
                    "\"Commands:\n!hangman ` ` - guess a letter\n!hangman `word` - guess a word\n!hangman reveal - reveal answer\"").queue();

            hangmanHashMap.put(member, new Hangman());
            Hangman game = hangmanHashMap.get(member);
            evt.getChannel().sendMessage(game.getGameState()+"```"+game.getStringToReturn()+"``````"+game.getMessageToReturn()+("```")).queue();
        }
        else {
            Hangman game = hangmanHashMap.get(member);
            game.nextMove(evt.getMessage().getContentRaw().toLowerCase());
            evt.getChannel().sendMessage(game.getGameState()+"```"+game.getStringToReturn()+"``````"+game.getMessageToReturn()+("```")).queue();
            if (game.getMessageToReturn().toLowerCase().contains("game over")||game.getMessageToReturn().toLowerCase().contains("congrat")){
                hangmanHashMap.remove(member);
            }
        }
    }
    public void handleHelp(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(colors[(int)(Math.random()*colors.length)]);

        eb.addField ("Commands:", "**General**", false);
        for (int i = 0; i < statisticsCommands.length; i++){
            eb.addField (command + statisticsCommands[i][0], " - "+statisticsCommands[i][1], true);
        }

        eb.addField ("", "**Music**", false);
        for (int i = 0; i < music.length; i++){
            eb.addField (command + music[i][0], " - "+music[i][1], true);
        }
        eb.addField ("", "**Mini Games**", false);
        for (int i = 0; i < miniGames.length; i++){
            eb.addField (command + miniGames[i][0], " - "+miniGames[i][1], true);
        }
        event.getChannel().sendMessage(eb.build()).queue();
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

    public void handleInteractions(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.contains("interactions")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your have interacted with this bot " + userInfoMap.get(id).getInteractions() + " times.").queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has interacted with this bot "+ user.getInteractions() + " times.").queue();
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

    public void handleMessageCount(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.toLowerCase().contains("messagecount")) {
            System.out.println(event.getMessage().getMentionedUsers().size());
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your have sent a total of " + userInfoMap.get(id).getMessageCount() + " messasges.").queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@" + mentionedUserId + "> has sent a total of " + user.getMessageCount() + " messasges.").queue();
            }
        }
    }

    public void handleTwitchAddiction(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.contains(command)&& msg.toLowerCase().contains("twitchaddiction")) {
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your have said twitch emotes " + userInfoMap.get(id).getTwitchAddiction() + " times.").queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("<@"+mentionedUserId+"> has  said twitch emotes "+ user.getTwitchAddiction() + " times.").queue();
            }
        }else if (msg.contains("pog")) {
            updateTwitchAddiction(1, event);
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

    public void handlePronouns(String msg, MessageReceivedEvent event){
        String id = event.getAuthor().getId();
        if (msg.toLowerCase().contains(command+"set pronouns")) {

            updatePronouns(msg.substring(14), event);
            event.getChannel().sendMessage("Your pronouns are: " +
                    userInfoMap.get(id).getPronouns()).queue();

        }else if(msg.contains(command+"pronouns")){
            if (event.getMessage().getMentionedUsers().size() == 0){
                event.getChannel().sendMessage("Your pronouns are: " + userInfoMap.get(id).getPronouns()).queue();
            }else{
                String mentionedUserId = event.getMessage().getMentionedUsers().get(0).getId();
                try {
                    validateUser(mentionedUserId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserInfo user = userInfoMap.get(mentionedUserId);
                event.getChannel().sendMessage("Pronouns of <@"+mentionedUserId+"> are: " + user.getPronouns()).queue();
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

    public void handlePlayMusic(String msg, MessageReceivedEvent event, Boolean guess){
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
                    .loadAndPlay(event.getTextChannel(), url, guess);
            }
        else{
            System.out.println("Yo");
            event.getChannel().sendMessage("Please join a voice channel! ").queue();
        }
        handleStartMusic(msg,event);
        /*if (!guess) {
            System.out.println("JOE");
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            System.out.println(musicManager);
            AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
            System.out.println(currentTrack);
            event.getChannel().sendMessage("Adding to queue: `")
                                .append(currentTrack.getInfo().title)
                                .append("` by `")
                                .append(currentTrack.getInfo().author)
                                .append("`")
                                .queue();
        } else {
            event.getChannel().sendMessage("Random song generated. Use "+command+"g ` ` to guess.")
                    .queue();
        }*/
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
    public void handleQueueMusic(String msg, MessageReceivedEvent event, Boolean guess){
        GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(colors[(int)(Math.random()*colors.length)]);
        eb.setTitle ("Queue");

        AudioTrack currentTrack = musicManager.audioPlayer.getPlayingTrack();
        eb.addField("Currently Playing:", currentTrack.getInfo().title + " by " + currentTrack.getInfo().author, false);

        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;


        int i = -1;
        for (AudioTrack track: queue){
            i++;
            System.out.println(track);
            eb.addField((i + 1) + ". ", track.getInfo().title+ " by "+track.getInfo().author, false);
        }
        event.getChannel().sendMessage(eb.build()).queue();
    }


    public void updateInteractions(int  change, MessageReceivedEvent event){
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setInteractions(user.getInteractions()+change);
        updateField(event);
    }

    public void updateMessageCount(int change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setMessageCount(user.getMessageCount()+change);
        updateField(event);
    }

    public void updateGender(String change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setGender(change);
        updateField(event);
    }

    public void updatePronouns(String change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setPronouns(change);
        updateField(event);
    }

    public void updateTwitchAddiction(int change, MessageReceivedEvent event) {
        UserInfo user = userInfoMap.get(event.getAuthor().getId());
        user.setTwitchAddiction(user.getTwitchAddiction()+change);
        updateField(event);
    }

    public void updateField(MessageReceivedEvent event) {
        String server_id =  event.getGuild().getId();
        String user_id =  event.getAuthor().getId();
        String url = Main.URLAddress+"login/servers/"+server_id+"/users/"+user_id;
        UserInfo user = userInfoMap.get(user_id);
        String payload="{\"user_name\":\""+event.getAuthor().getName()+"\",\"user_id\":\""+user_id+"\",\"message_count\":\""+user.getMessageCount()+"\",\"interactions\":\""+user.getInteractions()
                +"\",\"twitch_addiction\":\""+user.getTwitchAddiction()+"\",\"gender\":\""+user.getGender()+"\"," +
                "\"pronouns\":\""+user.getPronouns()+"\"}";
        Requests.sendPutRequest(url,payload);

    }
    public void inputInformationFromDataCSV(String server_id) {
        System.out.println(server_id);
        HttpResponse<String> response = Requests.sendGetRequest(Main.URLAddress+"login/servers/"+server_id+"/");
        String responseBody = response.body();
        System.out.println(responseBody);
        JSONObject obj = new JSONObject(responseBody);
        JSONArray array = obj.getJSONArray("data");

        for (int i = 0; i < array.length(); i++) {
            JSONObject user = array.getJSONObject(i);
            String user_id = user.getString("user_id");
            try {
                userInfoMap.put(user_id, new UserInfo(jda.retrieveUserById(user_id).complete(), Integer.parseInt(user.getString("message_count")),
                        Integer.parseInt(user.getString("interactions")), Integer.parseInt(user.getString("twitch_addiction")),
                        user.getString("gender"), user.getString("pronouns")));
            } catch (Exception e){

            }
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

    public void newUser(String id, String server_id) {
        userInfoMap.put(id, new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", "unidentified"));
        //userInfoMapReverse.put(count, id);
        //userInfoArrayList.add(new UserInfo(jda.retrieveUserById(id).complete(),0,0, 0, "unidentified", new String[]{"0","0"}));

        String payload="{\"user_name\":\""+jda.retrieveUserById(id).complete().getName()+"\",\"user_id\":\""+id+"\",\"message_count\":\"0\",\"interactions\":\"0\",\"twitch_addiction\":\"0\",\"gender\":\"unidentified\",\"pronouns\":\"unidentified\"}";
        String requestUrl=Main.URLAddress+"login/servers/" + server_id + "/users";


        Requests.sendPostRequest(requestUrl, payload);
    }


}
