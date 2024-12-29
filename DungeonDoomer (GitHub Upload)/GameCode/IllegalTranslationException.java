/**
 * Custom exception thrown when an unknown language extrapolation result is found in the code. 
 * The user will be informed of the issue and encouraged to load from a save or made one if possible. 
 * Note: this exception does not "check" for possible grammatical/spelling errors made in the translation. 
 * 
 * @author Blake Payne
 * @since  06.06.2024
 */
public class IllegalTranslationException extends Exception {

    public IllegalTranslationException(String translation) {
        super("Current translation extrapolation is not supported: " + translation);

        boolean successfulSaveCreation = true;
        try {
            new SaveFileMaker().createSave();
        } 
        catch (Exception e) { 
            e.printStackTrace(MainLogic.getPrintStream()); 
            successfulSaveCreation = false;
        }
        
        String errorMessage = "An Illegal Translation Exception has been thrown. " +
        "The game may not function normally from this point. ";
        
        if (successfulSaveCreation) { 
            errorMessage += "Your game has been saved."; 
        }
        else {
            errorMessage += "Your game could not be saved. Please reload from a save.";
        }
        MainLogic.getGameWindow().pushMessage(errorMessage);
    }

}