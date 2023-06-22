package org.easybot.exceptions;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateException extends RuntimeException{
    private Update update;

    public UpdateException(String message, Update update)
    {
        super(message);
        this.update = update;
    }


    public UpdateException(Update update)
    {
        this.update = update;
    }

    public Update getUpdate()
    {
        return update;
    }
}
