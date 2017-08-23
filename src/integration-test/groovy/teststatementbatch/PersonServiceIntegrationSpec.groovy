package teststatementbatch

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

@Integration
@Rollback
class PersonServiceIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    Integer serverPort

    def "test statement batch is not cleared on transaction rollback"() {
        given: "a person created in a new transaction so it's in the DB"
        Person.withNewTransaction {
            new Person(name: "1").save(flush: true)
        }

        when: "changing the name via the controller"
        RESTClient restClient = new RESTClient("http://localhost:$serverPort/person/updateName")
        restClient.get(query: [personId: 1, newName: 2])

        then: "exception is thrown"
        thrown()

        // First attempt will log HHH000346: Error during managed flush [Batch update returned unexpected row count from update [0]; actual row count: 0; expected: 1]      => StaleStateException    this is normal
        // Second and third attempt will log HHH000346: Error during managed flush [null]      => Caused by the fact there is a statement batch already opened in the session and it already have the failing update. When those attempts want to add their update statement, there seems to be a conflict detected and a NPE is thrown. Shouldn't the statement batch be cleared like the Hibernate session on rollback?
    }

}
