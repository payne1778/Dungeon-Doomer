import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Creates a "save" by storing all game information into a separate text file. 
 * 
 * @author Blake Payne
 * @since  08.19.2024
 */
public class SaveFileMaker {

    private String fileName = ("Save File " + MainLogic.getCurrentTimeStamp() + ".txt");

    /**
     * Creates a new save file and writes both dungeon and character info to it.  
     */
    public void createSave() {
        File f = new File("SaveFiles//" + fileName);
        
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(f));
            Character hero = MainLogic.getCharacterList().get(0);
            
            output.write("numOfCharacters: " + MainLogic.getCharacterList().size());
            output.write("\n");
            
            Integer dungeonSize = hero.getDungeonSize();
            output.write("dungeonSize: " + dungeonSize);
            output.write("\n");
            
            Integer turnCounter = hero.getTurnCounterValue();
            output.write("turnCounter: " + turnCounter);
            output.write("\n");
            
            Integer characterInSameRoom = hero.getCharacterInSameRoom();
            output.write("characterInSameRoom: " + characterInSameRoom);
            output.write("\n");
            
            Integer potionTurnCounter = MainLogic.getPotionTurnCounter();
            output.write("potionTurnCounter: " + potionTurnCounter);
            output.write("\n");
            
            Boolean canRetreat = MainLogic.getCanRetreat();
            output.write("canRetreat: " + canRetreat);
            output.write("\n");
            
            output.write("~~~ END OF DUNGEON INFO ~~~");
            output.write("\n");
            
            // The following for loop writes each character's info to the file 
            for (int i = 0; i < MainLogic.getCharacterList().size(); i++) {
                String name = MainLogic.getCharacterList().get(i).getName();
                output.write("name: " + name);
                output.write("\n");
                
                Integer health = MainLogic.getCharacterList().get(i).getHealth();
                output.write("health: " + health);
                output.write("\n");
                
                Integer maxDamage = MainLogic.getCharacterList().get(i).getMaxDamage();
                output.write("maxDamage: " + maxDamage);
                output.write("\n");
                
                Integer xCord = MainLogic.getCharacterList().get(i).getXCord();
                output.write("xCord: " + xCord);
                output.write("\n");
                
                Integer yCord = MainLogic.getCharacterList().get(i).getYCord();
                output.write("yCord: " + yCord);
                output.write("\n");
                
                Integer gold = MainLogic.getCharacterList().get(i).getGoldValue();
                output.write("gold: " + gold);
                output.write("\n");
                
                Integer type = MainLogic.getCharacterList().get(i).getTypeValue();
                output.write("type: " + type);
                output.write("\n");
                
                Boolean healthPotionCondition = MainLogic.getCharacterList().get(i).getHealthPotionCondition();
                output.write("healthPotionCondition: " + healthPotionCondition);
                output.write("\n");
                
                Boolean strengthPotionCondition = MainLogic.getCharacterList().get(i).getStrengthPotionCondition();
                output.write("strengthPotionCondition: " + strengthPotionCondition);
                output.write("\n");
                
                output.write("~~~ END OF CHARACTER INFO ~~~");
                output.write("\n");
            }
            
            output.write("~~~ END OF SAVE FILE ~~~");
            output.close();
            switch (MainLogic.getLanguage()) {
                case "English": MainLogic.getGameWindow().printToTerminal("\n\nYour game has been saved as: " + fileName); break;
                case "German": MainLogic.getGameWindow().printToTerminal("\n\nDas Spiel ist als " + fileName + " gespeichert "); break;
            }
        }
        catch (IndexOutOfBoundsException ioobe) {
            MainLogic.getGameWindow().pushMessage("Error: Your game could not be saved at this time. (MISSING CHARACTER(S) INFO)");
            ioobe.printStackTrace();
        }
        catch (Exception ex) {
            MainLogic.getGameWindow().pushMessage("Error: Your game could not be saved at this time. (UNKNOWN ERROR)");
            ex.printStackTrace();
        }
    }
}