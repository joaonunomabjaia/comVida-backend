package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import mz.org.csaude.comvida.backend.base.BaseService;
import mz.org.csaude.comvida.backend.entity.Person;
import mz.org.csaude.comvida.backend.repository.PersonRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;

import java.util.List;
import java.util.Optional;

@Singleton
public class PersonService extends BaseService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findByUuid(String uuid) {
        return personRepository.findByUuid(uuid);
    }

    @Transactional
    public Person create(Person person) {
        person.setCreatedAt(DateUtils.getCurrentDate());
        person.setLifeCycleStatus(mz.org.fgh.mentoring.util.LifeCycleStatus.valueOf("ACTIVE"));
        return personRepository.save(person);
    }

    @Transactional
    public Person update(Person person) {
        Optional<Person> existing = personRepository.findByUuid(person.getUuid());
        if (existing.isEmpty()) {
            throw new RuntimeException("Person not found");
        }

        Person toUpdate = existing.get();
        toUpdate.setNames(person.getNames());
        toUpdate.setSex(person.getSex());
        toUpdate.setBirthdate(person.getBirthdate());
        toUpdate.setAddress(person.getAddress());
        toUpdate.setPersonAttributes(person.getPersonAttributes());
        toUpdate.setUpdatedAt(DateUtils.getCurrentDate());
        toUpdate.setUpdatedBy(person.getUpdatedBy());

        return personRepository.update(toUpdate);
    }

    @Transactional
    public void delete(String uuid) {
        Optional<Person> existing = personRepository.findByUuid(uuid);
        existing.ifPresent(personRepository::delete);
    }
}
