package mz.org.csaude.comvida.backend.base;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


@Controller
public abstract class BaseController {

    protected <T extends BaseEntity, S extends BaseEntityDTO> List<S> listAsDtos(List<T> entities, Class<S> baseEntityDTOClass) {
        if (!Utilities.listHasElements(entities)) return new ArrayList<>();
        try {
            return Utilities.parseList(entities, baseEntityDTOClass);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected Pageable resolvePageable(Pageable pageable) {
        if (pageable == null || pageable.getSize() == Integer.MAX_VALUE) {
            return Pageable.from(0, 50); // Default to page 0 with 50 items
        }
        return pageable;
    }

    // Este método pode ser mantido caso você queira usá-lo em cenários internos (ex: integração especial),
    // mas não será mais necessário usar em Controllers com os ExceptionHandlers globais.
    protected HttpResponse<ComVidaAPIError> buildErrorResponse(Exception e) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        if (rootCause == null) {
            rootCause = e;
        }

        String userMessage = "Ocorreu um erro inesperado. Por favor, tente novamente ou contacte o administrador do sistema.";
        String technicalMessage = rootCause.getMessage() != null ? rootCause.getMessage() : e.getMessage();

        return HttpResponse.badRequest(ComVidaAPIError.builder()
                .status(HttpStatus.BAD_REQUEST.getCode())
                .message(userMessage)
                .error(technicalMessage)
                .build());
    }
}

