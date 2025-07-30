package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.dto.CohortWithMembersDTO;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.service.CohortMemberService;

import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.COHORT_MEMBER_CONTROLLER)
public class CohortMemberController {

    @Inject
    private CohortMemberService cohortMemberService;

    @Get("/")
    public List<CohortMember> getAll() {
        return cohortMemberService.findAll();
    }

    @Get("/{id}")
    public HttpResponse<CohortMember> getById(@PathVariable Long id) {
        Optional<CohortMember> member = cohortMemberService.findById(id);
        return member.map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

//    @Post("/")
//    public HttpResponse<CohortMember> create(@Body CohortMember cohortMember) {
//        CohortMember saved = cohortMemberService.save(cohortMember);
//        return HttpResponse.created(saved);
//    }

    @Put("/{id}")
    public HttpResponse<CohortMember> update(@PathVariable Long id, @Body CohortMember cohortMember) {
        cohortMember.setId(id);
        CohortMember updated = cohortMemberService.update(cohortMember);
        return HttpResponse.ok(updated);
    }

    @Get("/cohorts-with-members")
    public Page<CohortWithMembersDTO> findAllWithMembers(Pageable pageable) {
        return cohortMemberService.getCohortsWithMembers(pageable);
    }

}
