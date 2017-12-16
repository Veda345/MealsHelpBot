package mealsbot.requests;

import com.google.common.collect.Multimap;
import com.sun.istack.internal.Nullable;
import mealsbot.bot.ReplyCallback;
import mealsbot.data.Recipe;
import mealsbot.data.RecommendCache;
import mealsbot.http.RecipesRequester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindReplyTest {
    private RecommendCache recommendCache = mock(RecommendCache.class);
    private ReplyCallback replyCallback = mock(ReplyCallback.class);
    private Message message = mock(Message.class);
    private Update update = mock(Update.class);
    private Recipe recipe = mock(Recipe.class);
    private Multimap<String, String> allTitleRecipes = mock(Multimap.class);;
    private RecipesRequester recipesRequester = mock(RecipesRequester.class);
    private FindReply reply = new FindReply(recipesRequester, recommendCache);

    @Before
    public void setUp() {
        reply.setReplyCallback(replyCallback);

        User user = mock(User.class);
        Integer userId = 2;

        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("test");
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(recommendCache.getRecommended(userId)).thenReturn(recipe);
    }

    @Test
    public void test_initCall() throws Exception {
        reply.initCall(update);

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(replyCallback).sendReply(sendMessageCaptor.capture());
        Assert.assertEquals("What recipe do you want to find?",
                sendMessageCaptor.getValue().getText());
    }
}