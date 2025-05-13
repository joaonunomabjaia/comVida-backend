package mz.org.csaude.comvida.backend;

import io.micronaut.http.annotation.*;

@Controller("/comvida")
public class ComvidaController {

    @Get(uri = "/", produces = "text/plain")
    public String index() {
        return "Example Response";
    }
}