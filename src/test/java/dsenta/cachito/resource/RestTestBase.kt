package dsenta.cachito.resource

import dsenta.cachito.action.resource.ResourceAlter
import dsenta.cachito.assertions.clazz.ClazzAssert
import dsenta.cachito.factory.attribute.AttributeFactory
import dsenta.cachito.handler.resource.drop.ResourceDropHandler
import dsenta.cachito.model.clazz.Clazz
import dsenta.cachito.model.clazzalter.ClazzAlter
import dsenta.cachito.model.persistence.Persistence
import dsenta.cachito.repository.resource.PersistableResource
import dsenta.cachito.resource.factory.ClazzUtilFactory
import dsenta.cachito.resource.testmodel.PersistenceImplTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.List
import java.util.Map

open class RestTestBase {

    val persistence: Persistence = PersistenceImplTest()
    var articleClazz: Clazz? = null
    var personClazz: Clazz? = null
    var managerClazz: Clazz? = null
    var employeeClazz: Clazz? = null
    var billClazz: Clazz? = null


    @BeforeEach
    open fun setUp() {
        articleClazz = ClazzUtilFactory.createArticle()
        PersistableResource.create(articleClazz, { inputClazz: Clazz?, persistence: Persistence? -> ClazzAssert.canCreate(inputClazz, persistence) }, persistence)
        personClazz = ClazzUtilFactory.createPerson()
        PersistableResource.create(personClazz, { inputClazz: Clazz?, persistence: Persistence? -> ClazzAssert.canCreate(inputClazz, persistence) }, persistence)
        employeeClazz = ClazzUtilFactory.createEmployee(personClazz)
        PersistableResource.create(employeeClazz, { inputClazz: Clazz?, persistence: Persistence? -> ClazzAssert.canCreate(inputClazz, persistence) }, persistence)
        managerClazz = ClazzUtilFactory.createManager(employeeClazz)
        PersistableResource.create(managerClazz, { inputClazz: Clazz?, persistence: Persistence? -> ClazzAssert.canCreate(inputClazz, persistence) }, persistence)
        billClazz = ClazzUtilFactory.createBill()
        PersistableResource.create(billClazz, { inputClazz: Clazz?, persistence: Persistence? -> ClazzAssert.canCreate(inputClazz, persistence) }, persistence)
        ResourceAlter.alter(
                articleClazz,
                ClazzAlter(
                        List.of(AttributeFactory.asRelationshipOne("bill", -1, billClazz)),
                        Map.of(),
                        List.of()
                ),
                persistence
        )
    }

    @AfterEach
    open fun tearDown() {
        ResourceDropHandler.drop(billClazz, persistence)
        billClazz = null
        ResourceDropHandler.drop(articleClazz, persistence)
        articleClazz = null
        ResourceDropHandler.drop(managerClazz, persistence)
        managerClazz = null
        ResourceDropHandler.drop(employeeClazz, persistence)
        employeeClazz = null
        ResourceDropHandler.drop(personClazz, persistence)
        personClazz = null
    }
}