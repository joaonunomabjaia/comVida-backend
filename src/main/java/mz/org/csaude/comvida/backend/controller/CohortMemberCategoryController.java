package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.entity.CohortMemberCategory;
import mz.org.csaude.comvida.backend.service.CohortMemberCategoryService;

import java.util.List;
import java.util.Optional;

@Controller(RESTAPIMapping.COHORT_MEMBER_CATEGORY_CONTROLLER)
public class CohortMemberCategoryController extends BaseController {

    @Inject
    private CohortMemberCategoryService service;

    @Get("/")
    public List<CohortMemberCategory> getAll() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<CohortMemberCategory> getById(@PathVariable Long id) {
        Optional<CohortMemberCategory> item = service.findById(id);
        return item.map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

//    @Post("/")
//    public HttpResponse<CohortMemberCategory> create(@Body CohortMemberCategory data) {
//        CohortMemberCategory saved = service.save(data);
//        return HttpResponse.created(saved);
//    }

    @Put("/{id}")
    public HttpResponse<CohortMemberCategory> update(@PathVariable Long id, @Body CohortMemberCategory data) {
        data.setId(id);
        CohortMemberCategory updated = service.update(data);
        return HttpResponse.ok(updated);
    }

//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable Long id) {
//        service.deleteById(id);
//        return HttpResponse.noContent();
//    }
}
