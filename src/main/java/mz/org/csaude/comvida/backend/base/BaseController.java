package mz.org.csaude.comvida.backend.base;

import io.micronaut.http.annotation.Controller;
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

}
