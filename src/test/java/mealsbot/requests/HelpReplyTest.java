package mealsbot.requests;

import mealsbot.bot.ReplyCallback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HelpReplyTest {
    private ReplyCallback replyCallback = mock(ReplyCallback.class);
    private Message message = mock(Message.class);
    private Update update = mock(Update.class);

    private HelpReply reply = new HelpReply();

    @Before
    public void setUp() {
        reply.setReplyCallback(replyCallback);

        User user = mock(User.class);
        Integer userId = 2;

        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("test");
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
    }

    @Test
    public void test_initCall() throws Exception {
        reply.initCall(update);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(replyCallback).sendReply(sendMessageCaptor.capture());
        Assert.assertEquals("Please type \'/cal\' for getting calories for product info,\n" +
                        "\'/pfc\' for getting protein, fat and carb for product info,\n" +
                        "\'/recommend\' for getting an advice,\n" +
                        "\'/fav\' for getting your favourite recipes,\n" +
                        "\'/addtofav\' to save your recent recommendation,\n" +
                        "\'/find\' to find recipes with some words in title",
                sendMessageCaptor.getValue().getText());
    }

}