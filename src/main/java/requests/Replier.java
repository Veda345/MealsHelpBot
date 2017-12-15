package requests;

import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;

public interface Replier {

    String DONT_KNOW_MSG = "Sorry, I don't know";
    String RETRY_MSG = "Sorry, try again";
    String RETRY_FIND = "There is no such recipe. Please, try another one";

    void initCall(@NotNull Update update);

    void reply(@NotNull Update update);
}
