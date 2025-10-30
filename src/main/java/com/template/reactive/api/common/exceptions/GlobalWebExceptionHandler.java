package com.template.reactive.api.common.exceptions;

import com.template.reactive.api.common.AppConstants;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GlobalWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GlobalWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                                     ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.serverCodecConfigurer = serverCodecConfigurer;
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STATUS
        );

        final Map<String, Object> rawErrors = getErrorAttributes(request, errorAttributeOptions);
        return processError(rawErrors);
    }

    private Mono<ServerResponse> processError(Map<String, Object> errorDetails) {
        if (errorDetails.get("exception").equals(ExpiredJwtException.class.getCanonicalName())) {
            String body = String.format(AppConstants.ERROR_MESSAGE_FORMAT, "Authorization token expired.");
            return ServerResponse
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body));
        }

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(AppConstants.DEFAULT_ERROR_MESSAGE));
    }
}
