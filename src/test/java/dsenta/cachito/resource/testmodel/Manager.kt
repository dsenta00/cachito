package dsenta.cachito.resource.testmodel

import dsenta.cachito.model.attribute.Attribute
import dsenta.cachito.model.attribute.DataType.RELATIONSHIP_ZERO_MANY
import dsenta.cachito.model.clazz.Clazz
import dsenta.cachito.model.resource.info.ResourceInfo

@Suppress("EqualsOrHashCode")
class Manager : Employee() {
    var employees: Set<Any>? = null

    override fun equals(other: Any?): Boolean {
        val person = other as Manager
        return id == person.id && firstName == person.firstName && lastName == person.lastName &&
                employeeID == person.employeeID && salary == person.salary
    }

    companion object : Model {
        override fun clazz(): Clazz = Clazz().let {
            it.apply {
                resourceInfo = ResourceInfo().apply {
                    name = "Manager"
                    fileName = "Manager.cache"
                    key = "123"
                    clazz = it
                }
                parentClazz = Employee.clazz()
                attributes = mapOf(
                        "employees" to Attribute().apply {
                            name = "employees"
                            dataType = RELATIONSHIP_ZERO_MANY
                            clazz = Employee.clazz()
                            propertyIndex = 0
                        }
                )
            }
        }
    }
}