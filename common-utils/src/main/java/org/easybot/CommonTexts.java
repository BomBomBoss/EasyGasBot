package org.easybot;

import com.vdurmont.emoji.EmojiParser;

import javax.ws.rs.PUT;

public class CommonTexts {


    /************************ Bot Commands *****************************************************/
    public static final String NESTE_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Neste");
    public static final String CIRCLE_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Circle");
    public static final String VIADA_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Viada");

    public static final String VIRSI_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":fuelpump:" + "цены на заправках Virsi");
    public static final String START_COMMAND_DESCRIPTION = "Добро пожаловать *%s* на просторы бота EasyGas.\n" +
            "Здесь ты сможешь посмотреть, где сейчас продаётся самое дешёвое топливо среди заправок NESTE, CIRCLE_K, VIADA, VIRŠI. \n" +
            "Команды, которые сейчас доступны: \n";

    /************************ Replaced Text *****************************************************/
    public static final String VIRSI_ALL_STATIONS = "Visā Viršu tīklā";
    public static final String ALL_RIGA_DUS_EQUALS_1 = "Visās Rīgas DUS cenas ir vienādas";
    public static final String ALL_RIGA_DUS_EQUALS_2 = "Visos Rīgas DUS degvielas cenas ir vienādas";
    public static final String VIADA_ALL_STATIONS = "Visās VIADA uzpildes stacijās.";


    /************************ Response Text *****************************************************/

    public static final String RESPONSE_COMMAND_NOT_FOUND = "Command %s *NOT FOUND*. Please try another command";
    public static final String RESPONSE_ALL_RIGA_DUS_EQUALS = "Цены на всех заправках одинаковые";
    public static final String RESPONSE_ADDRESS_EQUALS = "адрес= ";
    public static final String RESPONSE_PRICE_EQUALS = "цена= ";
    public static final String RESPONSE_EUR_SIGN_BOLD = " *EUR*";


    /************************ Util Text *****************************************************/

    public static final String UTIL_LINE_SEPARATOR = "line.separator";
    public static final String CIRCLE_K_TITLE = "circle_k";
    public static final String CIRCLE_WITHOUT_K_TITLE = "circle";
    public static final String NESTE_TITLE = "neste";
    public static final String VIADA_TITLE = "viada";
    public static final String VIRSI_TITLE = "virsi";
}
