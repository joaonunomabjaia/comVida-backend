package mz.org.csaude.comvida.backend.service;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.repository.UserRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;

import java.util.List;
import java.util.Optional;

@Singleton
public class UserService extends BaseService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public Page<User> findAll(@Nullable Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchByName(String name, Pageable pageable) {
        return userRepository.findByUsernameIlike("%" + name + "%", pageable);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Transactional
    public User create(User user) {
        user.setCreatedAt(DateUtils.getCurrentDate());
        user.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        Optional<User> existing = userRepository.findByUuid(user.getUuid());
        if (existing.isEmpty()) throw new RuntimeException("User not found");

        User toUpdate = existing.get();
        toUpdate.setUsername(user.getUsername());
        toUpdate.setPassword(user.getPassword());
        toUpdate.setStatus(user.getStatus());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(user.getUpdatedBy());

        return userRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<User> existing = userRepository.findByUuid(uuid);
        existing.ifPresent(userRepository::delete);
    }

    @Transactional
    public User updateLifeCycleStatus(String uuid, LifeCycleStatus status) {
        Optional<User> existing = userRepository.findByUuid(uuid);
        if (existing.isEmpty()) throw new RuntimeException("User not found");

        User user = existing.get();
        user.setLifeCycleStatus(status);
        user.setUpdatedAt(DateUtils.getCurrentDate());
        return userRepository.update(user);
    }

    public Optional<User> getByUserName(String identity) {
        return userRepository.findByUsername(identity);
    }
}
