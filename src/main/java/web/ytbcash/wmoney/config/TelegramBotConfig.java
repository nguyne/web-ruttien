package web.ytbcash.wmoney.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import web.ytbcash.wmoney.payload.botTele.MyBot;

@Configuration
public class TelegramBotConfig {

    @Bean
    public MyBot myBot(){
        return new MyBot();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(MyBot myBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myBot);
        return botsApi;
    }
}
