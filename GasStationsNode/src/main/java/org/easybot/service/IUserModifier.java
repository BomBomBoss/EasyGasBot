package org.easybot.service;

import org.easybot.util.Modifier;

import java.lang.reflect.InvocationTargetException;

public interface IUserModifier {

    String getOriginalTitle(Modifier modifier) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
