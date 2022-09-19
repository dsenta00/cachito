package dsenta.cachito.resource.testmodel

import dsenta.cachito.model.attribute.Attribute
import dsenta.cachito.model.attribute.DataType.*
import dsenta.cachito.model.clazz.Clazz
import dsenta.cachito.model.resource.info.ResourceInfo
import java.util.*

open class Employee : Person() {
    var employeeID: Int? = null
    var startDate: Date? = null
    var salary: Float? = null

    override fun equals(other: Any?): Boolean {
        val person = other as Employee
        return id == person.id && firstName == person.firstName && lastName == person.lastName &&
                employeeID == person.employeeID && salary == person.salary
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (employeeID ?: 0)
        result = 31 * result + (salary?.hashCode() ?: 0)
        return result
    }

    companion object : Model {
        override fun clazz(): Clazz = Clazz().let {
            it.apply {
                resourceInfo = ResourceInfo().apply {
                    name = "Employee"
                    fileName = "Employee.cache"
                    key = "123"
                    clazz = it
                }
                parentClazz = Person.clazz()
                attributes = mapOf(
                        "employeeID" to Attribute().apply {
                            name = "employeeID"
                            dataType = INTEGER
                            isUnique = true
                            propertyIndex = 0
                        },
                        "startDate" to Attribute().apply {
                            name = "startDate"
                            dataType = DATE
                            defaultValue = "now"
                            propertyIndex = 1
                        },
                        "salary" to Attribute().apply {
                            name = "salary"
                            dataType = FLOAT
                            defaultValue = 0.0
                            propertyIndex = 2
                        }
                )
            }
        }
    }
}