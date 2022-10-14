import java.util.ArrayList;

public class Hangman {
    private String gameState;
    private String[] wordBank= new String[]{"osteoporosis", "shakespeare", "photosynthesis", "tea", "trevor", "green",
            "diet", "linear", "electron", "goat", "armor",
    "characterization", "diagonal", "diagonalization", "preach", "span", "derivative", "module", "eigenvalue",
    "track", "player", "note", "untimely", "keyboard", "algebra", "learn", "rank", "form", "reduced", "induction", "unique",
    "matrix", "vector", "square", "elementary", "dot", "product", "scalar", "coordinate", "subspace", "base", "basis",
    "column", "row", "dependent", "reflection", "transformation", "arithmetic", "contradiction", "perpendicular",
    "algebraic", "field", "ring", "squeeze", "sequence", "series", "criterion", "lemma", "theorem", "computer", "club",
    "wheel", "word", "guess", "private", "index", "count", "next", "state", "home", "work", "piazza", "integral", "variable",
    "constant", "trigonometry", "identity", "evaluate", "finite", "infinite", "inverse", "antiderivative", "substitution",
    "elimination", "expression", "consider", "part", "apply", "remark", "might", "you", "example", "class", "suit",
    "that", "equivalent", "every", "for", "particular", "assume", "enough", "show", "geometric", "ratio", "root", "converge",
    "numeric"};

    private String currentWord;
    private int guessCount;
    private String stringToReturn = "";
    private String messageToReturn = "Please guess!";
    private int guessedCharactersCount = 0;
    private ArrayList<Character> guessedCharacters = new ArrayList<>();
    public Hangman(){
        int index = (int)(Math.random()*wordBank.length);
        currentWord = wordBank[index];
        for (int i = 0; i <currentWord.length(); i++) {
            stringToReturn += "_";
        }
        guessCount = 0;
        nextGameState(guessCount);

    }
    public String getGameState(){
        return gameState;
    }
    public String getStringToReturn(){

        return stringToReturn.replace(""," ").trim();
    }
    public String getMessageToReturn(){
        return messageToReturn;
    }
    public void nextMove (String str) {
        String message = str.toLowerCase().replace(" ", "").substring(8);
        int len = message.length();
        switch (len) {
            case 0:
                messageToReturn = "Please enter a letter or a word";
                break;
            case 1:
                if (guessedCharacters.contains(message.charAt(0))) {
                    messageToReturn = "You have already guessed this letter";
                }
                else {
                    guessedCharacters.add(message.charAt(0));
                    if (!currentWord.contains(message)) {
                        messageToReturn = "No! " + message.charAt(0) + " is not in the word!";
                        guessCount += 1;
                        nextGameState(guessCount);
                    } else {
                        ModifyString(message.charAt(0));
                        messageToReturn = "Yes! " + message.charAt(0) + " is in the word!";
                        if (guessedCharactersCount == currentWord.length()) {
                            messageToReturn += "\nYou have guessed the word! Congrats!";
                        }
                    }
                }
                break;
            default:
                if (message.equalsIgnoreCase(currentWord)) {
                    stringToReturn = currentWord;
                    messageToReturn = "Congrats! You guessed the word!";
                } else if(message.equalsIgnoreCase("reveal")){
                    stringToReturn = currentWord;
                    messageToReturn = "Game Over!";
                }
                else {
                    messageToReturn = "No! " + message + " is not the word. Keep trying!";
                }
                break;
        }
    }

    public boolean ModifyString(char letter){
        int index = currentWord.indexOf(letter);
        boolean ifExist = false;
        while(index!=-1){
            ifExist = true;
            stringToReturn = stringToReturn.substring(0, index) + letter + stringToReturn.substring(index+1);
            guessedCharactersCount+=1;
            index = currentWord.indexOf(letter, index+1);
        }
        return ifExist;

    }
    public void nextGameState(int stage){
        switch (stage){
            case 0:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 1:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 2:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        "  |   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 3:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|   |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 4:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        "      |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 5:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " /    |\n" +
                        "      |\n" +
                        "=========```";
                break;
            case 6:
                gameState = "```  +---+\n" +
                        "  |   |\n" +
                        "  O   |\n" +
                        " /|\\  |\n" +
                        " / \\  |\n" +
                        "      |\n" +
                        "=========```";
                messageToReturn+= " Game Over!";
                stringToReturn = currentWord;
                break;

        }
    }

}
