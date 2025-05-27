package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Tag;
import mz.org.csaude.comvida.backend.repository.TagRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class TagService extends BaseService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    public Optional<Tag> findByUuid(String uuid) {
        return tagRepository.findByUuid(uuid);
    }

    @Transactional
    public Tag create(Tag tag) {
        tag.setCreatedAt(DateUtils.getCurrentDate());
        tag.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag update(Tag tag) {
        Optional<Tag> existing = tagRepository.findByUuid(tag.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }

        Tag toUpdate = existing.get();
        toUpdate.setShortName(tag.getShortName());
        toUpdate.setDescription(tag.getDescription());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(tag.getUpdatedBy());

        return tagRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Tag> existing = tagRepository.findByUuid(uuid);
        existing.ifPresent(tagRepository::delete);
    }
}
