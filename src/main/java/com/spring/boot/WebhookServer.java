package com.spring.boot;
import com.spring.boot.bot.CvWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



public class WebhookServer {
    public static void main(String[] args) {
        try {
            String webhookUrl = "https://stale-states-occur.loca.lt";

            SetWebhook setWebhook = SetWebhook.builder()
                    .url(webhookUrl)
                    .build();

            CvWebhookBot bot = new CvWebhookBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot, setWebhook);

            System.out.println("Webhook bot is now active at " + webhookUrl);
            System.out.println("Bot is ready. Press Ctrl+C to stop.");
            Thread.sleep(Long.MAX_VALUE);

        } catch (TelegramApiException | InterruptedException  e) {
            e.printStackTrace();
        }
    }
}
