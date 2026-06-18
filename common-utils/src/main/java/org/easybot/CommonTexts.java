package org.easybot;

import com.vdurmont.emoji.EmojiParser;

public class CommonTexts {

    public static String parseTextWithEmoji(String text)
    {
        return EmojiParser.parseToUnicode(text);
    }


    /************************ Bot Commands *****************************************************/
    public static final String BRANDS_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":white_check_mark:" + " выбор бренда заправки");
    public static final String BRANDS_COMMAND_DESCRIPTION_EN = EmojiParser.parseToUnicode(":white_check_mark:" + " choose gas station");
    public static final String CHEAPEST_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":money_with_wings:" + " выбор вида топлива");
    public static final String CHEAPEST_COMMAND_DESCRIPTION_EN = EmojiParser.parseToUnicode(":money_with_wings:" + " choose fuel type");
    public static final String HELP_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":sos:" + " нужна помощь?");
    public static final String HELP_COMMAND_DESCRIPTION_EN = EmojiParser.parseToUnicode(":sos:" + " need help?");
    public static final String LANGUAGE_COMMAND_DESCRIPTION = EmojiParser.parseToUnicode(":earth_africa:" + " выбор языка");
    public static final String LANGUAGE_COMMAND_DESCRIPTION_EN = EmojiParser.parseToUnicode(":earth_africa:" + " choose language");

    /************************ Bot Short Description *****************************************************/

    public static final String BOT_SHORT_DESCRIPTION_RU = """
            Бот, который расскажет, где в Риге самое дешёвое топливо 👌
            Разработчик - @BomBoss 🤘
                        """;

    public static final String BOT_SHORT_DESCRIPTION_EN = """
            Bot that will tell you where are the cheapest fuel is in Riga 👌
            Developer - @BomBoss 🤘
                            """;

    /************************ Admin text *****************************************************/

    public static final String ADMIN_COMMAND_DISCLAIMER = """
            Ты зашёл в режим God Mode.
            Давай глянем сколько там пользователей
            воспользовались ботом за
            """;

    public static final String STATISTICS_COMMAND_DISCLAIMER = """
            Ты зашёл в режим Статистики.
            Выбери когда было самое дешёвое топливо за
            """;

    public static final String STATISTICS_ANSWER = """
            ⛽%s⛽
            
            *%s* - *%s*
            💶 *%s* €
            📅 *%s* (*%s*)
            
            *%s* - *%s*
            💶 *%s* €
            📅 *%s* (*%s*)
            
            *%s* - *%s*
            💶 *%s* €
            📅 *%s* (*%s*)
            """;

    /************************ Replaced Text *****************************************************/
    public static final String VIRSI_ALL_STATIONS = "Visā Viršu tīklā";
    public static final String NESTE_ALL_STATIONS = "Visās DUS cenas vienādas";
    public static final String NESTE_ALL_STATIONS_1 = "Visās stacijās cenas vienādas";
    public static final String NESTE_ALL_STATIONS_2 = "Cena visās stacijās";
    public static final String CIRCLE_ALL_STATIONS = "Visās Rīgas DUS cenas ir vienādas";
    public static final String CIRCLE_ALL_STATIONS_1 = "Visos Rīgas DUS degvielas cenas ir vienādas";
    public static final String CIRCLE_ALL_STATIONS_2 = "Visās Rīgas DUS degvielas cenas ir vienādas";
    public static final String VIADA_ALL_STATIONS = "Visās VIADA uzpildes stacijās.";


    public static final String ALL_STATIONS_1 = "Visās";
    public static final String ALL_STATIONS_2 = "Visā";
    public static final String ALL_STATIONS_3 = "Visos";



    /************************ Util Text *****************************************************/


    public static final String EUR_SIGN_BOLD = " *EUR*";
    public static final String TWO_NEW_LINES = "\n\n";
    public static final String ONE_SPACE = " ";
    public static final String CIRCLE_K_TITLE = "circle_k";
    public static final String CIRCLE_WITHOUT_K_TITLE = "circle";
    public static final String NESTE_TITLE = "neste";
    public static final String VIADA_TITLE = "viada";
    public static final String VIRSI_TITLE = "virsi";
    public static final String STRAUJUPITE_TITLE = "straujupite";
    public static final String RIGA = "Rīga";

    public static final String LANGUAGE_RUS = "RUS " + "\uD83C\uDDF7\uD83C\uDDFA";
    public static final String LANGUAGE_LV = "LV " + "\uD83C\uDDF1\uD83C\uDDFB";
    public static final String LANGUAGE_EN = "EN " + "\uD83C\uDDEC\uD83C\uDDE7";

    public static final String GENERAL_ERROR_PARSING_MESSAGE = "Error occurred during '%s' gas type parsing with message '%s'";

    /************************ Localization Text *****************************************************/
    public static final String START_COMMAND_DISCLAIMER_LABEL= "start_command_disclaimer_lbl";
    public static final String START_COMMAND_PRICES_ADD = "start_command_prices_add";
    public static final String HELP_DISCLAIMER_LABEL = "help_command_disclaimer_lbl";
    public static final String CHEAPEST_COMMAND_DISCLAIMER_LABEL = "cheapest_command_disclaimer_lbl";
    public static final String STATION_BRANDS_DISCLAIMER_LABEL = "station_brands_disclaimer_lbl";
    public static final String RESPONSE_COMMAND_NOT_FOUND_LABEL = "response_command_not_found_lbl";
    public static final String RESPONSE_ALL_RIGA_DUS_EQUALS_LABEL = "response_all_riga_dus_price_equals_lbl";
    public static final String UNABLE_TO_PROCEED_RESPONSE_LABEL = "unable_to_proceed_response_lbl";
    public static final String LANGUAGE_COMMAND_DISCLAIMER_LABEL = "language_command_disclaimer_lbl";
    public static final String LANGUAGE_IS_SET_LABEL = "language_is_set_lbl";
    public static final String LANGUAGE_TO_SET_LABEL = "language_to_set_lbl";
    public static final String RESPONSE_ADDRESS_LABEL = "address_lbl";
    public static final String RESPONSE_PRICE_LABEL = "price_lbl";
    public static final String RESPONSE_NOT_SUPPORTED_UPDATE_LABEL = "not_supported_update_lbl";
    public static final String RESPONSE_STATISTICS_LABEL = "statistics_lbl";
    public static final String WEEKLY_NOTIFICATION_STATISTICS_LABEL = "weekly_statistics_lbl";


}
