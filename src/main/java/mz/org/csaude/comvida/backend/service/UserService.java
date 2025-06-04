package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.User;
import mz.org.csaude.comvida.backend.repository.UserRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

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

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Transactional
    public User create(User user) {
        user.setCreatedAt(DateUtils.getCurrentDate());
        user.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return userRepository.save(user);
    }

    @Transactional
    public User update(User user) {
        Optional<User> existing = userRepository.findByUuid(user.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User toUpdate = existing.get();
        toUpdate.setUsername(user.getUsername());
        toUpdate.setPassword(user.getPassword());
        toUpdate.setStatus(user.getStatus());

        // Campos herdados de Person
        toUpdate.setNames(user.getNames());
        toUpdate.setSex(user.getSex());
        toUpdate.setBirthdate(user.getBirthdate());
        toUpdate.setAddress(user.getAddress());
        toUpdate.setPersonAttributes(user.getPersonAttributes());

        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(user.getUpdatedBy());

        return userRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<User> existing = userRepository.findByUuid(uuid);
        existing.ifPresent(userRepository::delete);
    }

    public Optional<User> getByUserName(String identity) {
        return userRepository.findByUsername(identity);
    }
}
