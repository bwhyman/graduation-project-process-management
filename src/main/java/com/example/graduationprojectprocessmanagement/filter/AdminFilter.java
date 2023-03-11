package com.example.graduationprojectprocessmanagement.filter;

import com.example.graduationprojectprocessmanagement.dox.User;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class AdminFilter implements WebFilter {
    PathPattern includes = new PathPatternParser().parse("/api/admin/**");
    private final ResponseHelper responseHelper;

    @NotNull
    @Override
    @SneakyThrows
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (includes.matches(request.getPath().pathWithinApplication())) {
            int role = (int) exchange.getAttributes().get(RequestAttributeConstant.ROLE);
            if (role != User.ROLE_ADMIN) {
                return responseHelper.response(Code.FORBIDDEN, exchange);
            }
        }
        return chain.filter(exchange);
    }

}
