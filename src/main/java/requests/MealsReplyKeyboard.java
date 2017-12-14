package requests;

import bot.MealsBotCommands;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class MealsReplyKeyboard {

    @NotNull
    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    public MealsReplyKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>(2);
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();

        firstRow.add(MealsBotCommands.CAL.getCommandName());
        firstRow.add(MealsBotCommands.PFC.getCommandName());
        firstRow.add(MealsBotCommands.FAV.getCommandName());

        secondRow.add(MealsBotCommands.RECOMMEND.getCommandName());
        secondRow.add(MealsBotCommands.ADDTOFAV.getCommandName());
        secondRow.add(MealsBotCommands.CLEAR.getCommandName());
        secondRow.add(MealsBotCommands.HELP.getCommandName());

        rows.add(firstRow);
        rows.add(secondRow);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
    }

    @NotNull
    public ReplyKeyboardMarkup getKeyboardMarkup() {
        return keyboardMarkup;
    }
}
