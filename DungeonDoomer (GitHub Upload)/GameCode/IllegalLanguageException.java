/**
 * Custom exception thrown when an unsupported language is used in the code. 
 * When thrown, the user is informed, and the game language is set to English by default. 
 * 
 * @author Blake Payne
 * @since  06.06.2024
 */
public class IllegalLanguageException extends Exception {

    /**
     * This will be thrown if the current language setting is not supported.
     */
    public IllegalLanguageException() {
        super("Current language setting is not supported: " + MainLogic.getLanguage());
        String errorMessage = "An Illegal Language Exception has been thrown. " +
        "The game's language will now default to English.";
        MainLogic.setLanguage("English");
        MainLogic.getGameWindow().pushMessage(errorMessage);
    }

    /**
     * This will be thrown if an attempt was made for the language variable to be changed to an invalid language. 
     * 
     * @param invalidLanguage   The invalid language that the game was attempting to switch to 
     */
    public IllegalLanguageException(String invalidLanguage) {
        super("The following language setting is not supported: " + invalidLanguage);
        String errorMessage = "An Illegal Language Exception has been thrown. " +
        "The game's language will now default to English.";
        MainLogic.setLanguage("English");
        MainLogic.getGameWindow().pushMessage(errorMessage);
    }

}
