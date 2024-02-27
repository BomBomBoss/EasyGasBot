package org.easybot.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nonnull
    @Column(name = "user_id")
    private Long userId;
    @Nullable
    @Column(name = "first_name")
    private String firstName;
    @Nullable
    @Column(name = "language_code")
    private String languageCode;
    @Transient
    private Locale locale;

    @Nonnull
    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(@Nonnull Long userId)
    {
        this.userId = userId;
    }

    @Nullable
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(@Nullable String firstName)
    {
        this.firstName = firstName;
    }

    @Nullable
    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public void setLanguageCode(@Nullable String languageCode)
    {
        this.languageCode = languageCode;
    }

    public void resolveLocaleFromLanguageCode(String languageCode)
    {
        if (languageCode == null || languageCode.isEmpty())
        {
            setLocale(Locale.ENGLISH);
        }
        else
        {
            setLocale(Locale.of(languageCode));
        }
    }

    public Locale getLocale()
    {
        return locale;
    }
}
