package com.example.ExchangeRatesKapigaR_bot.service;

import com.example.ExchangeRatesKapigaR_bot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {

        String currency = "";
        String infoCurrency = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/info":
                    sendMessage(chatId, "Список всех валют");
                    messageText = "/info";
                    try {
                        infoCurrency = Parser.parse(messageText);
                    } catch (Exception e) {
                        sendMessage(chatId, "Возникла непредвиденная ошибка!");
                    }
                    sendMessage(chatId,infoCurrency);
                    break;
                default:
                    try {
                        currency = Parser.parse(messageText);
                    } catch (Exception e) {
                        sendMessage(chatId, "Возникла непредвиденная ошибка!");
                    }
                    if (currency == null){
                        currency = "Что-то пошло не так, попробуй ещё раз";
                    }
                        sendMessage(chatId, currency);
                        commandRepeat(chatId);

            }
        }

    }

    private void startCommandReceived (Long chatId, String name) {
        String answer = "Привет, " + name + ", мой создатель (@KapIgAl) рад тебя видеть тут." + "\n"
                + "У меня ты сможешь посмотреть стоимость валюты в рублях (например, USD) по самому актуальному курсу." + "\n"
                + "Для этого напиши буквенный код валюты и отправь мне, дальше я всё сделаю сам." + "\n"
                + "Для того чтобы посмотреть все актуальные коды введите: /info";
        sendMessage(chatId, answer);
    }
    private void commandRepeat (Long chatId) {
        String answer = "Пожалуйста введите 3-х значный код валюты, для того чтобы я смог показать её курс в рублях, например USD" +
                "\n" + "Для того, чтобы посмотреть все актуальные коды введите: /info";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}

