package mz.org.csaude.comvida.backend.error.handler;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces
@Singleton
public class GlobalExceptionHandler implements ExceptionHandler<RuntimeException, HttpResponse<ComVidaAPIError>> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<ComVidaAPIError> handle(HttpRequest request, RuntimeException exception) {
        LOG.error("Unhandled RuntimeException: {}", exception.getMessage(), exception);

        return HttpResponse.serverError(
                ComVidaAPIError.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                        .message("Ocorreu um erro inesperado. Por favor, tente novamente ou contacte o administrador do sistema.")
                        .error(exception.getMessage())
                        .build()
        );
    }
}
