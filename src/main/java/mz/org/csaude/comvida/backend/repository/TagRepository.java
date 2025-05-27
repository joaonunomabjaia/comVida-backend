package mz.org.csaude.comvida.backend.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;
import mz.org.csaude.comvida.backend.entity.Tag;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Override
    List<Tag> findAll();

    @Override
    Optional<Tag> findById(@NotNull Long id);

    List<Tag> findByShortName(String shortName);

    Optional<Tag> findByUuid(String uuid);
}
