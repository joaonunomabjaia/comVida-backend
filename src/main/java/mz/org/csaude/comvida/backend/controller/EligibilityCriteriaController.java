package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.EligibilityCriteria;
import mz.org.csaude.comvida.backend.service.EligibilityCriteriaService;

import java.util.List;
import java.util.Optional;

@Controller(RESTAPIMapping.ELIGIBILITY_CRITERIA_CONTROLLER)
public class EligibilityCriteriaController {

    @Inject
    private EligibilityCriteriaService service;

    @Get("/")
    public List<EligibilityCriteria> findAll() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<EligibilityCriteria> findById(@PathVariable Long id) {
        Optional<EligibilityCriteria> criteria = service.findById(id);
        return criteria.map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

//    @Post("/")
//    public HttpResponse<EligibilityCriteria> create(@Body EligibilityCriteria data) {
//        EligibilityCriteria saved = service.save(data);
//        return HttpResponse.created(saved);
//    }

    @Put("/{id}")
    public HttpResponse<EligibilityCriteria> update(@PathVariable Long id, @Body EligibilityCriteria data) {
        data.setId(id);
        EligibilityCriteria updated = service.update(data);
        return HttpResponse.ok(updated);
    }

//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable Long id) {
//        service.deleteById(id);
//        return HttpResponse.noContent();
//    }
}
