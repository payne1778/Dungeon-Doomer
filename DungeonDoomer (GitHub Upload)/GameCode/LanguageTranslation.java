/**
 * Objects of this record will hold an English and German translation of a sentence. 
 * 
 * @author Blake Payne
 * @since  06.12.2024
 */
public record LanguageTranslation(String english, String german) {

    /**
     * Constructor for LanguageTranslation record. Takes in two translations and stores them as a record object. 
     * 
     * @param english   The English translation string 
     * @param german    The German translation string 
     */
    public LanguageTranslation(String english, String german) {
        if (english.isEmpty() || german.isEmpty()) {
            throw new IllegalStateException("Translation string(s) may not be empty");
        }
        this.english = english;
        this.german = german;
    }

    /**
     * Returns translated text from record object depending on game language. 
     * 
     * @return translationText    The translated text from the record object 
     */
    public String extrapolate() {
        String translationText = "";
        if (MainLogic.getLanguage().equals("English")) {
            translationText = english;
        }
        else if (MainLogic.getLanguage().equals("German")) {
            translationText = german;
        }
        else {
            try { throw new IllegalLanguageException(); } catch (IllegalLanguageException e) { e.printStackTrace(MainLogic.getPrintStream()); }
        }
        return translationText;
    }
}
