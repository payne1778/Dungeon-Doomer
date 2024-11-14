import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Holds most of the backend logic for the game. 
 * 
 * @author Blake Payne
 * @since  08.19.2024
 */
public class MainLogic {
    
    private static Integer dungeonSize;              // Stores the numerical size of the dungeon
    private static Integer merchantCounter = 0;      // Keeps track of the number of merchants created
    private static Integer skeletonCounter = 0;      // Keeps track of the number of skeletons created
    private static Integer goblinCounter = 0;        // Keeps track of the number of goblins created
    private static Integer mimicCounter = 0;         // Keeps track of the number of mimics created
    private static Integer golemCounter = 0;         // Keeps track of the number of golems created
    private static Integer potionPrice = 50;         // The price of a potion that a hero can buy from a merchant 
    private static Integer chestReward = 15;         // The reward given to the hero when finding a non-hostile mimic
    private static Integer potionTurnCounter = 0;    // Keeps track of how many turns the hero has a strength boost activated     
    private static Boolean canRetreat = true;        // Keeps track of the ability of characters to retreat from a fight
    private static Boolean gameStart = true;         // Keeps track if the game has been started by Game Window 
    private static Boolean gameOver = false;         // Keeps track if the game has ended due to hero death or escape 
    private static boolean gameRerun = false;        // Keeps track if the game has been rerun or not (only true by escape)
    private static Boolean isInCombat = false;       // Keeps track if the hero is in combat with a monster
    private static Boolean isInTrade = false;        // Keeps track if the hero is in a trade with a merchant 
    private static Boolean characterDeath = false;   // Keeps track of when a character has died in the dungeon
    private static Boolean isSaveLoading = false;    // Checks to see if the user is attempting to load from a save 
    private static String language = "English";      // Stores the string name of the game's language. English is the default.
    
    private static GUIFrame gameWindow;         // GUIFrame class object to access and send information to GUI components
    private static Character tempCharacter;     // Temporary Character class object that serves to create characters and add them to list  
    private static Character hero;              // Character class object that serves as the hero object in the list of characters
    private static PrintStream ps;              // PrintStream object in charge of creating error logs and logging errors. 
    
    private static ArrayList<Character> characterList = new ArrayList<Character>(); // The collection of characters in the dungeon

    public static void main(String[] args) throws IOException {
        gameWindow = new GUIFrame();
        
        // Will warn user not to close game without saving progress. 
        gameWindow.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            
            @Override
            public void windowClosing(WindowEvent e) {
                gameWindow.pushCloseMessage();
            }
            
        });
        
        // Creates a error log file for game instance. 
        try {
            ps = new PrintStream(new File("GameLogs//Error Log " + getCurrentTimeStamp() + ".log"));
        }
        catch (FileNotFoundException e) {
            gameWindow.pushMessage("Warning: Error log could not be created. Please try again.");
            gameWindow.dispose();
        }

    }

    // Getters and Setters for MainLogic class
    public static ArrayList<Character> getCharacterList() {
        return characterList;
    }

    public static void setCharacterList(ArrayList<Character> list) {
        characterList = list;
    }

    public static PrintStream getPrintStream() {
        return ps;
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(Calendar.getInstance().getTime());
    }

    public static GUIFrame getGameWindow() {
        return gameWindow;
    }

    public static Integer getDungeonSize() {
        return dungeonSize;
    }

    public static void setDungeonSize(Integer size) {
        dungeonSize = size;
    }

    public static Integer getPotionTurnCounter() {
        return potionTurnCounter;
    }

    public static void setPotionTurnCounter(Integer turnCount) {
        potionTurnCounter = turnCount;
    }

    public static int getPotionPrice() {
        return potionPrice;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String newLanguageSetting) {
        
        if (!newLanguageSetting.equalsIgnoreCase("English") && !newLanguageSetting.equalsIgnoreCase("German")) {
            try {
                throw new IllegalLanguageException();
            }
            catch (IllegalLanguageException e) { 
                e.printStackTrace(MainLogic.getPrintStream());
            }
            return;
        }
        language = newLanguageSetting;

    }

    public static Boolean getCharacterDeath() {
        return characterDeath;
    }

    public static void setCharacterDeath(Boolean death) {
        characterDeath = death;
    }

    public static Boolean getGameStart() {
        return gameStart;
    }

    public static void setGameStart(boolean start) {
        gameStart = start;
    }

    public static Boolean getGameOver() {
        return gameOver;
    }

    public static void setGameOver(Boolean overBoolean) {
        gameOver = overBoolean;
    }

    public static Boolean getCanRetreat() {
        return canRetreat;
    }

    public static void setCanRetreat(Boolean retreatBoolean) {
        canRetreat = retreatBoolean;
    }

    public static Boolean getIsSaveLoading() {
        return isSaveLoading;
    }

    public static void setIsSaveLoading(Boolean saveLoading) {
        isSaveLoading = saveLoading;
    }

    public static Boolean getGameRerun() {
        return gameRerun;
    }

    public static void setGameRerun(Boolean rerunBoolean) {
        gameRerun = rerunBoolean;
        gameRerunResetter();
    }

    public static Boolean getIsInCombat() {
        
        // If there is no character in the same room, the hero is not in combat
        if (hero.getCharacterInSameRoom() == -1) {
            isInCombat = false;
        }
        return isInCombat;
        
    }

    public static void setIsInCombat(Boolean combatBoolean) {
        isInCombat = combatBoolean;
    }

    public static Boolean getIsInTrade() {
        
        // If there is no character in the same room, the hero is not in a trade
        if (hero.getCharacterInSameRoom() == -1) {
            isInTrade = false;
        }
        return isInTrade;
        
    }

    public static void setIsInTrade(Boolean tradeBoolean) {
        isInTrade = tradeBoolean;
    }

    /**
     * Initializes the game by making the characters and placing them in the dungeon. 
     * Returns the characterList arraylist back to Game Window class. 
     * 
     * @param heroName          The name of the hero as the user had inputted it 
     * @return characterList    The list characters in the dungeon
     */
    public static ArrayList<Character> initializeGame(String heroName) {
        gameWindow.printToTerminal(new LanguageTranslation("Welcome to the Dungeon!", "Willkommen im Kerker!").extrapolate());
        makeHero(heroName);
        makeCharacters();
        setGameStart(false);
        return characterList;
    }

    /**
     * Creates the hero and initializes a random size for the dungeon. 
     * 
     * @param heroName  The name of the hero as the user had inputted it 
     */
    private static void makeHero(String heroName) {
        int randomNum;
        
        while (true) {
            Random random = new Random();
            randomNum = random.nextInt(10);
            if (randomNum > 5) break;  
            else randomNum = random.nextInt(10);
        }
        
        if (getGameStart()) {
            tempCharacter = new Character(heroName, 100, 0, 25, 0, 0, 0, false, false);
            characterList.add(tempCharacter);
            hero = characterList.get(0);
        }
        
        dungeonSize = randomNum;
        tempCharacter.setDungeonSize(dungeonSize);
    }

    /**
     * Generates a random number between 1 and the size of the dungeon. 
     * 
     * @return randomNum    A random number to be returned 
     */
    public static int randomNumber() { 
        // Generates a random number between 0 and the size of the catacomb
        Random random = new Random();
        int randomNum = random.nextInt(dungeonSize);
        return randomNum;
    }

    /**
     * When the hero escapes and wants to keep exploring, the following resets certain values and states to allow this. 
     */
    public static void gameRerunResetter() {
        if (getGameRerun()) {
            Integer numOfCharacters = characterList.size() - 1;
            
            for (int i = numOfCharacters; i > 0; i--) {
                characterList.remove(i);
            }

            merchantCounter = 0;
            skeletonCounter = 0;
            goblinCounter = 0;
            mimicCounter = 0;
            golemCounter = 0;
            hero.setXCord(0);
            hero.setYCord(0);
            hero.setCharacterInSameRoom(-1);
            hero.setTurnCounterValue(0);
            MainLogic.setCharacterDeath(false);
            MainLogic.setIsInCombat(false);
            MainLogic.setIsInTrade(false);
            MainLogic.setIsSaveLoading(false);
            MainLogic.setPotionTurnCounter(0);
            MainLogic.setGameOver(false);
            gameWindow.playGame(false);
            gameWindow.clearTerminalsAndUpdate();
            hero.setDungeonSize(dungeonSize);
            MainLogic.setGameRerun(false);
        }
    }

    /**
     * Ensures that monsters do not spawn on top of each other or at (0, 0).
     * 
     * @return coordinateArray    An array of valid, random coordinates for a monster to spawn
     */
    private static int[] validCoordinates() {
        int xCord = randomNumber(), yCord = randomNumber(); 
        int[] coordinateArray = new int[2];
        
        while (true) {
            boolean invalidCoordinates = false;
            
            // If the x/y coordinates are equal to the catacomb's entrance/exit position, invalidCoordinates will be set to true
            if (((xCord == 0) && (yCord == 0)) || ((xCord == (dungeonSize - 1)) && (yCord == (dungeonSize - 1)))) {
                invalidCoordinates = true;
            }
            
            // If a monster is attempting to spawn where another monster already is, invalidCoordinates will be set to true
            if (characterList.size() == 1) {
                coordinateArray[0] = xCord; coordinateArray[1] = yCord; 
                break;
            } 
            else {

                // Checks every room to see if there is a monster at the generated xCord and yCord coordinates
                for (int j = 1; j < characterList.size(); j++) {
                    if ((xCord == characterList.get(j).getXCord()) && (yCord == characterList.get(j).getYCord())) {
                        invalidCoordinates = true;
                        break;
                    }
                }

            }
            
            // If invalidCoordinates is true, new random coordinates are chosen. Otherwise the while loop breaks
            if (invalidCoordinates) {
                xCord = randomNumber(); yCord = randomNumber();
            } 
            else { 
                coordinateArray[0] = xCord; coordinateArray[1] = yCord; 
                break;
            }
            
        }
        return coordinateArray;
    }

    /**
     * Creates the merchant character(s) and calls the makeMonsters() method afterwards. 
     */
    private static void makeCharacters() {
        int xCord, yCord;
        
        for (int i = 0; i < (dungeonSize * dungeonSize) / 5.5; i++) {
            
            int[] coordinateArray = validCoordinates();
            xCord = coordinateArray[0]; yCord = coordinateArray[1]; 
            
            // If the random coordinates are valid, a new character will be created at that position
            if ((characterList.size() == 1)) { 
                
                if (dungeonSize == 5 || dungeonSize == 6 || dungeonSize == 7) {
                    
                    // Creates one merchant if the dungeonSize is equal to five, six, or seven
                    merchantCounter++;
                    tempCharacter = new Character("Merchant " + merchantCounter, 25, 1, 10, xCord, yCord, 10, true, true);
                    characterList.add(tempCharacter);
                    
                }
                else {
                    
                    // Creates two merchants if the dungeonSize is equal to eight, nine, or ten
                    merchantCounter++;
                    tempCharacter = new Character("Merchant " + merchantCounter, 25, 1, 10, xCord, yCord, 10, true, true);
                    characterList.add(tempCharacter);
                    
                    // Selects new coordinates for the second merchant to spawn at 
                    coordinateArray = validCoordinates();
                    xCord = coordinateArray[0]; yCord = coordinateArray[1];
                    
                    // Creates a second merchant at different coordinates than the first 
                    merchantCounter++;
                    tempCharacter = new Character("Merchant " + merchantCounter, 25, 1, 10, xCord, yCord, 10, true, true);
                    characterList.add(tempCharacter);
                    
                }
                
            }
            else {
                // Once the merchant(s) is/are created, the rest of the monsters will be spawned in one by one
                makeMonsters(xCord, yCord);
            }
        }
    }

    /**
     * Creates a random number of monsters at random places in the dungeon.
     * 
     * @param xCord     A random X coordinate for a monster to spawn at 
     * @param yCord     A random Y coordinate for a monster to spawn at 
     */
    private static void makeMonsters(int xCord, int yCord) {
        switch (randomNumber()) {
            case 0:
                goblinCounter++;
                tempCharacter = new Character("Goblin " + goblinCounter, 15, 2, 5, xCord, yCord, 5, false, false);
                break;
            case 1:
                goblinCounter++;
                tempCharacter = new Character("Goblin " + goblinCounter, 15, 2, 5, xCord, yCord, 5, false, false);
                break;
            case 2:
                goblinCounter++;
                tempCharacter = new Character("Goblin " + goblinCounter, 15, 2, 5, xCord, yCord, 5, false, false);
                break;
            case 3:
                goblinCounter++;
                tempCharacter = new Character("Goblin " + goblinCounter, 15, 2, 5, xCord, yCord, 5, false, false);
                break;
            case 4:
                skeletonCounter++;
                tempCharacter = new Character("Skeleton " + skeletonCounter, 30, 2, 10, xCord, yCord, 8, false, false);
                break;
            case 5:
                skeletonCounter++;
                tempCharacter = new Character("Skeleton " + skeletonCounter, 30, 2, 10, xCord, yCord, 8, false, false);
                break;
            case 6:
                skeletonCounter++;
                tempCharacter = new Character("Skeleton " + skeletonCounter, 30, 2, 10, xCord, yCord, 8, false, false);
                break;
            case 7:
                mimicCounter++;
                tempCharacter = new Character("Mimic " + mimicCounter, 40, 3, 15, xCord, yCord, 13, false, false);
                break;
            case 8:
                mimicCounter++;
                tempCharacter = new Character("Mimic " + mimicCounter, 40, 3, 15, xCord, yCord, 13, false, false);
                break;
            case 9:
                golemCounter++;
                tempCharacter = new Character("Golem " + golemCounter, 50, 2, 20, xCord, yCord, 0, false, false);
                break;
        }
        characterList.add(tempCharacter);
    }

    /**
     * Gets the text from the instructions.txt file and returns it as a string.
     * 
     * @return instructionsText    All of the text from the instructions file
     */
    public static String getInstructionsText() {
        String fileName = "", instructionsText = "", line = "";
        
        LanguageTranslation instructionsError = new LanguageTranslation(
            "The Instructions document cannot be accessed/obtained. Please exit and reload game.", 
            "Die Anweisungen Datei konnte nicht aufgerufen/gefunden. Bitte beenden Sie das Spiel und laden Sie es neu."
        );
        
        try {
            
            switch (language) {
                case "English": fileName = "instructions.txt"; break;
                case "German": fileName = "anweisungen.txt"; break;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader("Instructions//" + fileName));
            line = reader.readLine();
            
            while(line != null) {
                instructionsText += line += "\n";
                line = reader.readLine();
            }
            reader.close();

        }
        catch (Exception e) {
            gameWindow.pushMessage(instructionsError.extrapolate());
            e.printStackTrace(ps);
        }

        return instructionsText;
    }

    /**
     * Clears the error log cache when user clicks on the "Clear Cache" button. 
     */
    public static void clearErrorLogCache() {
        File[] arrayOfFiles = new File("GameLogs//").listFiles();
        boolean successfulClearing = true; 
        
        for (int i = 0; i < arrayOfFiles.length; i++) {
            try {
                Files.deleteIfExists(Paths.get(arrayOfFiles[i].getAbsolutePath()));
            }
            catch (IOException ioe) {
            }
            catch (Exception ex) {
                gameWindow.pushMessage("An error occurred while clearing the cache." + arrayOfFiles[i].getName() + " could not be deleted.");
                successfulClearing = false;
                ex.printStackTrace(ps);
            }
        }
        
        if (successfulClearing) {
            gameWindow.pushMessage(new LanguageTranslation("The game cache has been cleared.", "Der Spiel Cache wurde geleert.").extrapolate()); 
        }

    }

    /**
     * Toggles the retreat boolean. This method is called in the GUI settings() method. 
     */
    public static void toggleRetreat() {
        canRetreat = !canRetreat;
        String retreatSetting = "";
        
        switch (language) {
            case "English": 
                if (canRetreat) { retreatSetting = "Yes"; } else { retreatSetting = "No"; }
                gameWindow.pushMessage("Characters can retreat: " + retreatSetting); 
            break;
            case "German":
                if (canRetreat) { retreatSetting = "Ja"; } else { retreatSetting = "Nein"; }
                gameWindow.pushMessage("Charaktere können sich zurückziehen: " + retreatSetting);
            break;
        }
    }

    /**
     * Toggles the retreat boolean. This method is called in the GUI settings() method. 
     */
    public static void toggleLanguage() {
        switch (language) {
            case "English": language = "German"; break;
            case "German": language = "English"; break;
        }

        switch (language) {
            case "English": gameWindow.pushMessage("The game language is set to English."); break;
            case "German": gameWindow.pushMessage("Die Spielsprache wurde auf Deutsch gesetzt."); break;
        }
    }

    /**
     * Checks to see if the game has ended when a character has died. 
     * 
     * @return gameOver     If true, the game ends. Otherwise, the dead character is removed
     */
    public static boolean isGameOverAndPushMessageIfSo() {
        hero = characterList.get(0);
        
        if (hero.isDead()) {
            gameOver = true;
            // if (pushMessages) gameWindow.pushDeathMessage();
            gameWindow.pushDeathMessage();
        }
        else if (hero.hasEscaped()) {
            gameOver = true;
            // if (pushMessages) gameWindow.pushEscapeMessage();
            gameWindow.pushEscapeMessage();
        }
        
        // TODO: delegate this to a new method and call it in the spots where clean up is needed
        if (!gameOver) {
            
            // Checks to see if any of the monsters have died and removes them from the game if so 
            for (int i = 1; i < characterList.size(); i++) {
                
                if (characterList.get(i).isDead()) {
                    
                    // Informs hero that a character has perished and adds their gold to the hero's "inventory"
                    switch (language) {
                        case "English":
                            gameWindow.printToTerminal("\n" + characterList.get(i).getName() + " has perished!");
                            hero.setGoldValue(hero.getGoldValue() + characterList.get(i).getGoldValue());
                            gameWindow.printToTerminal("\nYou gained " + characterList.get(i).getGoldValue() + " gold from this fight"); 
                            break;
                        case "German": 
                            gameWindow.printToTerminal("\n" + characterList.get(i).getName() + " ist gestorben!");
                            hero.setGoldValue(hero.getGoldValue() + characterList.get(i).getGoldValue());
                            gameWindow.printToTerminal("\nSie haben in diesem Kampf " + characterList.get(i).getGoldValue() + " Geld erhalten"); 
                            break;
                    }
                    
                    characterList.remove(characterList.get(i));
                    setCharacterDeath(false);
                    hero.setCharacterInSameRoom(-1);
                    
                }

            } 

        }
        
        return gameOver;
    }

    /** 
     * Increases the turn counter when called. If the user drank a strength potion, it's effects 
     * will be removed after five turns. 
     */
    public static void incrementTurnCounter() {
        
        hero.setTurnCounterValue(hero.getTurnCounterValue() + 1);
        hero = characterList.get(0);
        
        if (hero.getMaxDamage() == 50) {
            potionTurnCounter += 1;
            
            if (potionTurnCounter > 5) {
                hero.setMaxDamage(25);
                
                switch (language) {
                    case "English": gameWindow.printToTerminal("\n\nThe strength potion ran out! Your max damage is now " + hero.getMaxDamage()); break;
                    case "German": gameWindow.printToTerminal("\n\nDer Krafttrank ist aufgebraucht! Ihre maximaler Schaden Kapabilität beträgt jetzt " + hero.getMaxDamage()); break;
                }

                potionTurnCounter = 0;
                
            }

        }

    }

    /** 
     * Allows hero to drink a health potion if one was previously bought. 
     */
    public static void drinkHealthPotion() {
        hero = characterList.get(0);
        
        if (hero.getHealthPotionCondition()) {
            
            hero.setHealth(hero.getHealth() + 25);
            hero.setHealthPotionCondition(false);
            
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\nYou consumed a health potion. Your health is now " + hero.getHealth()); break;
                case "German": gameWindow.printToTerminal("\n\nSie haben ein Heiltrank getrunken. Ihre Gesundheit beträgt jetzt " + hero.getHealth()); break;
            }
            
        }
        else {
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\nYou don't have a potion to consume"); break;
                case "German": gameWindow.printToTerminal("\n\nSie haben kein Trank zu trinken");  break;
            }
        }

    }

    /** 
     * Allows hero to drink a strength potion if one was previously bought. 
     */
    public static void drinkStrengthPotion() {
        hero = characterList.get(0);
        
        if (hero.getStrengthPotionCondition()) {
            
            hero.setMaxDamage(50);
            hero.setStrengthPotionCondition(false);
            
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\nYou consumed a strength potion! Your max damage is now " + hero.getMaxDamage()); break;
                case "German": gameWindow.printToTerminal("\n\nSie haben ein Heiltrank getrunken. Ihre Schaden Kapabilität beträgt jetzt " + hero.getMaxDamage()); break;
            } 
            
        }
        else {
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\nYou don't have a potion to consume"); break;
                case "German": gameWindow.printToTerminal("\n\nSie haben kein Trank zu trinken");  break;
            }
        }

    }

    /**
     * Checks to see if the hero can move in a direction, and if an event will occur when 
     * the hero moves to a particular spot on the map. 
     * 
     * @param direction     The inputted direction that the hero wishes to move in 
     */
    public static void move(String direction) {
        hero = characterList.get(0);
        
        // TODO: try to see if you can change the background based on whether the hero has been there or not
        // gameWindow.getButtonGrid().get(gameWindow.getHeroLocationID()).setBackground(gameWindow.getColor("BLACK"));
        
        if (hero.canMove(direction) && !hero.hasEscaped() && !hero.isDead()) {
            
            hero.turnHealthDeduction();
            incrementTurnCounter();
            
            if (hero.hasEscaped() || hero.isDead()) {
                characterDeath = true;
                return;
            }
            
            if (!hero.isAnotherCharacterInSameRoom()) {
                
                // If alone in a room, the hero also has a random chance to find gold there 
                if (randomNumber() >= 5) {
                    
                    int randomGoldReward;
                    while (true) {
                        randomGoldReward = randomNumber();
                        if (randomGoldReward == 0) continue; else break;
                    }
                    
                    switch (language) {
                        case "English": gameWindow.printToTerminal("\n\nYou found " + randomGoldReward + " gold at " + hero.getXCord() + ", " + hero.getYCord() + " "); break;
                        case "German": gameWindow.printToTerminal("\n\nSie haben " + randomGoldReward + " Geld an " + hero.getXCord() + ", " + hero.getYCord() + " gefunden "); break;
                    }
                    
                    hero.setGoldValue(hero.getGoldValue() + randomGoldReward);

                }
                
            }
            else event();
            
        }
        else {
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\nYou can't move that way!"); break;
                case "German": gameWindow.printToTerminal("\n\nSie können sich nicht in diese Richtung bewegen!"); break;
            }
        }

    }

    /**
     * Handles various events that may occur while user moves through the dungeon.
     */
    public static void event() {
        hero = characterList.get(0);
        
        switch (characterList.get(hero.getCharacterInSameRoom()).getTypeValue()) {
            case 1:
                setIsInTrade(true);
                printMerchantInventory(true);
            break;
            case 2:
                
                /**
                 * If the hero has less than 75 health or if monsters cannot retreat, combat will
                 * be initiated. Otherwise, there is a 50% chance of monster retreat
                 */
                if (hero.getHealth() < 75 || !MainLogic.getCanRetreat() || randomNumber() % 2 == 0) {
                    initializeCombat();
                }
                else {
                    
                    retreat(2);
                    
                    // If the monster died while retreating, it will be removed.
                    if (characterList.get(hero.getCharacterInSameRoom()).isDead()) {
                        setCharacterDeath(true);
                        isGameOverAndPushMessageIfSo(); // TODO: Clean up IS needed here
                    }
                    else {
                        switch (language) {
                            case "English": gameWindow.printToTerminal("\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " ran away from the fight!"); break;
                            case "German": gameWindow.printToTerminal("\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " hat sich aus dem Kampf zurückgezogen!"); break;
                        }
                    }
                    
                    hero.setCharacterInSameRoom(-1);
                    setIsInCombat(false);
                    
                }
                
            break;
            case 3:
                
                // If the hero is in the same room as a mimic, there is a 50% chance that it will yield gold without a fight 
                if (randomNumber() % 2 == 0) {
                    switch (language) {
                        case "English": gameWindow.printToTerminal("\n\nAt " + hero.getXCord() + ", " + hero.getYCord() + " you find a treasure chest and inside you find " + chestReward + " gold"); break;
                        case "German": gameWindow.printToTerminal("\n\nAn " + hero.getXCord() + ", " + hero.getYCord() + " gibt es eine Schatzkiste. Drinnen haben Sie " + chestReward + " Geld gefunden"); break;
                    }
                    characterList.remove(characterList.get(hero.getCharacterInSameRoom()));
                }
                else {
                    
                    switch (language) {
                        case "English": gameWindow.printToTerminal("\n\nAt " + hero.getXCord() + ", " + hero.getYCord() + " you find a treasure chest and it attacks you!"); break;
                        case "German": gameWindow.printToTerminal("\n\nAn " + hero.getXCord() + ", " + hero.getYCord() + " gibt es eine Schatzkiste und sie greift Ihnen an!"); break;
                    }
                    
                    characterList.get(hero.getCharacterInSameRoom()).setTypeValue(2);
                    initializeCombat();
                    
                }

            break;
        }
    }

    /**
     * When this method is called, the game's combat state can be initialized.
     */
    private static void initializeCombat() {
        setIsInCombat(true);
        gameWindow.fight(true);
    }

    /**
     * Configures UI to trade display and prints out merchant info. 
     * 
     * @param firstTrade    Indicates whether this is the first time the hero traded
     */
    public static void printMerchantInventory(Boolean firstTrade) {
        String merchantName = characterList.get(hero.getCharacterInSameRoom()).getName();
        gameWindow.fullGameGraphicsUpdate();
        
        if (firstTrade) {
            switch (language) {
                case "English": 

                    gameWindow.printToTerminal("\n\n~ Trade Menu ~"); 
                    
                    if (characterList.get(hero.getCharacterInSameRoom()).getHealthPotionCondition()) {
                        gameWindow.printToTerminal("\n" + merchantName + " is selling a health potion for " + potionPrice + " gold");
                    }
                    else gameWindow.printToTerminal("\n" + merchantName + " does not have a health potion for sale\n");
                    
                    if (characterList.get(hero.getCharacterInSameRoom()).getStrengthPotionCondition()) {
                        gameWindow.printToTerminal("\n" + merchantName + " is selling a strength potion for " + potionPrice + " gold");
                    } 
                    else gameWindow.printToTerminal("\n" + merchantName + " does not have a strength potion for sale");
                    
                    gameWindow.printToTerminal("\nWhat would you like to buy? ");
                    
                break;
                case "German": 

                    gameWindow.printToTerminal("\n\n~ Handelsmenü ~"); 
                    
                    if (characterList.get(hero.getCharacterInSameRoom()).getHealthPotionCondition()) {
                        gameWindow.printToTerminal("\n" + merchantName + " verkauft einen Heiltrank für " + potionPrice + " Geld");
                    }
                    else gameWindow.printToTerminal("\n" + merchantName + " verkauft keine Heiltränke\n");
                    
                    if (characterList.get(hero.getCharacterInSameRoom()).getStrengthPotionCondition()) {
                        gameWindow.printToTerminal("\n" + merchantName + " verkauft einen Krafttrank für " + potionPrice + " Geld");
                    } 
                    else gameWindow.printToTerminal("\n" + merchantName + " verkauft keine Krafttränke");
                    
                    gameWindow.printToTerminal("\nWas möchten Sie kaufen?");
                    
                break;
            }
        }
    }

    /**
     * Handles the functionality for the hero or monster that wishes to retreat. 
     * 
     * @param type                   The character type (0 is hero, 2 is monster)
     * @return successfulRetreat     Indicates whether a retreat was successful 
     */
    public static boolean retreat(int characterType) {
        hero = characterList.get(0);
        Random random = new Random();
        boolean successfulRetreat = getCanRetreat();
        
        if (!successfulRetreat) {
            if (characterType == 0) {
                gameWindow.printToTerminal(new LanguageTranslation("\n\nYou can't retreat from this battle!", "\n\nSie können aus diesen Kampf nicht zurückziehen!").extrapolate());
                gameWindow.playGame(false);
            }
            return successfulRetreat;
        }
        
        if (characterType == 0) {
            
            // While retreating, the hero will take a varied health deduction based on the monster's max damage
            int damage = random.nextInt(characterList.get(hero.getCharacterInSameRoom()).getMaxDamage());
            int characterHealth = hero.getHealth() - damage;
            hero.setHealth(characterHealth);
            
            switch (language) {
                case "English": gameWindow.printToTerminal("\n\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " hits you for " + damage + " damage"); break;
                case "German": gameWindow.printToTerminal("\n\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " hat Sie für " + damage + " Schadenspunkte geschlagen"); break;
            }
            
            // If the hero is dead, the game will end. Otherwise, the hero is informed of the successful escape
            if (hero.isDead()) { 
                characterDeath = true; 
                isGameOverAndPushMessageIfSo(); // No clean up needed
            }
            else {
                switch (language) {
                    case "English": gameWindow.printToTerminal("\n\nYou retreated from battle with " + hero.getHealth() + " health remaining"); break;
                    case "German": gameWindow.printToTerminal("\n\nSie haben sich aus diesem Kampf zurückgezogen mit " + hero.getHealth() + " Gesundheitspunkte übrig"); break;
                }
                setIsInCombat(false);
            }
            
        }
        else {
            
            // Logic for a monster retreat from a battle
            int damage = random.nextInt(hero.getMaxDamage());
            int characterHealth = characterList.get(hero.getCharacterInSameRoom()).getHealth() - damage;
            characterList.get(hero.getCharacterInSameRoom()).setHealth(characterHealth);
            
            switch (language) {
                case "English": 
                    gameWindow.printToTerminal("\n\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " is retreating from " + hero.getXCord() + ", " + hero.getYCord() + "!");
                    gameWindow.printToTerminal("\nYou hit " + characterList.get(hero.getCharacterInSameRoom()).getName() + " for " + damage + " damage");
                break;
                case "German": 
                    gameWindow.printToTerminal("\n\n" + characterList.get(hero.getCharacterInSameRoom()).getName() + " zurückzieht aus " + hero.getXCord() + ", " + hero.getYCord() + "!");
                    gameWindow.printToTerminal("\nSie haben " + characterList.get(hero.getCharacterInSameRoom()).getName() + " für " + damage + " Schadenspunkte geschlagen");
                break;
            }
            
        }
        return successfulRetreat;
    }
}
