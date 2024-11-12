import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads a save file with all game information and loads a new game from that info. 
 * 
 * @author Blake Payne
 * @since  08.19.2024
 */
public class SaveFileReader {

    private ArrayList<Character> characterList = new ArrayList<Character>();
    private ArrayList<String> saveFiles;
    private File[] arrayOfFiles;
    private int dungeonSizeValue = -1;
    private String targetFile = "";

    public int getSaveFileNumberTotal() {
        saveFiles = new ArrayList<String>(); 
        File folder = new File("SaveFiles//");
        arrayOfFiles = folder.listFiles();
        return arrayOfFiles.length;
    }

    public Integer getDungeonSizeValue() {
        return dungeonSizeValue;
    }

    public void setDungeonSizeValue(int value) {
        dungeonSizeValue = value;
    }

    public String getSaveFileString() {
        return targetFile;
    }

    /**
     * Prints out the save files in the "SaveFiles" folder. Informs user if there are no save files. 
     */
    public void printSaveLoadingMenu() {
        if (getSaveFileNumberTotal() > 0) {
            MainLogic.getGameWindow().printToTerminal(new LanguageTranslation("\n\n~ Save Menu ~", "\n\n~ Speicherdateien Menü ~").extrapolate());
            for (int i = 0; i < getSaveFileNumberTotal(); i++) {
                MainLogic.getGameWindow().printToTerminal("\n" + (i + 1) + ": " + arrayOfFiles[i].getName());
                saveFiles.add(arrayOfFiles[i].getName());
            }
            
            switch (MainLogic.getLanguage()) {
                case "English": MainLogic.getGameWindow().printToTerminal("\n\nWhich save file would you like to load from (Select from 1-" + getSaveFileNumberTotal() + ")?"); break;
                case "German": MainLogic.getGameWindow().printToTerminal("\n\nWelche Speicherdatei möchten Sie laden? (Wählen Sie 1-" + getSaveFileNumberTotal() + ")?"); break;
            }
        }
        else {
            switch (MainLogic.getLanguage()) {
                case "English": MainLogic.getGameWindow().printToTerminal("\n\nThere are no saves to load from. Make sure to save regularly!"); break;
                case "German": MainLogic.getGameWindow().printToTerminal("\n\nEs gibt keine Speicherdateien, die Sie laden können. Stellen Sie mehr davon her!"); break;
            }
        }
    }
    
    /**
     * Loads a new list of characters from a given save file index. 
     * 
     * @param index     Index of the save file
     * @return          New list of characters made from save file 
     */
    public ArrayList<Character> loadFromSave(Integer index) {
        targetFile = arrayOfFiles[index - 1].getName();
        fileReader(targetFile);
        return characterList;
    }

    private Integer toInt(String str) {
        Integer integer = Integer.parseInt(str);
        return integer;
    }

    private Boolean toBoolean(String str) {
        Boolean booleanValue = Boolean.parseBoolean(str);
        return booleanValue;
    }

    @SuppressWarnings("unused")
    private void fileReader(String fileName) throws IndexOutOfBoundsException {
        try {
            int dungeonSize = 0, numOfCharacters = 0, turnCounter = 0, characterInSameRoom = -1, potionTurnCounter = 0;
            String fileNameWithPath = "SaveFiles//" + fileName;
            BufferedReader reader = new BufferedReader(new FileReader(fileNameWithPath));
            String line = reader.readLine();
            Boolean canRetreat;
            
            // Gets the value of dungeonSize, numOfCharacters, turnCounter, and characterInSameRoom from the save file 
            while (!line.contains("~~~ END OF DUNGEON INFO ~~~")) {
                String attributeType = attributeGetter(line, 'l');
                
                if (attributeType.contains("numOfCharacters")) {
                    String attributeValue = attributeGetter(line, 'r');
                    numOfCharacters = toInt(attributeValue);
                    characterAdder(numOfCharacters);
                }
                else if (attributeType.contains("dungeonSize")) {
                    String attributeValue = attributeGetter(line, 'r');
                    dungeonSize = toInt(attributeValue);
                    attributeUpdater("dungeonSize", attributeValue, 0);
                }
                else if (attributeType.contains("turnCounter")) {
                    String attributeValue = attributeGetter(line, 'r');
                    turnCounter = toInt(attributeValue);
                    attributeUpdater("turnCounter", attributeValue, 0);
                }
                else if (attributeType.equals("characterInSameRoom")) {
                    String attributeValue = attributeGetter(line, 'r');
                    characterInSameRoom = toInt(attributeValue);
                    attributeUpdater("characterInSameRoom", attributeValue, 0);
                }
                else if (attributeType.equals("potionTurnCounter")) {
                    String attributeValue = attributeGetter(line, 'r');
                    potionTurnCounter = toInt(attributeValue);
                    attributeUpdater("potionTurnCounter", attributeValue, 0);
                }
                else if (attributeType.equals("canRetreat")) {
                    String attributeValue = attributeGetter(line, 'r');
                    canRetreat = toBoolean(attributeValue);
                    attributeUpdater("canRetreat", attributeValue, 0);
                }
                line = reader.readLine();
            }
            
            System.out.println("dungeonSize: " + dungeonSize + ", numOfCharacters: " + numOfCharacters + ", turnCounter: " + turnCounter + ", characterInSameRoom: " + characterInSameRoom);
            reader.close();
            
            // Reopens BufferedReader to evaluate the characters' info in the file 
            reader = new BufferedReader(new FileReader(fileNameWithPath));
            line = reader.readLine();
            
            // Evaluates the rest of the characters' info and restores it in the character list
            for (int i = 0; i < numOfCharacters; i++) {
                while (!line.contains("~~~ END OF CHARACTER INFO ~~~")) {
                    String attributeType = attributeGetter(line, 'l');
                    
                    if (attributeType.contains("name")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("name", attributeValue, i);
                    } 
                    else if (attributeType.equals("health")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("health", attributeValue, i);
                    }
                    else if (attributeType.contains("maxDamage")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("maxDamage", attributeValue, i);
                    } 
                    else if (attributeType.contains("xCord")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("xCord", attributeValue, i);
                    } 
                    else if (attributeType.contains("yCord")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("yCord", attributeValue, i);
                    } 
                    else if (attributeType.contains("gold")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("gold", attributeValue, i);
                    } 
                    else if (attributeType.contains("type")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("type", attributeValue, i);
                    } 
                    else if (attributeType.equals("healthPotionCondition")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("healthPotionCondition", attributeValue, i);
                    } 
                    else if (attributeType.contains("strengthPotionCondition")) {
                        String attributeValue = attributeGetter(line, 'r');
                        attributeUpdater("strengthPotionCondition", attributeValue, i); 
                    }
                    line = reader.readLine();
                }
                line = reader.readLine();
            }
            reader.close();
            characterRemover(numOfCharacters);
            
        }
        catch (FileNotFoundException e) {
            System.out.println("That save file was not found");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // If the current game has less characters than the loaded save file, more "empty" characters are added
    private void characterAdder(int numOfCharacters) {
        while (true) {
            if (characterList.size() < numOfCharacters) {
                Character gameCharacter = new Character();
                characterList.add(gameCharacter);
            } 
            else { break; }
        }
    }

    private void characterRemover(int numOfCharacters) { 
        if (characterList.size() > numOfCharacters) {
            for (int i = numOfCharacters; i < characterList.size(); i++) {
                characterList.remove(i);
            }
            characterRemover(numOfCharacters);
        }
    }

    private String attributeGetter(String line, char letter) {
        int colonIndex = line.indexOf(":");
        String attributeValue = "";
        
        try {
            if (letter == ('l')) {
                attributeValue = line.substring(0, colonIndex);
            } else if (letter == ('r')) {
                attributeValue = line.substring(colonIndex, line.length());
            }
        }
        catch (IndexOutOfBoundsException e) {
            if (line.contains("~~~ END OF CHARACTER INFO ~~~")) {
                return "~~~ END OF CHARACTER INFO ~~~";
            } 
            else if (line.contains("~~~ END OF DUNGEON INFO ~~~")) {
                return "~~~ END OF DUNGEON INFO ~~~";
            }
            else if (line.contains("~~~ END OF SAVE FILE ~~~")) {
                return "~~~ END OF SAVE FILE ~~~";
            }
            else {
                return "~~~ ERROR STRING ~~~";
            }
        }
        
        String[] tempStringArray = attributeValue.split(":");
        for (String element: tempStringArray) { attributeValue = element; }
        attributeValue = attributeValue.trim();
        return attributeValue;
    }

    private void attributeUpdater(String attributeType, String attributeValue, int index) {
        int attributeIntValue;
        Boolean attributeBoolValue;
        switch (attributeType) {
            case "dungeonSize":
                attributeIntValue = toInt(attributeValue);
                characterList.get(0).setDungeonSize(attributeIntValue);
                setDungeonSizeValue(attributeIntValue);
                break;
            case "turnCounter":
                attributeIntValue = toInt(attributeValue);
                characterList.get(0).setTurnCounterValue(attributeIntValue);
                break;
            case "characterInSameRoom":
                attributeIntValue = toInt(attributeValue);
                characterList.get(0).setCharacterInSameRoom(attributeIntValue);
                break;
            case "potionTurnCounter":
                attributeIntValue = toInt(attributeValue);
                characterList.get(0).setPotionTurnCounter(attributeIntValue);
                break;
            case "canRetreat":
                attributeBoolValue = toBoolean(attributeValue);
                characterList.get(0).setCanRetreat(attributeBoolValue);
                break;
            case "name":
                characterList.get(index).setName(attributeValue);
                break;
            case "health":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setHealth(attributeIntValue);
                break;
            case "maxDamage":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setMaxDamage(attributeIntValue);
                break;
            case "xCord":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setXCord(attributeIntValue);
                break;
            case "yCord":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setYCord(attributeIntValue);
                break;
            case "gold":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setGoldValue(attributeIntValue);
                break;
            case "type":
                attributeIntValue = toInt(attributeValue);
                characterList.get(index).setTypeValue(attributeIntValue);
                break;
            case "healthPotionCondition":
                attributeBoolValue = toBoolean(attributeValue);
                characterList.get(index).setHealthPotionCondition(attributeBoolValue);
                break;
            case "strengthPotionCondition":
                attributeBoolValue = toBoolean(attributeValue);
                characterList.get(index).setStrengthPotionCondition(attributeBoolValue);
                break;
        }
    }
}
