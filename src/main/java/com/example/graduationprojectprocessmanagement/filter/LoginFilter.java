package com.example.graduationprojectprocessmanagement.filter;

import com.example.graduationprojectprocessmanagement.component.JWTComponent;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import com.example.graduationprojectprocessmanagement.vo.RequestAttributeConstant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class LoginFilter implements WebFilter {

    PathPattern includes = new PathPatternParser().parse("/api/**");
    PathPattern excludes = new PathPatternParser().parse("/api/login");

    private final JWTComponent jwtComponent;
    private final ResponseHelper responseHelper;

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        // 允许非数据接口请求。Swagger /swagger-ui/index.html
        if (excludes.matches(request.getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }
        if (!includes.matches(request.getPath().pathWithinApplication())) {
            return responseHelper.response(Code.BAD_REQUEST, exchange);
        }
        String token = request.getHeaders().getFirst(RequestAttributeConstant.TOKEN);
        if (token == null) {
            return responseHelper.response(Code.UNAUTHORIZED, exchange);
        }

        return jwtComponent.decode(token)
                .flatMap(decode -> {
                    exchange.getAttributes().put(RequestAttributeConstant.UID, decode.getClaim(RequestAttributeConstant.UID).asString());
                    exchange.getAttributes().put(RequestAttributeConstant.ROLE, decode.getClaim(RequestAttributeConstant.ROLE).asInt());

                    if (!decode.getClaim(RequestAttributeConstant.GROUP_NUMBER).isMissing()) {
                        exchange.getAttributes().put(RequestAttributeConstant.GROUP_NUMBER, decode.getClaim(RequestAttributeConstant.GROUP_NUMBER).asInt());
                    }
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> responseHelper.response(((XException) e).getCode(), exchange));

        //
        /*DecodedJWT decode = jwtComponent.decode(token);
        exchange.getAttributes().put(RequestAttributeConstant.UID, decode.getClaim(RequestAttributeConstant.UID).asString());
        exchange.getAttributes().put(RequestAttributeConstant.ROLE, decode.getClaim(RequestAttributeConstant.ROLE).asString());

        if (!decode.getClaim(RequestAttributeConstant.GROUP_NUMBER).isMissing()) {
            exchange.getAttributes().put(RequestAttributeConstant.GROUP_NUMBER, decode.getClaim(RequestAttributeConstant.GROUP_NUMBER).asInt());
        }
        return chain.filter(exchange);*/

    }
}
