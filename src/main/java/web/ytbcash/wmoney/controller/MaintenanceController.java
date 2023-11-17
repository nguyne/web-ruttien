package web.ytbcash.wmoney.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${heroku.api.key}")
    private String apiKey;

    @Value("${heroku.app.name}")
    private String appName;

    @PostMapping("/on")
    public String turnOnMaintenanceMode() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.heroku+json; version=3"); // Thêm header này

        String payload = "{\"updates\":[{\"type\":\"web\",\"maintenance\":true}]}";
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        String url = String.format("https://api.heroku.com/apps/%s/formation", appName);
        restTemplate.postForObject(url, request, String.class);

        return "Maintenance Mode is turned ON";
    }

    @PostMapping("/off")
    public String turnOffMaintenanceMode() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/vnd.heroku+json; version=3"); // Thêm header này

        String payload = "{\"updates\":[{\"type\":\"web\",\"maintenance\":false}]}"; // Chuyển 'maintenance' thành 'false'
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        String url = String.format("https://api.heroku.com/apps/%s/formation", appName);
        restTemplate.postForObject(url, request, String.class);

        return "Maintenance Mode is turned OFF";
    }
}
