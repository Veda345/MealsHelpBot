package requests;

import org.telegram.telegrambots.api.objects.Update;

public interface Replier {

    String DONT_KNOW_MSG = "Sorry, I don't know";
    String RETRY_MSG = "Sorry, try again";

    void initCall(Update update);

    void reply(Update update);
}
