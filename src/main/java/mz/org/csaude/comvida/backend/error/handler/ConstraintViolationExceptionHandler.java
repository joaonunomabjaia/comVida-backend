package mz.org.csaude.comvida.backend.error.handler;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
public class ConstraintViolationExceptionHandler implements ExceptionHandler<ConstraintViolationException, HttpResponse<ComVidaAPIError>> {

    private static final Logger LOG = LoggerFactory.getLogger(ConstraintViolationExceptionHandler.class);

    @Override
    public HttpResponse<ComVidaAPIError> handle(HttpRequest request, ConstraintViolationException exception) {
        LOG.warn("ConstraintViolationException: {}", exception.getMessage());

        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(
                ComVidaAPIError.builder()
                        .status(HttpStatus.BAD_REQUEST.getCode())
                        .message("Já existe um registo com o mesmo valor de um dos campos únicos ou uma constraint foi violada. Verifique e tente novamente.")
                        .error(exception.getMessage())
                        .build()
        );
    }
}
