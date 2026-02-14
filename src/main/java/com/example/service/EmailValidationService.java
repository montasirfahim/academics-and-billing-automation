package com.example.service;

import com.example.entity.EmailCheckResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EmailValidationService {

    private final RestClient restClient;
    private final String apiKey;

    public EmailValidationService(@Value("${app.email.api.key}") String apiKey){
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
    }

    public boolean isEmailDeliverable(String email){
        try {
            EmailCheckResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("emailreputation.abstractapi.com")
                            .path("/v1/")
                            .queryParam("api_key", apiKey)
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .body(EmailCheckResponse.class);

            if(response != null && response.getDeliverability() != null){
                boolean isDeliverable = "deliverable".equalsIgnoreCase(response.getDeliverability().getStatus());
                boolean isNotDisposable = response.getQuality() != null && !response.getQuality().isDisposable();

                return isDeliverable && isNotDisposable;
            }
            return true;
        } catch(Exception e){
            System.err.println(e.getMessage());
            return true;
        }
    }
}