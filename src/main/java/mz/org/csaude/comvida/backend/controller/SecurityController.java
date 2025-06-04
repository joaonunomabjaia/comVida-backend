package mz.org.csaude.comvida.backend.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.authentication.Authentication;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Map;

@Controller("/me")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Security", description = "Endpoints related to authentication and logged-in user")
public class SecurityController {

    @Get
    @Operation(summary = "Logged-in user information",
            description = "Returns details about the currently authenticated user based on the JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved authenticated user information")
    public Map<String, Object> me(Authentication authentication) {
        return authentication.getAttributes();
    }
}
