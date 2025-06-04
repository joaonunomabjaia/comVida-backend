package mz.org.csaude.comvida.backend.base;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import mz.org.csaude.comvida.backend.error.ComVidaAPIError;
import mz.org.csaude.comvida.backend.util.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


@Controller
public abstract class BaseController {

    protected  <T extends BaseEntity, S extends BaseEntityDTO> List<S> listAsDtos(List<T> entities, Class<S> baseEntityDTOClass) {
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

    protected HttpResponse<ComVidaAPIError> buildErrorResponse(Exception e) {
        return HttpResponse.badRequest(ComVidaAPIError.builder()
                .status(HttpStatus.BAD_REQUEST.getCode())
                .message(e.getMessage())
                .error(e.getLocalizedMessage())
                .build());
    }
}
