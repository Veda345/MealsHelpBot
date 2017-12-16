package mealsbot.requests;


import mealsbot.bot.ReplyCallback;
import mealsbot.data.ProductInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CalReplyTest {

    private ReplyCallback replyCallback = mock(ReplyCallback.class);
    private Message message = mock(Message.class);
    private Update update = mock(Update.class);

    private CalReply reply = new CalReply();

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
        Assert.assertEquals(sendMessageCaptor.getValue().getText(), "The caloric value of which product do you want to know?");
    }


    @Test
    public void test_replyCall() throws Exception {
        when(message.getText()).thenReturn("apples");

        reply.reply(update);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(replyCallback).sendReply(sendMessageCaptor.capture());
        Assert.assertEquals(sendMessageCaptor.getValue().getText(), "155 cal per serving (10 rings)");
    }

    @Test
    public void test_replyCallUnknown() throws Exception {
        when(message.getText()).thenReturn("asdfasdfa");

        reply.reply(update);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(replyCallback).sendReply(sendMessageCaptor.capture());
        Assert.assertEquals(sendMessageCaptor.getValue().getText(), "Sorry, I don't know");
    }

}