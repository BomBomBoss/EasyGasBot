package org.easybot;

import com.vdurmont.emoji.EmojiParser;

public class CommonTexts {

    public static String parseTextWithEmoji(String text)
    {
        return EmojiParser.parseToUnicode(text);
    }


    /************************ Bot Commands *****************************************************/
    public static final String NESTE_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":one:" + " цены на заправках Neste");
    public static final String CIRCLE_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":two:" + " цены на заправках Circle");
    public static final String VIADA_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":three:" + " цены на заправках Viada");
    public static final String VIRSI_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":four:" + " цены на заправках Virsi");
    public static final String BRANDS_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":white_check_mark:" + " выбор бренда заправки");
    public static final String CHEAPEST_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":money_with_wings:" + " выбор вида топлива");
    public static final String HELP_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":sos:" + " нужна помощь?");
    public static final String START_COMMAND_DISCLAIMER = """
            Добро пожаловать *%s* на просторы бота EasyGas.
            Здесь ты сможешь посмотреть, где сейчас продаётся самое дешёвое топливо среди заправок NESTE, CIRCLE_K, VIADA, VIRŠI.
            Команды, которые сейчас доступны:
            
            /brands - выбор заправки
            /cheapest - выбор топлива
            /help - помощь
            
            """;
    public static final String CHEAPEST_COMMAND_DISCLAIMER = """
                Цены будут показаны в порядке возрастания.
                *Пожалуйста, выберите тип топлива*
                """;

    public static final String STATION_BRANDS_DISCLAIMER = """
                Будет показан список адресов, видов топлива и цен, где в данный момент самая низкая цена.
                *Пожалуйста, выберите бренд заправочной станции*
                """;
    public static final String HELP_DISCLAIMER = EmojiParser.parseToUnicode("""
                Тут всё очень просто! :blush:
                Хочешь посмотреть какие цены на заправках? Нажимай: /brands
                Хочешь узнать, где самый дешёвый бензин или дизель среди заправок? Тогда: /cheapest
                А если хочешь сразу перейти к конкретной заправке, то это ещё проще! Жми одну из этих команд:
                /neste, /circle\\_k /neste, /virsi
                """);

    /************************ Replaced Text *****************************************************/
    public static final String VIRSI_ALL_STATIONS = "Visā Viršu tīklā";
    public static final String ALL_RIGA_DUS_EQUALS_1 = "Visās Rīgas DUS cenas ir vienādas";
    public static final String ALL_RIGA_DUS_EQUALS_2 = "Visos Rīgas DUS degvielas cenas ir vienādas";
    public static final String VIADA_ALL_STATIONS = "Visās VIADA uzpildes stacijās.";


    /************************ Response Text *****************************************************/

    public static final String RESPONSE_COMMAND_NOT_FOUND = "Command %s *NOT FOUND*. Please try another command";
    public static final String RESPONSE_COMMAND_NOT_FOUND_RU = "Ваша команда: *%s* не найдена. Попробуйте другую команду или проверьте список доступных команд тут /help";
    public static final String UNABLE_TO_PROCEED_RESPONSE = "На данный момент нет возможности предоставить информацию по ценам для данной заправки. Пожалуйста попробуйте позже.";
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

    /************************ Localization Text *****************************************************/
    public static final String START_COMMAND_DISCLAIMER_LABEL= "start_command_disclaimer_lbl";
    public static final String START_COMMAND_PRICES_ADD = "start_command_prices_add";
    public static final String HELP_DISCLAIMER_LABEL = "help_command_disclaimer_lbl";
    public static final String CHEAPEST_COMMAND_DISCLAIMER_LABEL = "cheapest_command_disclaimer_lbl";
    public static final String STATION_BRANDS_DISCLAIMER_LABEL = "station_brands_disclaimer_lbl";
    public static final String RESPONSE_COMMAND_NOT_FOUND_LABEL = "response_command_not_found_lbl";
    public static final String RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL = "response_all_riga_dus_price_equals_lbl";
    public static final String UNABLE_TO_PROCEED_RESPONSE_LABEL = "unable_to_proceed_response_lbl";
}
