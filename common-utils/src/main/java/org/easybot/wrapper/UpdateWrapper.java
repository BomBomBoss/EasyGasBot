package org.easybot.wrapper;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;


public record UpdateWrapper(Update update, User user) {

}
