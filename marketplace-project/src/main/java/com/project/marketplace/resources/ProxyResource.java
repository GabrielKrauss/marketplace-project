package com.project.marketplace.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy")
public class ProxyResource {

    private RestTemplate restTemplate = new RestTemplate();

    public ProxyResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/download")
    @PreAuthorize("hasAuthority('Operator')")
    public ResponseEntity<byte[]> download(@RequestParam String url) {
        // IMPORTANTE: valide a URL para garantir que seja segura e esperada (por exemplo, que pertença ao Pastebin).
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        HttpHeaders headers = new HttpHeaders();
//        headers.add("Access-Control-Allow-Origin", "*");

        // Copia o Content-Type da resposta original, se disponível.
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType != null) {
            headers.setContentType(contentType);
        }
        
        // Se desejar, copie outros cabeçalhos importantes.

        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }
}
