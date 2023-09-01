package org.easybot;

import com.vdurmont.emoji.EmojiParser;

public class CommonTexts {
    public static final String NESTE_PRICES_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Neste");
    public static final String CIRCLE_PRICES_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Circle");
    public static final String VIADA_PRICES_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Viada");
    public static final String START_COMMAND_DESCRIPTION = "Добро пожаловать *%s* на просторы бота EasyGas.\n" +
            "Здесь ты сможешь посмотреть, где сейчас продаётся самое дешёвое топливо среди заправок NESTE, CIRCLE, VIADA.\n" +
            "Команды, которые сейчас доступны: \n";


}
