package requests;

import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.validation.constraints.NotNull;

public interface ReplyCallback {

    void sendReply(@NotNull SendMessage message);
}
