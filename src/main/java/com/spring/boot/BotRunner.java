package com.spring.boot;

import com.spring.boot.bot.CvBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotRunner {

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new CvBot());
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
