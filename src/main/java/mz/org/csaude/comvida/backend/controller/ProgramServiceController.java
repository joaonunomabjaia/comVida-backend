package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.entity.ProgramService;
import mz.org.csaude.comvida.backend.service.ProgramServiceService;

import java.util.List;
import java.util.Optional;

@Controller(RESTAPIMapping.PROGRAM_SERVICE_CONTROLLER)
public class ProgramServiceController {

    private final ProgramServiceService service;

    public ProgramServiceController(ProgramServiceService service) {
        this.service = service;
    }

    @Get
    public List<ProgramService> listAll() {
        return service.findAll();
    }

    @Get("/{id}")
    public HttpResponse<ProgramService> getById(@PathVariable Long id) {
        Optional<ProgramService> result = service.findById(id);
        return result.map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

    @Post
    public HttpResponse<ProgramService> create(@Body @Valid ProgramService programService) {
        return HttpResponse.created(service.create(programService));
    }

    @Put("/{id}")
    public HttpResponse<ProgramService> update(@PathVariable Long id, @Body @Valid ProgramService programService) {
        return service.findById(id)
                .map(existing -> {
                    programService.setId(id);
                    return HttpResponse.ok(service.create(programService));
                })
                .orElse(HttpResponse.notFound());
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable Long id) {
        service.deleteById(id);
        return HttpResponse.noContent();
    }
}
