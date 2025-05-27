package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.HomeVisit;
import mz.org.csaude.comvida.backend.service.HomeVisitService;

import java.util.List;
import java.util.Optional;

@Controller(RESTAPIMapping.HOME_VISIT_CONTROLLER)
public class HomeVisitController {

    @Inject
    private HomeVisitService service;

    @Get("/")
    public List<HomeVisit> findAll() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<HomeVisit> findById(@PathVariable Long id) {
        Optional<HomeVisit> visit = service.findById(id);
        return visit.map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

//    @Post("/")
//    public HttpResponse<HomeVisit> create(@Body HomeVisit data) {
//        HomeVisit saved = service.save(data);
//        return HttpResponse.created(saved);
//    }

    @Put("/{id}")
    public HttpResponse<HomeVisit> update(@PathVariable Long id, @Body HomeVisit data) {
        data.setId(id);
        HomeVisit updated = service.update(data);
        return HttpResponse.ok(updated);
    }

//    @Delete("/{id}")
//    public HttpResponse<?> delete(@PathVariable Long id) {
//        service.deleteById(id);
//        return HttpResponse.noContent();
//    }
}
