package com.zachrrichards.CryptoAlerts.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zachrrichards.CryptoAlerts.dao.AlertRepository;
import com.zachrrichards.CryptoAlerts.dao.TickerRepository;
import com.zachrrichards.CryptoAlerts.models.Alert;
import com.zachrrichards.CryptoAlerts.models.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ScheduledJobs {

    @Autowired
    private TickerRepository tickerRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private EmailService emailService;

    private static final String URL = "https://api.binance.com/api/v1/ticker/24hr";
    private RestTemplate rest = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Scheduled(fixedRate = 60000)
    public void getTickers() throws IOException {

        ResponseEntity<String> json = rest.getForEntity(URL, String.class);
        JsonNode tickers = mapper.readTree(json.getBody());

        for(JsonNode ticker : tickers) {
            String symbol = ticker.path("symbol").asText();
            double lastPrice = ticker.path("lastPrice").asDouble();
            String formatPrice = ticker.path("lastPrice").asText();

            Ticker grabTicker = tickerRepository.findBySymbol(symbol);

            if(grabTicker == null) {
                Ticker newTicker = new Ticker(symbol, lastPrice, formatPrice);
                tickerRepository.save(newTicker);
            } else {
                grabTicker.setLastPrice(lastPrice);
                grabTicker.setFormatPrice(formatPrice);
                tickerRepository.save(grabTicker);
            }
        }

    }

    @Scheduled(fixedRate = 60000)
    public void alerts() {

        for(Alert alert : alertRepository.findAll()) {
            String symbol = alert.getSymbol();
            Ticker ticker = tickerRepository.findBySymbol(symbol);

            boolean price;
            double alertPrice = alert.getPrice();
            double tickerPrice = ticker.getLastPrice();
            String title;

            if(alert.isGreater()) {
                price = tickerPrice > alertPrice;
                title = " above ";
            } else {
                price = tickerPrice < alertPrice;
                title = " below ";
            }

            if(price) {

                SimpleMailMessage registrationEmail = new SimpleMailMessage();
                registrationEmail.setTo(alert.getUser().getEmail());
                registrationEmail.setSubject("CryptoAlerts | The price of " + symbol + " is" + title + alert.getFormatPrice());
                registrationEmail.setText(symbol + " is" + title + "your alert price of " + alert.getFormatPrice()
                        + " and the the current price is " + ticker.getFormatPrice() + " on Binance.");
                registrationEmail.setFrom("zacharyrrichards@gmail.com");

                emailService.sendEmail(registrationEmail);

                alertRepository.delete(alert);
            }

        }

    }

}
