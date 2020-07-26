package com.weking.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weking.core.models.ResponseResult;
import com.weking.core.models.Ticket;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Jim Cen
 * @date 2020/7/16 11:03
 */
public class ApiCallerServer {
    public final String PROTOCOL ="http://";
    private CryptoService cryptoService;
    public ApiCallerServer(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @SneakyThrows
    public ResponseResult call(String url, Object body) {
        long timeStamp = System.currentTimeMillis();
        Ticket ticket = cryptoService.generateTicket(timeStamp);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("ticket",objectMapper.writeValueAsString(ticket));
        HttpEntity<String> request = new HttpEntity(body, headers);
        return restTemplate.postForEntity(url,request,ResponseResult.class)
                           .getBody();
    }

    @SneakyThrows
    public Mono<ClientResponse> request(String url, Object body) {
        long timeStamp = System.currentTimeMillis();
        Ticket ticket = cryptoService.generateTicket(timeStamp);
        ObjectMapper objectMapper = new ObjectMapper();
        return WebClient.create()
                .post()
                .uri(url)
                .body(Mono.just(body),body.getClass())
                .header("ticket",objectMapper.writeValueAsString(ticket))
                .exchange();
    }
}
