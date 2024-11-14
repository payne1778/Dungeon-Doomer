import java.util.ArrayList;
import java.util.Random;

/**
 * Holds private fields and some logic for characters. 
 * 
 * @author Blake Payne
 * @since  06.12.2024
 */
public class Character {
    
    private String name;              // Name of a character
    private Integer health;           // Health value of a character
    private Integer dungeonSize;      // Integer size of a dungeon (stored in the Hero object)
    private Integer maxDamage;        // The value of maximum damage a character can do
    private Integer xCord;            // The X coordinate of a character
    private Integer yCord;            // The Y coordinate of a character
    private Integer gold;             // Amount of gold a character possesses
    private Integer type;             // Character type (0 = hero, 1 = merchant, 2 = monster, 3 = mimic (not hostile), -1 = n/a)
    
    private Boolean healthPotionCondition;      // Indicates if the hero can drink a health potion or if a merchant has one to sell
    private Boolean strengthPotionCondition;    // Indicates if the hero can drink a strength potion or if a merchant has one to sell
    private Integer characterInSameRoom = -1;   // Holds the index of the character in the same room as the hero
    private Integer turnCounter = 0;            // Tracks the turn number of the game (stored in the Hero object)
    private Character hero;                     // Represents the hero object and holds all of the hero's information 
    
    private ArrayList<Character> characterList = MainLogic.getCharacterList(); // Character arraylist holds all information about each character
    
    /**
     * A no argument constructor that assigns most fields to zero or false. This character will
     * be removed from the list of characters when checkForGameOver() is called. 
     */
    public Character() {
        this.health = 0;
        this.type = -1;
        this.maxDamage = 0;
        this.xCord = 0;
        this.yCord = 0;
        this.gold = 0;
        this.healthPotionCondition = false;
        this.strengthPotionCondition = false;
    }

    /**
     * A constructor for initialization of a Character class object with appropriate parameters
     * 
     * @param name                      The name of the character
     * @param health                    The health of the character
     * @param type                      The type of the character
     * @param maxDamage                 The max damage of the character
     * @param xCord                     The X coordinate of the character
     * @param yCord                     The Y coordinate of the character
     * @param gold                      The gold amount that the character has
     * @param healthPotionCondition     The indicator for possession of a health potion
     * @param strengthPotionCondition   The indicator for possession of a strength potion
     */
    public Character(String name, Integer health, Integer type, Integer maxDamage, Integer xCord, Integer yCord, 
                Integer gold, Boolean healthPotionCondition, Boolean strengthPotionCondition) {
        this.name = name;
        this.health = health;
        this.type = type;
        this.maxDamage = maxDamage;
        this.xCord = xCord;
        this.yCord = yCord;
        this.gold = gold;
        this.healthPotionCondition = healthPotionCondition;
        this.strengthPotionCondition = strengthPotionCondition;
    }

    // Getters and Setters for Character class
    public void setDungeonSize(Integer dungeonSize) {
        this.dungeonSize = dungeonSize;
    }

    public Integer getDungeonSize() {
        return dungeonSize;
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public Integer getHealth() { 
        return health; 
    }

    public void setHealth(Integer health) { 
        this.health = health;
    }

    public Integer getTypeValue() { 
        return type; 
    }

    public void setTypeValue(Integer type) { 
        this.type = type; 
    }

    public Integer getMaxDamage() { 
        return maxDamage;
    }

    public void setMaxDamage(Integer maxDamage) { 
        this.maxDamage = maxDamage; 
    }

    public Integer getXCord() { 
        return xCord; 
    }

    public void setXCord(Integer xCord) { 
        if (xCord < 0 || xCord >= MainLogic.getDungeonSize()) {
            MainLogic.getGameWindow().logAndNotifyIllegalState("X Coordinate cannot be negative or equal to/larger than the size of the dungeon");
        }
        this.xCord = xCord;
    }

    public Integer getYCord() { 
        return yCord;
    }

    public void setYCord(Integer yCord) { 
        if (yCord < 0 || yCord >= MainLogic.getDungeonSize()) {
            MainLogic.getGameWindow().logAndNotifyIllegalState("Y Coordinate cannot be negative or equal to/larger than the size of the dungeon");
        }
        this.yCord = yCord;
    }

    public void setXYCords(Integer xCord, Integer yCord) { 
        setXCord(xCord);
        setYCord(yCord);
    }

    public Integer getGoldValue() { 
        return gold; 
    }

    public void setGoldValue(Integer gold) { 
        this.gold = gold;
    }

    public void setTurnCounterValue(Integer turnCounter) {
        this.turnCounter = turnCounter;
    }

    public Integer getTurnCounterValue() {
        return turnCounter;
    }

    public Boolean getHealthPotionCondition() { 
        return healthPotionCondition;
    }

    public void setHealthPotionCondition(Boolean healthPotionCondition) { 
        this.healthPotionCondition = healthPotionCondition;
    }

    public Boolean getStrengthPotionCondition() { 
        return strengthPotionCondition;
    }

    public void setStrengthPotionCondition(Boolean strengthPotionCondition) { 
        this.strengthPotionCondition = strengthPotionCondition;
    }

    public String getPotionMessage(Boolean inPossession) { 
        String message = "";
        switch (MainLogic.getLanguage()) {
            case "English": if(inPossession) message = "Owned"; else message = "None"; break;
            case "German": if(inPossession) message = "Besitzt"; else message = "Kein"; break;
        }
        return message;
    }

    public void setCharacterInSameRoom(Integer characterInSameRoom) {
        this.characterInSameRoom = characterInSameRoom;
    }

    public Integer getCharacterInSameRoom() {
        return characterInSameRoom;
    }

    public Integer getPotionTurnCounter() {
        return MainLogic.getPotionTurnCounter();
    }

    public void setPotionTurnCounter(Integer potionTurnCounter) {
        MainLogic.setPotionTurnCounter(potionTurnCounter);
    }

    public Boolean getCanRetreat() {
        return MainLogic.getCanRetreat();
    }

    public void setCanRetreat(Boolean retreatBoolean) {
        MainLogic.setCanRetreat(retreatBoolean);
    }

    /**
     * Checks if a character is dead. 
     * 
     * @return true if health is less than 0, and false otherwise 
     */
    public Boolean isDead() { 
        if (health > 0) return false; else return true; 
    }

    /**
     * Checks if the hero has escaped the dungeon. 
     * 
     * @return true if escaped, and false otherwise
     */
    public Boolean hasEscaped() {
        if ((xCord == (dungeonSize - 1)) && (yCord == (dungeonSize - 1))) {
            return true;
        } 
        else return false; 
    }

    /**
    * This method is used to lower the hero's health each turn. Sets health to zero if negative. 
    */
    public void turnHealthDeduction() {
        health -= 2;
        if (health < 0) setHealth(0);
    }

    /**
    * Checks to see if the hero and any monsters are in the same room
    * 
    * @return true if they are in the same room, and false otherwise
    */
    public Boolean isAnotherCharacterInSameRoom() {
        characterList = MainLogic.getCharacterList();
        hero = characterList.get(0);
        this.setCharacterInSameRoom(-1);
        
        for (int i = 1; i < characterList.size(); i++) {
            
            // If the hero and a monster are in the same room, the characterInSameRoom variable will be updated to the index of that monster in characterList
            if ((hero.getXCord() == characterList.get(i).getXCord()) && (hero.getYCord() == characterList.get(i).getYCord())) {
                setCharacterInSameRoom(i);
                return true;
            }
            
        }
        return false;
    }

    /**
    * Checks to see if any monsters are adjacent to the hero
    * 
    * @return smellCounter; the counter that tracks how many adjacent monsters there are
    */
    public Integer inAdjacentRoom() {
        Integer smellCounter = 0;
        characterList = MainLogic.getCharacterList();
        hero = characterList.get(0);
        
        try {
            if (characterList.size() == 1) {
                smellCounter = 0;
            } 
            else if (characterList.size() == 2) {
                if (characterList.get(1).getTypeValue() == 1) {
                    smellCounter = 0;
                }
                else {
                    smellCounter += smellCounterIncrementor(1);
                }
            } 
            else if ((characterList.get(1).getTypeValue() == 1) && (characterList.get(2).getTypeValue() != 1)) { 
                // TODO: add comment on what this does 
                for (int i = 2; i < characterList.size(); i++) {
                    smellCounter += smellCounterIncrementor(i);
                }
            } 
            else if ((characterList.get(2).getTypeValue() == 1) && (characterList.get(3).getTypeValue() != 1)) {
                // TODO: add comment on what this does 
                for (int i = 3; i < characterList.size(); i++) {
                    smellCounter += smellCounterIncrementor(i);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(MainLogic.getPrintStream());
            MainLogic.getGameWindow().pushMessage("\nAn unknown error has occurred when hero smelled for monsters.\n");
        }
        return smellCounter;
    }

    /**
     * Functionality for smell counter in inAdjacentRoom() method. 
     * 
     * @param index     The index of the monster in the characterList ArrayList 
     * @return          smellCounter; the counter that tracks the number of adjacent monsters
     */
    private Integer smellCounterIncrementor(Integer index) {
        Integer smellCounter = 0;
        characterList = MainLogic.getCharacterList();
        hero = characterList.get(0);
        
        // Checks the change in x coordinates of the hero and every monster to see any are next to each other
        Integer xCordChecker = Math.abs(hero.getXCord() - characterList.get(index).getXCord());
        Integer yCordChecker = Math.abs(hero.getYCord() - characterList.get(index).getYCord());
        
        // If the difference between the x/y coordinate is 1 and the other x/y coordinate is 0, then that monster is adjacent to the hero
        if ((xCordChecker == 1 && yCordChecker == 0) || (xCordChecker == 0 && yCordChecker == 1)) {
            smellCounter++;
        }
        
        return smellCounter;
    }

    /**
    * Deals damage to hero and monster in combat. Returns boolean value based on character death.
    *
    * @return if a character dies, true is returned. Otherwise, false will be returned
    */
    public void hitCharacter() {
        Random random = new Random();
        Integer damage; Integer characterHealth;
        characterList = MainLogic.getCharacterList();
        hero = characterList.get(0);
        
        // Logic for when the hero hits a monster in combat 
        damage = random.nextInt(hero.getMaxDamage());
        characterHealth = characterList.get(hero.getCharacterInSameRoom()).getHealth() - damage;
        characterList.get(hero.getCharacterInSameRoom()).setHealth(characterHealth);
        
        switch (MainLogic.getLanguage()) {
            case "English": MainLogic.getGameWindow().printToTerminal("\n\nYou hit " + characterList.get(hero.getCharacterInSameRoom()).getName() + " for " + damage + " damage\n"); break;
            case "German": MainLogic.getGameWindow().printToTerminal("\n\nSie haben " + characterList.get(hero.getCharacterInSameRoom()).getName() + " für " + damage + " Schadenspunkte geschlagen\n"); break;
        }
        
        // If the monster dies, it will be removed, but the battle will continue if it is still alive
        if (characterList.get(hero.getCharacterInSameRoom()).isDead()) { 
            MainLogic.setCharacterDeath(true);
        }
        else { 
            // Logic for when a monster hits hero in combat 
            damage = random.nextInt(characterList.get(hero.getCharacterInSameRoom()).getMaxDamage());
            characterHealth = hero.getHealth() - damage;
            hero.setHealth(characterHealth);
            
            switch (MainLogic.getLanguage()) {
                case "English": MainLogic.getGameWindow().printToTerminal(characterList.get(hero.getCharacterInSameRoom()).getName() + " hits you for " + damage + " damage"); break;
                case "German": MainLogic.getGameWindow().printToTerminal(characterList.get(hero.getCharacterInSameRoom()).getName() + " hat Sie für " + damage + " Schadenspunkte geschlagen"); break;
            }
            
            // If the hero dies, the game will end, but the battle will continue if the hero is still alive
            if (hero.isDead()) MainLogic.setCharacterDeath(true);
        }
    }

    /**
    * Evaluates if the user's input is a valid direction that the hero can move.
    * 
    * @param direction is a String parameter that represents the user input for direction
    * @return if the direction is valid, true is returned. Otherwise, false is returned
    */
    public Boolean canMove(String direction) {
        Boolean validDirection = null; 
        if (direction.contains("north") || direction.contains("nord")) {
            if (yCord == 0) {
                validDirection = false;
            } 
            else {
                yCord -= 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("south") || direction.contains("süd") || direction.contains("sued")) {
            if (yCord == (dungeonSize - 1)) {
                validDirection = false;
            } 
            else {
                yCord += 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("east") || direction.contains("ost")) {
            if (xCord == (dungeonSize - 1)) {
                validDirection = false;
            } 
            else {
                xCord += 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("west")) {
            if (xCord == 0) {
                validDirection = false;
            } 
            else {
                xCord -= 1;
                validDirection = true;
            }
        } 
        else MainLogic.getGameWindow().logAndNotifyIllegalState("An valid direction was imputed, but the hero could not be moved.");
        
        return validDirection;
    }
    
    /**
    * Overrides the built-in toString() method to print out the status of a hero/monster
    */
    @Override 
    public String toString() {
        return name + " at " + xCord + ", " + yCord + " with " + health + " health and " + gold + " gold";
    }
}
