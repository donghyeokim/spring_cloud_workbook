package com.example.userservice.client.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> null;
            case 404 -> {
                if (methodKey.contains("getOrders")) {
                    yield new ResponseStatusException(HttpStatus.valueOf(response.status()), response.reason());
                }
                yield null;
            }
            default -> new Exception(response.reason());
        };
    }
}
