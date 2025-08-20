package mz.org.csaude.comvida.backend.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.api.RESTAPIMapping;
import mz.org.csaude.comvida.backend.api.response.PaginatedResponse;
import mz.org.csaude.comvida.backend.api.response.SuccessResponse;
import mz.org.csaude.comvida.backend.base.BaseController;
import mz.org.csaude.comvida.backend.dto.CohortMemberDTO;
import mz.org.csaude.comvida.backend.dto.CohortWithMembersDTO;
import mz.org.csaude.comvida.backend.dto.ProgramDTO;
import mz.org.csaude.comvida.backend.entity.CohortMember;
import mz.org.csaude.comvida.backend.entity.Program;
import mz.org.csaude.comvida.backend.service.CohortMemberService;
import mz.org.csaude.comvida.backend.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(RESTAPIMapping.COHORT_MEMBER_CONTROLLER)
@Tag(name = "Cohort Member", description = "API for managing cohort members")
public class CohortMemberController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CohortMemberController.class);

    @Inject
    private CohortMemberService cohortMemberService;

    @Operation(summary = "List or searchcohort members (paginated)")
    @Get
    public HttpResponse<?> listOrSearch(@Nullable @QueryValue("ActivityId") String ActivityId,
                                        @Nullable Pageable pageable) {
        LOG.info("listOrSearch");

        Page<CohortMember> cohortMembers = !Utilities.stringHasValue(ActivityId)
                ? cohortMemberService.findAll(resolvePageable(pageable))
                : cohortMemberService.searchByActivity(ActivityId, resolvePageable(pageable));

        List<CohortMemberDTO> dtos = cohortMembers.getContent().stream()
                .map(CohortMemberDTO::new)
                .collect(Collectors.toList());

        String message = cohortMembers.getTotalSize() == 0
                ? "Sem Dados para esta pesquisa"
                : "Dados encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(
                        dtos, // sempre passar lista (mesmo vazia)
                        cohortMembers.getTotalSize(),
                        cohortMembers.getPageable(),
                        message
                )
        );
    }


    @Operation(summary = "Get cohort member by ID")
    @Get("/{id}")
    public HttpResponse<?> getById(@PathVariable Long id) {
        Optional<CohortMember> member = cohortMemberService.findById(id);

        return member.map(cm ->
                HttpResponse.ok(
                        SuccessResponse.of("Membro encontrado com sucesso", new CohortMemberDTO(cm))
                )
        ).orElse(HttpResponse.notFound());
    }

    @Operation(summary = "Paginated list of members by cohort")
    @Get("/by-cohort")
    public HttpResponse<?> getByCohort(@QueryValue("cohortId") Long cohortId,
                                       @Nullable Pageable pageable) {

        Page<CohortMember> pageResult = cohortMemberService.findByCohortId(cohortId, pageable);

        List<CohortMemberDTO> dtos = pageResult.getContent()
                .stream()
                .map(CohortMemberDTO::new)
                .collect(Collectors.toList());

        String message = pageResult.getTotalSize() == 0
                ? "Sem membros para esta coorte"
                : "Membros encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, pageResult.getTotalSize(), pageResult.getPageable(), message)
        );
    }

    @Operation(summary = "Paginated list of members by cohort and file")
    @Get("/by-cohort-and-file/")
    public HttpResponse<?> findByCohortIdAndPatientImportFileId(@QueryValue("cohortId") Long cohortId, @QueryValue("fileId") Long fileId,
                                       @Nullable Pageable pageable) {

        Page<CohortMember> pageResult = cohortMemberService.findByCohortIdAndPatientImportFileId(cohortId, fileId, pageable);

        List<CohortMemberDTO> dtos = pageResult.getContent()
                .stream()
                .map(CohortMemberDTO::new)
                .collect(Collectors.toList());

        String message = pageResult.getTotalSize() == 0
                ? "Sem membros para esta coorte"
                : "Membros encontrados";

        return HttpResponse.ok(
                PaginatedResponse.of(dtos, pageResult.getTotalSize(), pageResult.getPageable(), message)
        );
    }
}
