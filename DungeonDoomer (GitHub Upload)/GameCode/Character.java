import java.util.ArrayList;

/**
 * Holds private fields and some logic for characters. 
 * 
 * @author Blake Payne
 * @since  12.29.2024
 */
public class Character {
    
    private String name = "";                   // Name of a character
    private int health = 0;                     // Health value of a character
    private int maxDamage = 0;                  // The value of maximum damage a character can do
    private int xCord = -1;                     // The X coordinate of a character
    private int yCord = -1;                     // The Y coordinate of a character
    private int gold = 0;                       // Amount of gold a character possesses
    private int type = -1;                      // Character type (0 = hero, 1 = merchant, 2 = monster, 3 = mimic (not hostile), -1 = n/a)
    private boolean hasHealthPotion = false;    // Indicates if the hero can drink a health potion or if a merchant has one to sell
    private boolean hasStrengthPotion = false;  // Indicates if the hero can drink a strength potion or if a merchant has one to sell

    private int characterInSameRoomIndex = -1;  // Holds the index of the character in the same room as the hero
    private int dungeonSize = 0;                // Integer size of a dungeon (stored in the Hero object)
    private int turnCounter = 0;                // Tracks the turn number of the game (stored in the Hero object)
    private Character hero;                     // Represents the hero object and holds all the hero's information
    
    private ArrayList<Character> characterList = MainLogic.getCharacterList(); // Character arraylist holds all information about each character
    
    /**
     * The default character constructor. This character will be removed from the list when any character dies.
     */
    public Character() {
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
     * @param hasHealthPotion           The indicator for possession of a health potion
     * @param hasStrengthPotion         The indicator for possession of a strength potion
     */
    public Character(String name, int health, int type, int maxDamage, int xCord, int yCord, int gold,
                     boolean hasHealthPotion, boolean hasStrengthPotion) {
        this.name = name;
        this.health = health;
        this.type = type;
        this.maxDamage = maxDamage;
        this.xCord = xCord;
        this.yCord = yCord;
        this.gold = gold;
        this.hasHealthPotion = hasHealthPotion;
        this.hasStrengthPotion = hasStrengthPotion;
    }

    // Getters and Setters for Character class
    public void setDungeonSize(int dungeonSize) {
        this.dungeonSize = dungeonSize;
    }

    public int getDungeonSize() {
        return dungeonSize;
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public int getHealth() {
        return health; 
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getTypeValue() {
        return type; 
    }

    public void setTypeValue(int type) {
        this.type = type; 
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage; 
    }

    public int getXCord() {
        return xCord; 
    }

    public void setXCord(int xCord) {
        if (xCord < 0 || xCord >= MainLogic.getDungeonSize()) {
            MainLogic.getGameWindow().logAndNotifyIllegalState("X Coordinate cannot be negative or equal to/larger than the size of the dungeon");
        }
        this.xCord = xCord;
    }

    public int getYCord() {
        return yCord;
    }

    public void setYCord(int yCord) {
        if (yCord < 0 || yCord >= MainLogic.getDungeonSize()) {
            MainLogic.getGameWindow().logAndNotifyIllegalState("Y Coordinate cannot be negative or equal to/larger than the size of the dungeon");
        }
        this.yCord = yCord;
    }

    public void setXYCords(int xCord, int yCord) {
        setXCord(xCord);
        setYCord(yCord);
    }

    public int getGoldValue() {
        return gold; 
    }

    public void setGoldValue(int gold) {
        this.gold = gold;
    }

    public void setTurnCounterValue(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public int getTurnCounterValue() {
        return turnCounter;
    }

    public boolean getHasHealthPotion() {
        return hasHealthPotion;
    }

    public void setHasHealthPotion(boolean hasHealthPotion) {
        this.hasHealthPotion = hasHealthPotion;
    }

    public boolean getHasStrengthPotion() {
        return hasStrengthPotion;
    }

    public void setHasStrengthPotion(boolean hasStrengthPotion) {
        this.hasStrengthPotion = hasStrengthPotion;
    }

    public String getPotionMessage(boolean inPossession) {
        String message = "";
        switch (MainLogic.getLanguage()) {
            case "English": if(inPossession) message = "Owned"; else message = "None"; break;
            case "German": if(inPossession) message = "Besitzt"; else message = "Kein"; break;
        }
        return message;
    }

    public void setCharacterInSameRoomIndex(int characterInSameRoomIndex) {
        this.characterInSameRoomIndex = characterInSameRoomIndex;
    }

    public int getCharacterInSameRoomIndex() {
        return characterInSameRoomIndex;
    }

    public int getPotionTurnCounter() {
        return MainLogic.getPotionTurnCounter();
    }

    public void setPotionTurnCounter(int potionTurnCounter) {
        MainLogic.setPotionTurnCounter(potionTurnCounter);
    }

    public boolean getCanRetreat() {
        return MainLogic.getCanRetreat();
    }

    public void setCanRetreat(boolean retreatBoolean) {
        MainLogic.setCanRetreat(retreatBoolean);
    }

    /**
     * Checks if a character is dead. 
     * 
     * @return true if health is less than 0, and false otherwise 
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Checks if the hero has escaped the dungeon. 
     * 
     * @return true if escaped, and false otherwise
     */
    public boolean hasEscaped() {
        return (xCord == (dungeonSize - 1)) && (yCord == (dungeonSize - 1));
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
    public boolean isAnotherCharacterInSameRoom() {
        characterList = MainLogic.getCharacterList();
        hero = characterList.getFirst();
        this.setCharacterInSameRoomIndex(-1);
        
        for (int i = 1; i < characterList.size(); i++) {
            
            // If the hero and a monster are in the same room, the characterInSameRoom variable will be updated to the index of that monster in characterList
            if ((hero.getXCord() == characterList.get(i).getXCord()) && (hero.getYCord() == characterList.get(i).getYCord())) {
                setCharacterInSameRoomIndex(i);
                return true;
            }
            
        }
        return false;
    }

    /**
    * Checks to see if any monsters are adjacent to the hero
    * 
    * @return smellCounter: the counter that tracks how many adjacent monsters there are
    */
    public int getSmellCounter() {
        characterList = MainLogic.getCharacterList();
        int smellCounter = 0;

        try {
            if (characterList.size() <= 2) {
                // If there are only two characters, the following will check if the second character is adjacent to the hero
                smellCounter += smellCounterIncrementor(1);
            } 
            else if ((characterList.get(1).getTypeValue() == 1) && (characterList.get(2).getTypeValue() != 1)) { 
                // If the second character in the character list is a merchant, but the third character is not,
                // the following will check all characters, starting with the third, to see if any are adjacent to the hero
                for (int i = 2; i < characterList.size(); i++) {
                    smellCounter += smellCounterIncrementor(i);
                }
            } 
            else if ((characterList.get(2).getTypeValue() == 1) && (characterList.get(3).getTypeValue() != 1)) {
                // If the third character in the character list is a merchant, but the fourth character is not,
                // the following will check all characters, starting with the fourth, to see if any are adjacent to the hero
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
     * @param index     The index of the monster in the characterList arrayList
     * @return          smellCounter: the counter that tracks the number of adjacent monsters
     */
    private int smellCounterIncrementor(int index) {
        characterList = MainLogic.getCharacterList();
        hero = characterList.getFirst();
        int smellCounter = 0;

        // Checks the change in x coordinates of the hero and every monster to see any are next to each other
        int xCordChecker = Math.abs(hero.getXCord() - characterList.get(index).getXCord());
        int yCordChecker = Math.abs(hero.getYCord() - characterList.get(index).getYCord());
        
        // If the difference between the x/y coordinate is 1 and the other x/y coordinate is 0, then that monster is adjacent to the hero
        if ((xCordChecker == 1 && yCordChecker == 0) || (xCordChecker == 0 && yCordChecker == 1)) {
            smellCounter++;
        }
        
        return smellCounter;
    }

    /**
    * Deals damage to hero and monster in combat. Returns boolean value based on character death.
    */
    public void hitCharacter() {
        Character monster = MainLogic.getCharacterList().get(hero.getCharacterInSameRoomIndex());
        hero = characterList.getFirst();

        // Logic for when the hero hits a monster in combat 
        int damageToMonster = MainLogic.getRandomNumber(hero.getMaxDamage());
        int newMonsterHealth = monster.getHealth() - damageToMonster;
        monster.setHealth(newMonsterHealth);
        
        switch (MainLogic.getLanguage()) {
            case "English": MainLogic.getGameWindow().printToTerminal("\n\nYou hit " + monster.getName() + " for " + damageToMonster + " damage\n"); break;
            case "German": MainLogic.getGameWindow().printToTerminal("\n\nSie haben " + monster.getName() + " für " + damageToMonster + " Schadenspunkte geschlagen\n"); break;
        }
        
        // If the monster dies, it will be removed, but the battle will continue if it is still alive
        if (monster.isDead()) {
            MainLogic.setCharacterDeath(true);
        }
        else { 
            // Logic for when a monster hits hero in combat 
            int damageToHero = MainLogic.getRandomNumber(monster.getMaxDamage());
            int newHeroHealth = hero.getHealth() - damageToHero;
            hero.setHealth(newHeroHealth);
            
            switch (MainLogic.getLanguage()) {
                case "English": MainLogic.getGameWindow().printToTerminal(monster.getName() + " hits you for " + damageToHero + " damage"); break;
                case "German": MainLogic.getGameWindow().printToTerminal(monster.getName() + " hat Sie für " + damageToHero + " Schadenspunkte geschlagen"); break;
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
    public boolean canMove(String direction) {
        boolean validDirection = false;
        if (direction.contains("north") || direction.contains("nord")) {
            if (yCord != 0) {
                yCord -= 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("south") || direction.contains("süd") || direction.contains("sued")) {
            if (yCord != (dungeonSize - 1)) {
                yCord += 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("east") || direction.contains("ost")) {
            if (xCord != (dungeonSize - 1)) {
                xCord += 1;
                validDirection = true;
            }
        } 
        else if (direction.contains("west")) {
            if (xCord != 0) {
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
