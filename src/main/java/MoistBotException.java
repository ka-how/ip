/**
 * Represents an error caused by invalid MoistBot commands or task operations.
 */
public class MoistBotException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message the explanation of the chatbot-specific error
     */
    public MoistBotException(String message) {
        super(message);
    }
}
