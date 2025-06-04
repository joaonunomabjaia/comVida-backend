package mz.org.csaude.comvida.backend.service;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.Setting;
import mz.org.csaude.comvida.backend.repository.SettingRepository;

import java.util.Optional;

@Singleton
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public Optional<Setting> findByDesignation(String designation) {
        return settingRepository.findByDesignation(designation);
    }

    public Page<Setting> findAll(Pageable pageable) {
        return settingRepository.findAll(pageable);
    }

    public Page<Setting> searchByDesignation(String designation, Pageable pageable) {
        return settingRepository.findByDesignationIlike("%" + designation + "%", pageable);
    }

    public Optional<Setting> findById(Long id) {
        return settingRepository.findById(id);
    }

    @Transactional
    public Setting create(Setting setting) {
        return settingRepository.save(setting);
    }

    public Setting updateSetting(Setting setting) {
        return null;
    }

    public void delete(String uuid) {

    }
}
