package teststatementbatch

import grails.transaction.Transactional
import org.hibernate.SessionFactory
import org.springframework.retry.annotation.Retryable
import org.springframework.transaction.annotation.Propagation

class PersonService {

    SessionFactory sessionFactory
    int count = 1

    @Retryable
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void updateName(Long personId, String newName) {
        println("pass $count")

        // Get the person
        Person person = Person.findById(personId)
        // Change name
        person.name = newName

        // During the first attempt, simulate that someone else changed the name via another transaction/session so the 'version' field is incremented and we get a StaleStateException the first time
        if (count++ == 1) {
            Person.withNewSession {
                Person.withNewTransaction {
                    Person p = Person.first()
                    p.name = "override"
                }
            }
        }

        println("commit")
    }
}
