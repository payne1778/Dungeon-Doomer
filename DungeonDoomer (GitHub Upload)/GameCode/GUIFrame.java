import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Holds most of the frontend components for the game. 
 * 
 * @author Blake Payne
 * @since  08.19.2024
 */
@SuppressWarnings("unused")
public class GUIFrame extends JFrame {
    
    private JPanel primaryPanel;    // The primary panel for all visual elements. Everything is added and removed from/to here
    
    private Integer screenWidth;      // Stores the value of the screen width size 
    private Integer screenHeight;     // Stores the value of the screen height size 
    
    private JTextField inputTerminal;   // The text area for the user's input in the createTerminal() method
    private JTextArea outputTerminal;   // The text area for the output in the createTerminal() method
    private JTextField nameField;       // The text field of the user's name when entered in mainMenu() method 
    
    private String terminalOutput = "";  // A large string that hold all text printed to the output terminal 
    private String terminalInput = "";   // A string that hold all text inputted by the user into the input terminal 
    private String statusName = "";      // Used in switch case in createStatusPanel() as the name of a hero's attribute
    private String buttonName = "";      // Used in switch case in mainMenu() to store the name of a button 
    private String userInput = "";       // A string that stores the most recent input of the user into the GUI terminal 
    private String valueName = "";       // Used in switch case in createStatusPanel() to store the value of an attribute
    private String heroName = "";        // Stores the name of the hero when the user enters it in the mainMenu() method
    
    private ArrayList<Character> characterList;       // The ArrayList of characters found in and around the dungeon
    private Integer mainMenuButtonCounter = 0;        // Used in switch case in mainMenu() to track the number of buttons created
    private Integer statusPanelCounter = -1;          // Used in switch case in createStatusPanel() to track the number of panels created
    private Character hero;                           // A character class object that holds the info for the hero
    
    private LanguageTranslation[] statusPanelTranslations;           // Holds the status' name translations in an array  
    private boolean isAdmin = false, nameEntered = false;            // These track if the user has given a hero name or is admin 
    private ArrayList<JPanel> panelGrid = new ArrayList<>();         // Stores the JPanel objects located in each part of the map grid
    private ArrayList<JButton> buttonGrid = new ArrayList<>();       // Stores the JPanel objects for the main menu buttons 
    private SaveFileMaker saveFileMaker = new SaveFileMaker();       // The class object for the Save File Maker file. 
    private SaveFileReader saveFileReader = new SaveFileReader();    // The class object for the Save File Reader file. 
    
    private String[] adminCommands = {"perish", "escape", "list", "size", "status", "lang", "money"};   // A list of commands that can be used if user is admin
    
    /**
     * Constructor that initializes the game.
     */
    public GUIFrame() {
        super("~ Dungeon Doomer ~");
        
        primaryPanel = new JPanel();
        primaryPanel.setLayout(new BorderLayout());
        
        setScreenSize();
        mainMenu();
        
        // Window frame setup
        this.setContentPane(primaryPanel);
        this.setSize(screenWidth, screenHeight);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * Sets the width and height of the screen to private class fields. 
     * 
     * @reference https://www.geeksforgeeks.org/java-program-to-print-screen-resolution/
     */
    private void setScreenSize() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize(); 
        screenWidth = (int)size.getWidth();
        screenHeight = (int)size.getHeight();
    }

    // Getters/Setters for GUIFrame class
    public Integer getScreenWidth() {
        return screenWidth;
    }

    public Integer getScreenHeight() {
        return screenHeight;
    }

    public JPanel getPrimaryPanel() {
        return primaryPanel;
    }

    public String getInputText() {
        return inputTerminal.getText();
    }

    public ArrayList<JPanel> getPanelGrid() {
        return panelGrid;
    }

    public ArrayList<JButton> getButtonGrid() {
        return buttonGrid;
    }

    public void setIsAdmin(boolean privilege) {
        isAdmin = privilege;
    }

    public int getCharacterLocationOnMap(int index) {
        return (MainLogic.getCharacterList().get(index).getYCord() * MainLogic.getCharacterList().get(index).getDungeonSize()) + MainLogic.getCharacterList().get(index).getXCord();
    }

    /**
     * Clears the primary panel of its contents and updates the UI. 
     */
    public void clearPrimaryAndUpdate() {
        primaryPanel.removeAll(); 
        primaryPanel.updateUI();
    }

    /**
     * Clears the primary panel and re-adds graphics by calling the main "create" methods. 
     */
    public void fullGameGraphicsUpdate() {
        clearPrimaryAndUpdate();
        createStatusPanel();
        createTitlePanel();
        createTerminal();
        panelGrid.clear();
        buttonGrid.clear();
        createMap();
    }

    /**
     * "Prints" a string to the terminal in the game. 
     * 
     * @param thingToPrint     The string to "print" or add to the output terminal
     */
    public void printToTerminal(String thingToPrint) { 
        terminalOutput += thingToPrint;
    }

    /**
     * Clears both the input and output terminals of all text. 
     */
    public void clearTerminals() {
        terminalOutput = "";
        terminalInput = "";
    }

    /**
     * Clears both the input and output terminals of all text and updates both terminals.
     */
    public void clearTerminalsAndUpdate() {
        clearTerminals();
        createTerminal();
    }

    /**
     * Creates a popup to the screen with a specific message. 
     * 
     * @param message     A string message to be included in the popup
     */
    public void pushMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Creates a popup to warn user about unsaved progress before closing window. 
     */
    public void pushCloseMessage() {
        String title = "", message = "";
        switch (MainLogic.getLanguage()) {
            case "English": title = "Warning"; message = "All unsaved progress will be lost! Do you want to continue?"; break;
            case "German": title = "Achtung"; message = "Alle nicht gespeicherten Informationen geht verloren! Möchten Sie fortfahren?"; break;
        }
        Integer yesOrNo = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (yesOrNo == 0) { MainLogic.getGameWindow().dispose(); }
    }

    /**
     * Creates a popup to tell user that they have escaped the dungeon.
     */
    public void pushEscapeMessage() {
        String title = "", message = "";
        switch (MainLogic.getLanguage()) {
            case "English": title = "Congrats"; message = "You have escaped the dungeon! Would you like to keep exploring?"; break;
            case "German": title = "Glückwunsch"; message = "Sie sind aus dem Kerker entkommen! Möchten Sie weiter abenteuern?"; break;
        }
        
        Integer yesOrNo = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (yesOrNo == 0) { 
            MainLogic.setGameRerun(true);
        }
        else if (yesOrNo == 1) { pushCloseMessage(); }
    }

    /**
     * Creates a popup to tell user that they have perished in the dungeon.
     */
    public void pushDeathMessage() {
        String title = "", message = "";
        switch (MainLogic.getLanguage()) {
            case "English": title = "Game Over"; message = "You have perished. Click “Okay“ to close or “Cancel“ to reload from a save."; break;
            case "German": title = "Spiel vorbei"; message = "Sie sind gestorben. Drücken Sie „OK“, um zu schließen, oder „Abbrechen“, um aus einer gespeicherten Datei zu laden."; break;
        }
        
        Integer yesOrNo = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
        if (yesOrNo == 2) {
            try { 
                clearTerminalsAndUpdate(); 
                MainLogic.setIsSaveLoading(true); 
                playGame(true); 
            } 
            catch (Exception e) { e.printStackTrace(MainLogic.getPrintStream()); }
        }
        else if (yesOrNo == 0) { MainLogic.getGameWindow().dispose(); }
    }

    /**
     * Calls various methods to create the main menu of the game.
     */
    public void mainMenu() {
        clearPrimaryAndUpdate();
        displayTitleTexts();
        createMainMenuButtons();
        nameField.requestFocusInWindow();
        mainMenuButtonCounter = 0;
    }

    /** 
     * Displays the title, author, and name texts in the upper half of the main menu.
     */
    public void displayTitleTexts() {
        // Title Panel and Label (Used in upper half of main menu screen)
        JPanel titlePanel = createBorderLayoutPanel(screenWidth, screenHeight * 4/9, "BLACK");
        primaryPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Game Title Panel and Label 
        JPanel gameTitlePanel = createFlowLayoutPanel(screenWidth, (screenHeight / 2) - screenHeight * 1/5, (FlowLayout.CENTER), 0, (screenHeight - 725) / 2, "BLACK");
        titlePanel.add(gameTitlePanel, BorderLayout.NORTH);
        
        JLabel gameTitleLabel = createJLabel("Dungeon Doomer", "Arial", 137, "BLACK", "BLUE");
        gameTitlePanel.add(gameTitleLabel);
        
        String authorLabelText = "", nameLabelText = "";
        switch (MainLogic.getLanguage()) {
            case "English": authorLabelText = "Made By: Blake Payne"; nameLabelText = "Enter Hero Name:"; break;
            case "German": authorLabelText = "Hergestellt von: Blake Payne"; nameLabelText = "Geben Sie den Name ein:"; break;
        }
        
        // Author Panel and Label 
        JPanel authorPanel = createFlowLayoutPanel(FlowLayout.CENTER, 0, (screenHeight - 675) / 2, "BLACK");
        titlePanel.add(authorPanel, BorderLayout.SOUTH);
        
        JLabel authorLabel = createJLabel(authorLabelText, "Arial", 30, "BLACK", "BLUE");
        authorPanel.add(authorLabel);
        
        // Hero Name Panel, Label, and Field 
        JPanel namePanel = createGridLayoutPanel(screenWidth, screenHeight, 1, 2, "BLACK");
        primaryPanel.add(namePanel, BorderLayout.CENTER);
        
        JLabel nameLabel = createJLabel(nameLabelText, "Arial", 30, "BLACK", "BLUE");
        namePanel.add(nameLabel);
        
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 30));
        nameField.setHorizontalAlignment((int)JPanel.CENTER_ALIGNMENT);
        nameField.setBackground(Color.DARK_GRAY.darker());
        nameField.setForeground(Color.BLUE);
        namePanel.add(nameField);
        
        nameField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if ((nameField.getText().length() > 0)) {
                        heroName = nameField.getText().toLowerCase().trim();
                        if (heroName.equals("admin")) isAdmin = true;
                        nameEntered = true;
                        playGame(true);
                    }
                }
            }
        });
    }

    /** 
     * Displays the game buttons found in the bottom half of the main menu. 
     */
    private void createMainMenuButtons() {
        // Menu Panel with Buttons
        JPanel menuPanel = createGridLayoutPanel(screenWidth, screenHeight * 4/9, 1, 3);
        primaryPanel.add(menuPanel, BorderLayout.SOUTH);
        
        ArrayList<JPanel> buttonPanels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, (screenHeight / 2 - 125) / 2));
            buttonPanel.setBackground(Color.DARK_GRAY.darker());
            buttonPanels.add(buttonPanel);
            menuPanel.add(buttonPanel);
        }
        
        for (JPanel panel : buttonPanels) {
            
            LanguageTranslation instructions = new LanguageTranslation("Instructions", "Anweisungen");
            LanguageTranslation playGame = new LanguageTranslation("Play Game", "Spiel spielen");
            LanguageTranslation settingsMenu = new LanguageTranslation("Settings", "Einstellungen");
            
            switch (mainMenuButtonCounter) {
                case 0: buttonName = instructions.extrapolate(); break;
                case 1: buttonName = playGame.extrapolate(); break;
                case 2: buttonName = settingsMenu.extrapolate(); break;
            }
            
            JButton button = createJButton(buttonName, "Arial", 27, "BLUE", "WHITE");
            button.setPreferredSize(new Dimension(200, 80));
            button.addActionListener(e -> {
                
                if ((nameField.getText().length() > 0)) {
                    heroName = nameField.getText().toLowerCase().trim();
                    if (heroName.equals("admin")) isAdmin = true;
                    nameEntered = true;
                }
                
                if (button.getText().equals(instructions.extrapolate())) {
                    instructionsMenu(new LanguageTranslation("Back to the Main Menu", "Zurück zum Hauptmenü"));
                }
                else if (button.getText().equals(playGame.extrapolate())) {
                    playGame(true);
                }
                else if (button.getText().equals(settingsMenu.extrapolate())) {
                    settingsMenu(new LanguageTranslation("Back to the Main Menu", "Zurück zum Hauptmenü"));
                } 
                else try { throw new IllegalTranslationException(button.getText()); } catch (IllegalTranslationException ite) { ite.printStackTrace(MainLogic.getPrintStream()); }
                
            });
            
            panel.add(button);
            mainMenuButtonCounter++;
        }
    }

    /**
     * Configures UI to a normal game for the user to interact with. Also prints out command prompts. 
     */
    public void playGame(Boolean userInputRequest) {
        if (MainLogic.getGameStart() || MainLogic.getGameRerun()) {
            characterList = MainLogic.initializeGame(heroName);
        }
        characterList = MainLogic.getCharacterList();
        hero = characterList.get(0);
        fullGameGraphicsUpdate();
        
        if (userInputRequest){
            if (MainLogic.getIsInCombat()) {
                fight(false);
            }
            else if (MainLogic.getIsInTrade()) {
                MainLogic.printMerchantInventory(false);
            }
            else if (MainLogic.getIsSaveLoading()) {
                saveFileReader.printSaveLoadingMenu();
            }
            else {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\nWhat would you like to do? "); break;
                    case "German": printToTerminal("\n\nWas möchten Sie tun? "); break;
                }
            }
        }
    }

    /**
     * Displays a map of the dungeon in the center of the screen. 
     */
    public void createMap() {
        JPanel mapPanel = createFlowLayoutPanel(screenWidth / 2, screenHeight, FlowLayout.CENTER, 0, (screenHeight - 750) / 2, "BLACK");
        primaryPanel.add(mapPanel, BorderLayout.CENTER);
        
        JPanel mapGrid = createGridLayoutPanel((int)(screenWidth / 2.4), (int)(screenHeight / 1.4), MainLogic.getDungeonSize(), MainLogic.getDungeonSize());
        mapPanel.add(mapGrid);
        
        for (int i = 0; i < (MainLogic.getDungeonSize() * MainLogic.getDungeonSize()); i++) {
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new BorderLayout());
            gridPanel.setBackground(Color.DARK_GRAY.darker());
            gridPanel.setPreferredSize(new Dimension((int)(screenWidth / 2.4) / MainLogic.getDungeonSize(), (int)(screenHeight / 1.4) / MainLogic.getDungeonSize()));
            panelGrid.add(gridPanel);
            mapGrid.add(gridPanel);
            primaryPanel.updateUI();
        }
        
        for (JPanel panel : panelGrid) { 
            JButton gridButton = new JButton();
            gridButton.setBackground(Color.DARK_GRAY.darker());
            buttonGrid.add(gridButton);
            panel.add(gridButton);
        }
        
        updateLocation(getCharacterLocationOnMap(0));
    }

    /** 
     * Updates the location of the character to a different spot on the grid. 
     * This is done by passing in the numerical ID of that spot within the grid. 
     * 
     * @param getCounter    Index number of the character's location on the map 
    */
    public void updateLocation(Integer getCounter) {
        JButton selectedButton = buttonGrid.get(getCounter);
        selectedButton.setBackground(Color.GREEN);
    }

    /**
     * Creates status panels of the hero's info and adds them to the frame. 
     */
    public void createStatusPanel() {
        JPanel statusPanel = createGridLayoutPanel((int)(screenWidth / 4), (int)(screenHeight - (screenHeight * 1/8)), 8, 1, "BLUE");
        primaryPanel.add(statusPanel, BorderLayout.WEST);
        
        ArrayList<JPanel> statusBars = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            JPanel statusBar = new JPanel();
            statusBar.setLayout(new BorderLayout());
            statusBars.add(statusBar);
            statusPanel.add(statusBar);
        }
        
        for (JPanel bar : statusBars) {
            JPanel attributePanel = createGridLayoutPanel((screenWidth / 4) / 3, (screenHeight - (screenHeight * 1/8) / 8), 1, 1, "BLACK");
            bar.add(attributePanel, BorderLayout.CENTER);
            
            handleStatusPanelTranslations();
            statusName = statusPanelTranslations[statusPanelCounter].extrapolate();
            
            JLabel attributeLabel = createJLabel(statusName, "Arial", 25, "BLACK", "BLUE");
            attributeLabel.setVerticalAlignment((int)JPanel.CENTER_ALIGNMENT);
            attributePanel.add(attributeLabel);
            
            JPanel valuePanel = createGridLayoutPanel((screenWidth / 4) / 4, (screenHeight - (screenHeight * 1/8) / 8), 1, 1, "BLUE");
            bar.add(valuePanel, BorderLayout.EAST);
            
            JLabel valueLabel = createJLabel(valueName, "Arial", 25, "BLACK", "WHITE");
            valueLabel.setVerticalAlignment((int)JPanel.CENTER_ALIGNMENT);
            valuePanel.add(valueLabel);
            statusPanelCounter++;
        }
        statusPanelCounter = 0;
    }

    /**
     * Populates the translation Array if first call. Otherwise assigns info to valueName. 
     */
    private void handleStatusPanelTranslations() {
        if (statusPanelCounter < 0) {
            statusPanelTranslations = new LanguageTranslation[]
            {
                new LanguageTranslation("Health", "Gesundheitspunkte"),
                new LanguageTranslation("Max Damage", "Max. Schadenspunkte"),
                new LanguageTranslation("Coordinates", "Koordinaten"),
                new LanguageTranslation("Nearby Monsters", "Monsters in der Nähe"),
                new LanguageTranslation("Turn Number", "Zugnummer"),
                new LanguageTranslation("Gold Amount", "Geld Menge"),
                new LanguageTranslation("Health Potion", "Heiltrank"),
                new LanguageTranslation("Strength Potion", "Krafttrank"),
            };
            statusPanelCounter++;
        }
        
        switch (statusPanelCounter) {
            case 0: valueName = String.valueOf(hero.getHealth()); break;
            case 1: valueName = String.valueOf(hero.getMaxDamage()); break;
            case 2: valueName = "(" + hero.getXCord() + ", " + hero.getYCord() + ")"; break;
            case 3: valueName = String.valueOf(hero.inAdjacentRoom()); break; 
            case 4: valueName = String.valueOf(hero.getTurnCounterValue()); break; 
            case 5: valueName = String.valueOf(hero.getGoldValue()); break;
            case 6: valueName = String.valueOf(hero.getPotionMessage(hero.getHealthPotionCondition())); break;
            case 7: valueName = String.valueOf(hero.getPotionMessage(hero.getHealthPotionCondition())); break;
        }
    }

    /**
     * Creates the input and output terminals and adds them to the frame. 
     */
    public void createTerminal() {
        JPanel terminalPanel = createBorderLayoutPanel(screenWidth / 4, screenHeight, "BLACK");
        primaryPanel.add(terminalPanel, BorderLayout.EAST);
        
        outputTerminal = createJTextArea(terminalOutput, "Arial", 24, false, "BLACK", "BLUE");
        JScrollPane scrollPane = new JScrollPane(outputTerminal); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(screenWidth / 4, screenHeight - (screenHeight * 3/10)));
        terminalPanel.add(scrollPane, BorderLayout.NORTH);
        
        SwingUtilities.invokeLater(() -> {
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });
        
        inputTerminal = createJTextField(terminalInput, "Arial", 24, "BLACK", "BLUE");
        inputTerminal.setPreferredSize(new Dimension(screenWidth / 4, screenHeight - (screenHeight * 26/30)));
        terminalPanel.add(inputTerminal, BorderLayout.SOUTH);
        inputTerminal.requestFocusInWindow();
        inputTerminal.addKeyListener(new KeyListener() {
            
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            
            // REFERENCE: https://stackoverflow.com/questions/4673350/detecting-when-user-presses-enter-in-java
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    userInput = inputTerminal.getText().toLowerCase().trim().replaceAll("\\p{Punct}", "");
                    
                    if (MainLogic.getGameOver() && !MainLogic.getIsSaveLoading()) {
                        if (hero.hasEscaped()) { 
                            pushEscapeMessage(); 
                        } 
                        else if (hero.isDead()) {
                            pushDeathMessage();
                        }
                        else {
                            logAndNotifyIllegalState("Error: The game is over, but the hero is neither dead nor free from dungeon.");
                        }
                    }
                    try {
                        if (MainLogic.getIsInCombat()) {
                            combatCommandInterpreter(userInput);
                        }
                        else {
                            generalCommandInterpreter(userInput);
                        }
                    } 
                    catch (Exception ex) {
                        ex.printStackTrace(MainLogic.getPrintStream());
                    }
                    playGame(false);
                    inputTerminal.requestFocusInWindow();
                }
            }
        });
    }

    /**
     * Evaluates the user's input when the hero is not in combat.
     * 
     * @param userInput     The user's input for what the hero should do
     */
    public void generalCommandInterpreter(String userInput)  {
        String[] validDirectionsEN = {"north", "south", "east", "west", "go north", "go south", "go east", "go west"};
        String[] validDirectionsDE = {"nord", "süd", "sued", "ost", "west", "geh nord", "geh süd", "geh sued", "geh ost", "geh west"};
        
        if (userInput.equals("exit") || userInput.equals("verlassen") || userInput.equals("beenden")) {
            pushCloseMessage();
        }
        else if (Arrays.stream(adminCommands).anyMatch(userInput::contains)) {
            if (!isAdmin) { 
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\nThis command is not available to non-admin users"); break;
                    case "German": printToTerminal("\nDieser Befehl ist für Nicht-Admin-Benutzer nicht verfügbar"); break;
                } 
            } 
            adminCommand(userInput);
        }
        else if (userInput.equals("help") || userInput.contains("hilf") || userInput.contains("helf")) {
            handleSaveAndHelpCommands("help");
        }
        else if (MainLogic.getIsSaveLoading()) {
            loadFromSave(userInput);
        }
        // If the user had not entered the above inputs and is not loading a save, the following inputs will be checked. 
        else if (!MainLogic.getIsSaveLoading()) {
            if (userInput.contains("save") || userInput.contains("speich")) {
                handleSaveAndHelpCommands("save");
            }
            else if (userInput.contains("load") || userInput.contains("lad")) {
                handleSaveAndHelpCommands("load");
            }
            else {
                if (MainLogic.getIsInTrade()) {
                    if ((userInput.contains("leave")) || userInput.contains("verlassen")) {
                        MainLogic.setIsInTrade(false);
                        playGame(true);
                    }
                    else trade(userInput);
                }
                else {
                    if (userInput.contains("health") || userInput.contains("heil")) {
                        MainLogic.drinkHealthPotion();
                    }
                    else if (userInput.contains("strength") || userInput.contains("kraft")) {
                        MainLogic.drinkStrengthPotion();
                    }
                    // REFERENCE: https://stackoverflow.com/questions/8992100/test-if-a-string-contains-any-of-the-strings-from-an-array
                    else if ((Arrays.stream(validDirectionsEN).anyMatch(userInput::contains)) || 
                    (Arrays.stream(validDirectionsDE).anyMatch(userInput::contains))) {
                        MainLogic.move(userInput);
                    }
                    else {
                        nonvalidInputMessage();
                        playGame(false);
                    }
                }
            }
        }
        else {
            logAndNotifyIllegalState("User must either be loading a save or playing the game.");
        }
        
        if (MainLogic.getCharacterDeath()) {
            MainLogic.setIsInCombat(false);
            if (!MainLogic.isGameOverAndPushMessageIfSo()) playGame(true);
            characterList = MainLogic.getCharacterList();
        }
        else playGame(true);
    }

    /**
     * Loads a save from a user specified save file. 
     * 
     * @param userInput     The save file number that the user wishes to load from 
     */
    private void loadFromSave(String userInput) {
        int fileNameIndex = -1, largestSaveFileIndex = saveFileReader.getSaveFileNumberTotal();
        try { fileNameIndex = Integer.valueOf(userInput); } catch (NumberFormatException nfe) {}
        
        if ((fileNameIndex >= 1) && (fileNameIndex <= largestSaveFileIndex)) {
            
            // Clears both the front and back end lists 
            characterList.clear();
            MainLogic.getCharacterList().clear();
            
            // Stores the new character list derived from save file
            characterList = saveFileReader.loadFromSave(fileNameIndex);
            MainLogic.setCharacterList(characterList);
            
            clearTerminalsAndUpdate();
            MainLogic.setGameRerun(false);
            MainLogic.setGameOver(false);
            MainLogic.setCharacterDeath(false);
            MainLogic.setIsInCombat(false);
            MainLogic.setIsInTrade(false);
            
            // Sets the dungeon size in front and back end values
            Integer dungeonSize = saveFileReader.getDungeonSizeValue();
            MainLogic.setDungeonSize(dungeonSize);
            hero.setDungeonSize(dungeonSize); 
            hero.setTurnCounterValue(characterList.get(0).getTurnCounterValue());
            if (characterList.get(0).getName().equalsIgnoreCase("admin")) setIsAdmin(true);
            
            MainLogic.setIsSaveLoading(false);
            switch (MainLogic.getLanguage()) {
                case "English": pushMessage("Successfully loaded from " + saveFileReader.getSaveFileString()); break;
                case "German": pushMessage("Erfolgreich geladen aus " + saveFileReader.getSaveFileString()); break;
            }
            playGame(false);
        }
        else nonvalidSaveMessage();
    }

    /**
     * Handles functionality of commands to load from/make a save or request information. 
     * 
     * @param command    The inputted command from the user 
     */
    private void handleSaveAndHelpCommands(String command) {
        switch (command) {
            case "help":
                if (MainLogic.getIsInTrade()) {
                    switch (MainLogic.getLanguage()) {
                        case "English": 
                            printToTerminal("When trading, you have the following commands available to you: *health* potion, *strength* potion, *leave* trade, *kill* merchant"); 
                        break;
                        case "German": 
                            printToTerminal("Während des Handels können Sie die folgenden Befehle nutzen: *Heiltrank* trinken, *Krafttrank* trinken, Handel *verlassen*, Händler *toeten/töten*"); 
                        break;
                    }
                }
                else if (MainLogic.getIsSaveLoading()) {
                    switch (MainLogic.getLanguage()) {
                        case "English": printToTerminal("When loading from a save, you must enter a number that corresponds to the save file you wish to load from."); break;
                        case "German": printToTerminal("Wenn Sie eine Speicherdatei laden möchten, drücken Sie bitte die passende Nummer des Speicherdateinamens."); break;
                    }
                }
                else {
                    switch (MainLogic.getLanguage()) {
                        case "English": 
                        printToTerminal("\n\nGenerally, you have the following commands available to you: go north, go south, go east, go west, save, load save, drink health potion, drink strength potion");
                        break;
                        case "German": 
                            printToTerminal("Normalerweise können Sie die folgenden Befehle nutzen: geh *nord*, geh *süd/sued*, geh *ost*, geh *west*, *Heiltrank* trinken, *Krafttrank* trinken, *speichern*, *laden*"); 
                        break;
                    }
                }
            break;
            case "save":
                if (MainLogic.getIsInTrade()) {
                    printToTerminal(new LanguageTranslation("You cannot save while trading", "Sie können während des Handels keine Speicherdatei erstellen").extrapolate());
                }
                else if (MainLogic.getIsInCombat()) {
                    printToTerminal(new LanguageTranslation("You cannot save while in combat", "Sie können eine Speicherdatei nicht erstellen, während Sie im Kampf sind.").extrapolate());
                }
                else { saveFileMaker.createSave(); }
            break;
            case "load":
                if (MainLogic.getIsInTrade()) {
                    printToTerminal(new LanguageTranslation("You cannot load a save while trading", "Sie können während des Handels keine gespeicherte Datei laden").extrapolate());
                }
                else if (MainLogic.getIsInCombat()) {
                    printToTerminal(new LanguageTranslation("You cannot load a save while in combat", "Sie können eine gespeicherte Datei nicht laden, während Sie im Kampf sind.").extrapolate());
                }
                else {
                    MainLogic.setIsSaveLoading(true);
                    playGame(false);
                }
            break;
        }
    }

    /**
     * When called, an Illegal State Exception will be called and logged. 
     * Additionally, the user will be notified of the exception. 
     * 
     * @param exceptionMessage     The reason why the exception was thrown 
     */
    public void logAndNotifyIllegalState(String exceptionMessage) {
        try { 
            throw new IllegalStateException(exceptionMessage); 
        }
        catch (IllegalStateException ise) {
            MainLogic.getGameWindow().pushMessage(ise.getMessage());
            ise.printStackTrace(MainLogic.getPrintStream());
        }
    }

    /**
     * Evaluates the user's input when the hero is not in combat. 
     * 
     * @param userInput     The user's input for what the hero should do
     */
    public void combatCommandInterpreter(String userInput) {
        Boolean successfulRetreat = false;
        if (userInput.equals("fight") || userInput.contains("kampf")) {
            hero.hitCharacter();
            hero.turnHealthDeduction();
        }
        else if (userInput.equals("retreat") || userInput.contains("zieh")) {
            successfulRetreat = MainLogic.retreat(0);
            hero.turnHealthDeduction();
        }
        else if (userInput.contains("health") || userInput.contains("heil")) {
            MainLogic.drinkHealthPotion();
        }
        else if (userInput.contains("strength") || userInput.contains("kraft")) {
            MainLogic.drinkStrengthPotion();
        }
        else if (userInput.equals("help") || userInput.contains("hilf") || userInput.contains("helf")) {
            handleSaveAndHelpCommands("help");
        }
        else if (userInput.contains("save") || userInput.contains("speich")) {
            handleSaveAndHelpCommands("save");
        }
        else if (userInput.contains("load") || userInput.contains("lad")) {
            handleSaveAndHelpCommands("load");
        }
        else if (Arrays.stream(adminCommands).anyMatch(userInput::contains)) {
            if (!isAdmin) { 
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\nThis command is not available to non-admin users"); break;
                    case "German": printToTerminal("\nDieser Befehl ist für Nicht-Admin-Benutzer nicht verfügbar"); break;
                } 
            } 
            adminCommand(userInput);
        }
        else {
            nonvalidInputMessage();
            playGame(false);
        }
        if (MainLogic.getCharacterDeath() || successfulRetreat) {
            MainLogic.setIsInCombat(false);
            if (!MainLogic.isGameOverAndPushMessageIfSo()) playGame(true); // No cleanup function needed 
            characterList = MainLogic.getCharacterList();
        }
        else { 
            if(!successfulRetreat) MainLogic.setIsInCombat(true); fight(false); 
        }
    }

    /**
     * A selection of commands only available to a user with admin privileges. 
     */
    private void adminCommand(String command) {
        switch (command) {
            case "perish":
                hero.setHealth(0);
                MainLogic.isGameOverAndPushMessageIfSo(); // No clean up needed
                break;
            case "escape":
                hero.setXYCords(MainLogic.getDungeonSize() - 1, MainLogic.getDungeonSize() - 1); 
                MainLogic.isGameOverAndPushMessageIfSo(); // No clean up needed
                break;
            case "list":
                for (Character character: characterList) {
                    System.out.println(character.toString());
                }
                System.out.println();
                break;
            case "size":
                System.out.println("Size: " + MainLogic.getDungeonSize());
                break;
            case "status":
                System.out.println("Combat: " + MainLogic.getIsInCombat() + ", Trade: " + MainLogic.getIsInTrade() + ", Loading Save: " + MainLogic.getIsSaveLoading());
                break;
            case "lang": 
                MainLogic.setLanguage("non-valid-language-setting");
                break;
            case "money":
                hero.setGoldValue(hero.getGoldValue() + 100);
                break;
                
        }
    }

    /**
     * Prints a message to the terminal indicating that the most recent command input was invalid.
     */
    private void nonvalidInputMessage() {
        switch (MainLogic.getLanguage()) {
            case "English": printToTerminal("\n\nThat's not a valid input!"); break;
            case "German": printToTerminal("\n\nDas ist keine gültige Eingabe!"); break;
        }
    }

    /**
     * Prints a message to the terminal indicating that the most recent save file input was invalid.
     */
    private void nonvalidSaveMessage() {
        switch (MainLogic.getLanguage()) {
            case "English": pushMessage("\n\nThat's not a valid save file!"); break;
            case "German": pushMessage("\n\nDas ist keine gültige Speicherdatei!"); break;
        }
    }

    /**
     * Preforms various trades with a merchant when the hero is in a room with one. 
     * 
     * @param tradeInput    The user's input for a specific trade 
     */
    public void trade(String tradeInput) {
        String merchantName = characterList.get(hero.getCharacterInSameRoom()).getName();
        fullGameGraphicsUpdate(); 
        
        if (tradeInput.contains("health") || tradeInput.contains("heil")) {
            
            if (!characterList.get(hero.getCharacterInSameRoom()).getHealthPotionCondition()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\n" + merchantName + " does not have a health potion for sale"); break;
                    case "German": printToTerminal("\n\n" + merchantName + " verkauft keinen Heiltrank"); break;
                }
            }
            if (hero.getHealthPotionCondition()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\nYou already have a health potion in your inventory"); break;
                    case "German": printToTerminal("\n\nSie haben schon einen Heiltrank im Inventar"); break;
                }
            }
            if (hero.getGoldValue() < MainLogic.getPotionPrice()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\n" + hero.getGoldValue() + " gold is not enough to purchase a health potion");  break;
                    case "German": printToTerminal("\n\n" + hero.getGoldValue() + " Geld ist nicht genug, um einen Heiltrank zu kaufen");  break;
                }
            }
            characterList.get(hero.getCharacterInSameRoom()).setHealthPotionCondition(false); 
            hero.setHealthPotionCondition(true);
            hero.setGoldValue(hero.getGoldValue() - MainLogic.getPotionPrice());
            switch (MainLogic.getLanguage()) {
                case "English": printToTerminal("\n\nYou bought a health potion and have " + hero.getGoldValue() + " gold remaining"); break;
                case "German": printToTerminal("\n\nSie haben einen Heiltrank gekauft und haben noch " + hero.getGoldValue() + " Geld übrig"); break;
            }
        }
        else if (tradeInput.contains("strength") || tradeInput.contains("kraft")) {
            
            if (!characterList.get(hero.getCharacterInSameRoom()).getStrengthPotionCondition()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\n" + merchantName + " does not have a strength potion for sale"); break;
                    case "German": printToTerminal("\n\n" + merchantName + " verkauft keinen Krafttrank"); break;
                }
            }
            if (hero.getStrengthPotionCondition()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\nYou already have a strength potion in your inventory"); break;
                    case "German": printToTerminal("\n\nSie haben schon einen Krafttrank im Inventar"); break;
                }
            }
            if (hero.getGoldValue() < MainLogic.getPotionPrice()) {
                switch (MainLogic.getLanguage()) {
                    case "English": printToTerminal("\n\n" + hero.getGoldValue() + " gold is not enough to purchase a strength potion");  break;
                    case "German": printToTerminal("\n\n" + hero.getGoldValue() + " Geld ist nicht genug, um einen Krafttrank zu kaufen");  break;
                }
            }
            characterList.get(hero.getCharacterInSameRoom()).setStrengthPotionCondition(false); 
            hero.setStrengthPotionCondition(true);
            hero.setGoldValue(hero.getGoldValue() - MainLogic.getPotionPrice());
            switch (MainLogic.getLanguage()) {
                case "English": printToTerminal("\n\nYou bought a strength potion and have " + hero.getGoldValue() + " gold remaining"); break;
                case "German": printToTerminal("\n\nSie haben einen Krafttrank gekauft und haben noch " + hero.getGoldValue() + " Geld übrig"); break;
            }
        } 
        else if (tradeInput.contains("kill") || tradeInput.contains("toet") || tradeInput.contains("töt")) {
            // If the user decides to kill the merchant, the merchant will then forever be considered a monster 
            characterList.get(hero.getCharacterInSameRoom()).setTypeValue(2); 
            MainLogic.setIsInTrade(false);
            MainLogic.setIsInCombat(true);
            fight(true);
        }
        else nonvalidInputMessage();
    }

    /**
     * Configures UI to combat display and prints out fight info. 
     * 
     * @param firstFight    Indicates whether this is the first time the hero has 
     *                      fought this monster
     */
    public void fight(Boolean firstFight) {
        fullGameGraphicsUpdate();
        
        if (firstFight) {
            switch (MainLogic.getLanguage()) {
                case "English": 
                    printToTerminal("\n\n~ Fight with " + MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getName() + " ~\n" +
                    hero.getName() + " at " + hero.getXCord() + ", " + hero.getYCord() + " with " + hero.getHealth() + " health versus " +
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getName() + " at " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getXCord() + ", " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getYCord() + " with " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getHealth() + " health");
                    printToTerminal("\n\nWhat would you like to do? ");
                break;
                case "German": 
                    printToTerminal("\n\n~ Kampf mit " + MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getName() + " ~\n" +
                    hero.getName() + " an " + hero.getXCord() + ", " + hero.getYCord() + " mit " + hero.getHealth() + " Gesundheitspunkte gegen " +
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getName() + " an " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getXCord() + ", " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getYCord() + " mit " + 
                    MainLogic.getCharacterList().get(hero.getCharacterInSameRoom()).getHealth() + " Gesundheitspunkte");
                    printToTerminal("\n\nWas möchten Sie tun?"); 
                break;
            }
        }
    }

    /**
     * Displays the game title and the instructions and settings buttons in the top of screen. 
     */
    public void createTitlePanel() {
        JPanel titlePanel = createBorderLayoutPanel(screenWidth, screenHeight / 8, "BLACK");
        primaryPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Game Title Panel and Text
        JPanel gameTitlePanel = createFlowLayoutPanel(screenWidth / 2 , screenHeight / 8, FlowLayout.CENTER, 0, (screenHeight - 815) / 2, "BLACK");
        titlePanel.add(gameTitlePanel, BorderLayout.CENTER);
        
        JLabel titleLabel = createJLabel("Dungeon Doomer", "Arial", 75, "BLACK", "BLUE");
        gameTitlePanel.add(titleLabel);
        
        String settingsText = "", instructionsText = "";
        switch (MainLogic.getLanguage()) {
            case "English": settingsText = "Settings"; instructionsText = "Instructions"; break;
            case "German": settingsText = "Einstellungen"; instructionsText = "Anweisungen"; break;
        }
        
        // Instructions Panel and Button
        JPanel instructionsButtonPanel = createGridLayoutPanel(screenWidth / 4 , screenHeight / 8, 1, 1, "BLACK");
        titlePanel.add(instructionsButtonPanel, BorderLayout.WEST);
        
        JButton instructionsButton = createJButton(instructionsText, "Arial", 35, "BLUE", "WHITE");
        instructionsButton.setBorder(null);
        instructionsButtonPanel.add(instructionsButton);
        instructionsButton.addActionListener(e -> instructionsMenu(new LanguageTranslation("Back to the Game", "Zurück zum Spiel")));
        
        // Settings Panel and Button
        JPanel settingsButtonPanel = createGridLayoutPanel(screenWidth / 4 , screenHeight / 8, 1, 1, "BLACK");
        titlePanel.add(settingsButtonPanel, BorderLayout.EAST);
        
        JButton settingsButton = createJButton(settingsText, "Arial", 35, "BLUE", "WHITE");
        settingsButton.setBorder(null);
        settingsButtonPanel.add(settingsButton);
        settingsButton.addActionListener(e -> settingsMenu(new LanguageTranslation("Back to the Game", "Zurück zum Spiel")));
    }

    /**
     * When instructions button is pressed, the instructions menu will be displayed. 
     * 
     * @param langObject     The name translation of the method to return to when done
     */
    private void instructionsMenu(LanguageTranslation langObject) {
        clearPrimaryAndUpdate();
        
        JPanel instructionsPanel = createBorderLayoutPanel((screenWidth), (screenHeight - (screenHeight * 1/8)), "BLUE");
        primaryPanel.add(instructionsPanel, BorderLayout.CENTER);
        
        JTextArea instructionsArea = createJTextArea(MainLogic.getInstructionsText(), "Arial", 25, false, "BLACK", "BLUE");
        instructionsPanel.add(instructionsArea, BorderLayout.CENTER);
        
        JScrollPane scrollPane = createScrollPane(instructionsArea, (screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 4/9)), false);
        instructionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel spacerPanelEast = createSpacerPanelEast(instructionsPanel, (screenWidth / 26), (screenHeight - (screenHeight * 1/8)), "BLACK");
        JPanel spacerPanelWest = createSpacerPanelWest(instructionsPanel, (screenWidth / 20), (screenHeight - (screenHeight * 1/8)), "BLACK");
        JPanel spacerPanelNorth = createSpacerPanelNorth(instructionsPanel, (screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 49/50)), "BLACK");
        JPanel spacerPanelSouth = createSpacerPanelSouth(instructionsPanel, (screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 49/50)), "BLACK");
        
        // Return Button functionality 
        JPanel instructionsReturnPanel = createGridLayoutPanel((screenWidth), (screenHeight - (screenHeight * 7/8)), 1, 1, "BLACK");
        primaryPanel.add(instructionsReturnPanel, BorderLayout.SOUTH);
        
        String translation = langObject.extrapolate();
        JButton returnButton = createJButton(translation, "BLUE", "WHITE");
        instructionsReturnPanel.add(returnButton);
        returnButton.addActionListener(e -> {
            
            try {
                if (translation.equals("Back to the Game") || translation.equals("Zurück zum Spiel")) {
                    playGame(false);
                }
                else if (translation.equals("Back to the Main Menu") || translation.equals("Zurück zum Hauptmenü")) {
                    mainMenu();
                }
                else throw new IllegalTranslationException(translation);
            }
            catch (IllegalTranslationException ile) {
                ile.printStackTrace(MainLogic.getPrintStream());
            }
            catch (Exception ex) {
                ex.printStackTrace(MainLogic.getPrintStream());
            }
            
        });
    }

    /**
     * When settingsMenu button is pressed, the settings menu will be displayed. 
     * 
     * @param langObject     The name translation of the method to return to when done 
     */
    private void settingsMenu(LanguageTranslation langObject) {
        clearPrimaryAndUpdate();
        
        // Main panel for settings menu and other panels for settings buttons 
        JPanel settingsPanel = createGridLayoutPanel((screenWidth), (screenHeight - (screenHeight * 1/8)), 2, 2);
        primaryPanel.add(settingsPanel, BorderLayout.CENTER); 
        
        JPanel retreatOptionPanel = createBorderLayoutPanel((screenWidth / 4), (screenHeight - (screenHeight * 1/8)) / 4);
        settingsPanel.add(retreatOptionPanel, BorderLayout.NORTH);
        
        JPanel languageOptionPanel = createBorderLayoutPanel((screenWidth / 4), (screenHeight - (screenHeight * 1/8)) / 4);
        settingsPanel.add(languageOptionPanel, BorderLayout.CENTER);    
        
        JPanel cacheOptionPanel = createBorderLayoutPanel((screenWidth / 4), (screenHeight - (screenHeight * 1/8)) / 4);
        settingsPanel.add(cacheOptionPanel, BorderLayout.SOUTH);
        
        JPanel loadSaveOptionPanel = createBorderLayoutPanel((screenWidth / 4), (screenHeight - (screenHeight * 1/8)) / 4);
        settingsPanel.add(loadSaveOptionPanel, BorderLayout.SOUTH);
        
        // Various button functionality
        String retreatButtonText = "", languageButtonText = "", cacheButtonText = "", loadSaveButtonText = "";
        switch (MainLogic.getLanguage()) {
            case "English": 
                retreatButtonText = "Toggle Retreat Option"; 
                languageButtonText = "Toggle Language Option"; 
                cacheButtonText = "Clear Cache";
                loadSaveButtonText = "Load From Save";
            break;
            case "German": 
                retreatButtonText = "Zurückziehen umschalten"; 
                languageButtonText = "Sprache umschalten"; 
                cacheButtonText = "Cache leeren";
                loadSaveButtonText = "Aus Speicherdatei Laden";
            break;
        }
        
        // Button creation for settings menu 
        JButton retreatButton = createJButton(retreatButtonText, "DARK_GRAY", "WHITE");
        retreatOptionPanel.add(retreatButton, BorderLayout.CENTER);
        retreatButton.addActionListener(e -> { MainLogic.toggleRetreat(); settingsMenu(langObject); });
        
        JButton languageButton = createJButton(languageButtonText, "DARK_GRAY", "WHITE");
        languageOptionPanel.add(languageButton, BorderLayout.CENTER);
        languageButton.addActionListener(e -> { MainLogic.toggleLanguage(); settingsMenu(langObject); });
        
        JButton cacheButton = createJButton(cacheButtonText, "DARK_GRAY", "WHITE");
        cacheOptionPanel.add(cacheButton, BorderLayout.CENTER);
        cacheButton.addActionListener(e -> { MainLogic.clearErrorLogCache(); settingsMenu(langObject); });
        
        JButton loadSaveButton = createJButton(loadSaveButtonText, "DARK_GRAY", "WHITE");
        loadSaveOptionPanel.add(loadSaveButton, BorderLayout.CENTER);
        loadSaveButton.addActionListener(e -> { saveLoadingMenu(langObject); });
        
        // Return button functionality 
        JPanel settingsReturnPanel = createGridLayoutPanel((screenWidth), (screenHeight - (screenHeight * 7/8)), 1, 1, "BLACK");
        primaryPanel.add(settingsReturnPanel, BorderLayout.SOUTH);
        
        String translation = langObject.extrapolate();
        JButton returnButton = createJButton(translation, "BLUE", "WHITE");
        settingsReturnPanel.add(returnButton);
        returnButton.addActionListener(e -> {
            
            try {
                
                if (translation.equals("Back to the Game") || translation.equals("Zurück zum Spiel")) {
                    playGame(false);
                }
                else if (translation.equals("Back to the Main Menu") || translation.equals("Zurück zum Hauptmenü")) {
                    mainMenu();
                }
                else throw new IllegalTranslationException(translation);
            
            }
            catch (IllegalTranslationException ile) {
                ile.printStackTrace(MainLogic.getPrintStream());
            }
            catch (Exception ex) {
                ex.printStackTrace(MainLogic.getPrintStream());
            }

        });
    }

/**
     * When instructions button is pressed, the instructions menu will be displayed. 
     * 
     * @param langObject     The name translation of the method to return to when done
     */
    private void saveLoadingMenu(LanguageTranslation langObject) {
        clearPrimaryAndUpdate();
        clearTerminals(); 
        
        // Main panels for saveLoadingMenu method
        JPanel loadSaveMainPanel = createBorderLayoutPanel((screenWidth), (screenHeight - (screenHeight * 1/8)), "BLACK");
        primaryPanel.add(loadSaveMainPanel, BorderLayout.CENTER);
        
        JPanel textAndSpacersPanel = createBorderLayoutPanel((screenWidth), (screenHeight - (screenHeight * 1/8)), "BLACK");
        loadSaveMainPanel.add(textAndSpacersPanel, BorderLayout.CENTER);
        
        // Game title panel and label creation
        JPanel gameTitlePanel = createFlowLayoutPanel(screenWidth / 2 , screenHeight / 8, FlowLayout.CENTER, 0, (screenHeight - 850) / 2, "BLACK");
        loadSaveMainPanel.add(gameTitlePanel, BorderLayout.NORTH);
        
        JLabel gameTitleLabel = createJLabel("Dungeon Doomer", "Arial", 75, "BLACK", "BLUE");
        gameTitlePanel.add(gameTitleLabel);     
        
        JPanel spacerPanelEast = createSpacerPanelEast(textAndSpacersPanel, (screenWidth / 26), (screenHeight - (screenHeight * 1/8)), "BLACK");
        JPanel spacerPanelWest = createSpacerPanelWest(textAndSpacersPanel, (screenWidth / 20), (screenHeight - (screenHeight * 1/8)), "BLACK");
        JPanel spacerPanelNorth = createSpacerPanelNorth(textAndSpacersPanel, (screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 49/50)), "BLACK");
        JPanel spacerPanelSouth = createSpacerPanelSouth(textAndSpacersPanel, (screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 49/50)), "BLACK");
        
        // Inner center panel of textAndSpacersPanel & "Output Terminal" with scroll bar 
        JPanel textsPanel = createBorderLayoutPanel((screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 1/8)), "BLACK");
        textAndSpacersPanel.add(textsPanel, BorderLayout.CENTER);
        
        saveFileReader.printSaveLoadingMenu();
        JTextArea saveMenuArea = createJTextArea(terminalOutput, "Arial", 28, false, "BLACK", "BLUE");
        textsPanel.add(saveMenuArea, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(saveMenuArea); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension((screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 4/9))));
        scrollPane.setBorder(null);
        textsPanel.add(scrollPane, BorderLayout.NORTH);
        
        // User input functionality
        JTextField userInputField = createJTextField("", "Arial", 28, "BLACK", "BLUE");
        userInputField.setPreferredSize(new Dimension((screenWidth - (screenWidth * 1/4)), (screenHeight - (screenHeight * 8/9))));
        textsPanel.add(userInputField, BorderLayout.SOUTH);
        
        userInputField.requestFocusInWindow();
        userInputField.setBorder(null);
        userInputField.addKeyListener(new KeyListener() {
            
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    userInput = userInputField.getText().toLowerCase().trim().replaceAll("\\p{Punct}", "");
                    loadFromSave(userInput);
                    saveLoadingMenu(langObject);
                }
            }
        });
        
        // Return Button Functionality
        JPanel loadSaveReturnPanel = createGridLayoutPanel((screenWidth), (screenHeight - (screenHeight * 7/8)), 1, 1, "BLACK");
        primaryPanel.add(loadSaveReturnPanel, BorderLayout.SOUTH);
        
        String buttonTextTranslation;
        if (!MainLogic.getGameStart()) if (MainLogic.isGameOverAndPushMessageIfSo()) { 
            buttonTextTranslation = new LanguageTranslation("Quit Game", "Spiel verlassen").extrapolate(); 
        }
        buttonTextTranslation = new LanguageTranslation("Back to Settings", "Zurück zur Einstellungen").extrapolate();
        
        JButton returnButton = createJButton(buttonTextTranslation, "BLUE", "WHITE");
        loadSaveReturnPanel.add(returnButton);
        returnButton.addActionListener(e -> { 
            
            if (!MainLogic.getGameStart()) if (MainLogic.isGameOverAndPushMessageIfSo()) pushCloseMessage();
            settingsMenu(langObject); 
            
        });
    }

    /**
     * Creates a new JPanel with a BorderLayout manager when called. 
     * 
     * @param width                 The preferred value for the panel's width
     * @param height                The preferred value for the panel's height
     * @return borderLayoutPanel    The Panel that is created when method is called 
     */
    private JPanel createBorderLayoutPanel(int width, int height) {
        JPanel borderLayoutPanel = new JPanel();
        borderLayoutPanel.setPreferredSize(new Dimension(width, height));
        borderLayoutPanel.setLayout(new BorderLayout());
        return borderLayoutPanel;
    }

    /**
     * Creates a new JPanel with a BorderLayout manager when called. 
     * 
     * @param width                 The preferred value for the panel's width
     * @param height                The preferred value for the panel's height
     * @param colorName             The preferred color for the panel's background
     * @return borderLayoutPanel    The Panel that is created when method is called 
     */
    private JPanel createBorderLayoutPanel(int width, int height, String colorName) {
        JPanel borderLayoutPanel = createBorderLayoutPanel(width, height);
        borderLayoutPanel.setBackground(getColor(colorName));
        return borderLayoutPanel;
    }

    /**
     * Creates a new JPanel with a GridLayout manager when called. 
     * 
     * @param width                 The preferred value for the panel's width
     * @param height                The preferred value for the panel's height
     * @param rows                  The preferred number of rows in the grid
     * @param columns               The preferred number of columns in the grid
     * @return gridLayoutPanel      The Panel that is created when method is called 
     */
    private JPanel createGridLayoutPanel(int width, int height, int rows, int columns) {
        JPanel gridLayoutPanel = new JPanel();
        gridLayoutPanel.setPreferredSize(new Dimension(width, height));
        gridLayoutPanel.setLayout(new GridLayout(rows, columns));
        return gridLayoutPanel;
    }

    /**
     * Creates a new JPanel with a GridLayout manager when called. 
     * 
     * @param width                 The preferred value for the panel's width
     * @param height                The preferred value for the panel's height
     * @param rows                  The preferred number of rows in the grid
     * @param columns               The preferred number of columns in the grid
     * @param colorName             The preferred color for the panel's background
     * @return gridLayoutPanel      The Panel that is created when method is called 
     */
    private JPanel createGridLayoutPanel(int width, int height, int rows, int columns, String colorName) {
        JPanel gridLayoutPanel = createGridLayoutPanel(width, height, rows, columns);
        gridLayoutPanel.setBackground(getColor(colorName));
        return gridLayoutPanel;
    }

    /**
     * Creates a new JPanel with a FlowLayout manager when called. 
     * 
     * @param align              Screen axis alignment value 
     * @param hgap               Value of horizontal gap 
     * @param vgap               Value of vertical gap 
     * @param colorName          The preferred color for the panel's background
     * @return flowLayoutPanel   The Panel that is created when method is called 
     */
    private JPanel createFlowLayoutPanel(int align, int hgap, int vgap, String colorName) {
        JPanel flowLayoutPanel = new JPanel();
        flowLayoutPanel.setLayout(new FlowLayout(align, hgap, vgap));
        flowLayoutPanel.setBackground(getColor(colorName));
        return flowLayoutPanel;
    }

    /**
     * Creates a new JPanel with a FlowLayout manager when called. 
     * 
     * @param width              The preferred value for the panel's width
     * @param height             The preferred value for the panel's height
     * @param align              Screen axis alignment value 
     * @param hgap               Value of horizontal gap 
     * @param vgap               Value of vertical gap 
     * @param colorName          The preferred color for the panel's background
     * @return flowLayoutPanel   The Panel that is created when method is called 
     */
    private JPanel createFlowLayoutPanel(int width, int height, int align, int hgap, int vgap, String colorName) {
        JPanel flowLayoutPanel = createFlowLayoutPanel(align, hgap, vgap, colorName);
        flowLayoutPanel.setPreferredSize(new Dimension(width, height));
        return flowLayoutPanel;
    }

    /**
     * Creates a spacer panel in the eastern position of a BorderLayout Panel. 
     * 
     * @param parentPanel         The parent panel of the spacer panel (What this panel will be added to)
     * @param width               The preferred value for the spacer panel's width
     * @param height              The preferred value for the spacer panel's height
     * @param colorName           The preferred color for the spacer panel's background
     * @return spacerPanelEast    The panel that is created when method is called 
     */
    private JPanel createSpacerPanelEast(JPanel parentPanel, int width, int height, String colorName) {
        JPanel spacerPanelEast = createBorderLayoutPanel(width, height, colorName);
        parentPanel.add(spacerPanelEast, BorderLayout.EAST);
        return spacerPanelEast;
    }

    /**
     * Creates a spacer panel in the western position of a BorderLayout Panel. 
     * 
     * @param parentPanel         The parent panel of the spacer panel (What this panel will be added to)
     * @param width               The preferred value for the spacer panel's width
     * @param height              The preferred value for the spacer panel's height
     * @param colorName           The preferred color for the spacer panel's background
     * @return spacerPanelWest    The panel that is created when method is called 
     */
    private JPanel createSpacerPanelWest(JPanel parentPanel, int width, int height, String colorName) {
        JPanel spacerPanelWest = createBorderLayoutPanel(width, height, colorName);
        parentPanel.add(spacerPanelWest, BorderLayout.WEST);
        return spacerPanelWest;
    }

    /**
     * Creates a spacer panel in the northern position of a BorderLayout Panel. 
     * 
     * @param parentPanel         The parent panel of the spacer panel (What this panel will be added to)
     * @param width               The preferred value for the spacer panel's width
     * @param height              The preferred value for the spacer panel's height
     * @param colorName           The preferred color for the spacer panel's background
     * @return spacerPanelNorth    The panel that is created when method is called 
     */
    private JPanel createSpacerPanelNorth(JPanel parentPanel, int width, int height, String colorName) {
        JPanel spacerPanelNorth = createBorderLayoutPanel(width, height, colorName);
        parentPanel.add(spacerPanelNorth, BorderLayout.NORTH);
        return spacerPanelNorth;
    }

    /**
     * Creates a spacer panel in the southern position of a BorderLayout Panel. 
     * 
     * @param parentPanel         The parent panel of the spacer panel (What this panel will be added to)
     * @param width               The preferred value for the spacer panel's width
     * @param height              The preferred value for the spacer panel's height
     * @param colorName           The preferred color for the spacer panel's background
     * @return spacerPanelSouth    The panel that is created when method is called 
     */
    private JPanel createSpacerPanelSouth(JPanel parentPanel, int width, int height, String colorName) {
        JPanel spacerPanelSouth = createBorderLayoutPanel(width, height, colorName);
        parentPanel.add(spacerPanelSouth, BorderLayout.SOUTH);
        return spacerPanelSouth;
    }

    /**
     * Creates a new JLabel when called. 
     * 
     * @param text          The text to be in the label 
     * @param font          The font of the label
     * @param fontSize      The size of the font 
     * @param background    The color of the background
     * @param foreground    The color of the foreground
     * @return label        The label that was created
     */
    private JLabel createJLabel(String text, String font, int fontSize, String background, String foreground) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(font, Font.PLAIN, fontSize));
        label.setBackground(getColor(background));
        label.setForeground(getColor(foreground));
        label.setHorizontalAlignment((int)JFrame.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Creates a new JTextArea when called. 
     * 
     * @param text          The text to be in textArea 
     * @param font          The font of textArea
     * @param fontSize      The size of the font 
     * @param editable      The option for edibility 
     * @param background    The color of the background
     * @param foreground    The color of the foreground
     * @return textArea     The text area that was created
     */
    private JTextArea createJTextArea(String text, String font, int fontSize, boolean editable, String background, String foreground) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font(font, Font.PLAIN, fontSize));
        textArea.setBackground(getColor(background));
        textArea.setForeground(getColor(foreground));
        textArea.setEditable(editable);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    /**
     * Creates a new JTextField when called. 
     * 
     * @param text          The text to be in textField 
     * @param font          The font of the text field
     * @param fontSize      The size of the font 
     * @param background    The color of the background
     * @param foreground    The color of the foreground
     * @return textField    The text field that was created
     */
    private JTextField createJTextField(String text, String font, int fontSize, String background, String foreground) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font(font, Font.PLAIN, fontSize));
        textField.setBackground(getColor(background));
        textField.setForeground(getColor(foreground));
        return textField;
    }

    /**
     * Creates a new JButton when called. 
     * 
     * @param text          The text to be in the button 
     * @param background    The color of the background
     * @param foreground    The color of the foreground
     * @return button       The button that was created
     */
    private JButton createJButton(String text, String background, String foreground) {
        JButton button = new JButton(text);
        button.setBackground(getColor(background));
        button.setForeground(getColor(foreground));
        return button;
    }

    /**
     * Creates a new JButton when called. 
     * 
     * @param text          The text to be in the button 
     * @param font          The font of the button 
     * @param fontSize      The size of the font 
     * @param background    The color of the background
     * @param foreground    The color of the foreground
     * @return button       The button that was created 
     */
    private JButton createJButton(String text, String font, int fontSize, String background, String foreground) {
        JButton button = createJButton(text, background, foreground);
        button.setFont(new Font(font, Font.PLAIN, fontSize));
        return button;
    }

    /**
     * Creates a scroll pane on a text area when called. 
     * 
     * @param textArea      The text area that the scroll pane is being added to 
     * @param width         The preferred value for the scrollPane's width
     * @param height        The preferred value for the scrollPane's height
     * @param hasBorder     If true, there is a border and if false, no border
     * @return scrollPane   The scroll pane that is created when method is called
     */
    private JScrollPane createScrollPane(JTextArea textArea, int width, int height, boolean hasBorder) {
        JScrollPane scrollPane = new JScrollPane(textArea); 
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(width, height));
        if (!hasBorder) scrollPane.setBorder(null);
        return scrollPane;
    }

    /**
     * Intakes a String name of a color and returns a color object.
     * 
     * @param colorName         The name of the color that should returned
     * @return desiredColor     The color object with the name of the colorName input 
     * @reference               https://stackoverflow.com/questions/3772098/how-does-java-awt-color-getcolorstring-colorName-work
     */
    private Color getColor(String colorName) {
        Color desiredColor;
        try {
            Field tempField = Color.class.getField(colorName);
            desiredColor = (Color)tempField.get(null);
        } 
        catch (Exception e) {
            desiredColor = null;
        }
        return desiredColor;
    }
}