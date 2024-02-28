package org.easybot.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Table(name = "telegram_user")
@Setter
@Getter
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @Column(name = "user_id")
    private Long userId;

    @Nonnull
    @Column(name = "chat_id")
    private Long chatId;

    @Nullable
    @Column(name = "first_name")
    private String firstName;

    @Nullable
    @Column(name = "language_code")
    private String languageCode;

    @Nullable
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private Locale locale;


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

}
