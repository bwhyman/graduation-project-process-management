package com.example.graduationprojectprocessmanagement.controller.exception;


import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.vo.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

//@Component
//@Order(-2)
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @NonNull
    @SneakyThrows
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        Code code = Code.FORBIDDEN;
        if (ex instanceof XException e) {
            code = e.getCode();
        }
        String result = objectMapper.writeValueAsString(ResultVO.error(code));
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Flux.just(wrap));

        /*log.debug("ExceptionHandler");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ResultVO.error(code)).doOnSuccess(r -> {
                    log.debug(r.toString());
                }).then();*/


    }
}
