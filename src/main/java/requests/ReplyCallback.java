package requests;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public interface ReplyCallback {
    void sendReply(SendMessage message);
}
