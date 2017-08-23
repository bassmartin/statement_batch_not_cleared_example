package teststatementbatch

class PersonController {

    PersonService personService

    def updateName() {
        Long personId = params.personId as Long
        String newName = params.newName
        personService.updateName(personId, newName)
    }
}
