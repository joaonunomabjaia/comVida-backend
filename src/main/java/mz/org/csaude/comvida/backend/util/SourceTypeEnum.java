package mz.org.csaude.comvida.backend.util;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public enum SourceTypeEnum {
    FILE,
    INTEGRATION
}
