package dsenta.cachito.resource.testmodel

import dsenta.cachito.model.attribute.Attribute
import dsenta.cachito.model.attribute.DataType.DATE
import dsenta.cachito.model.attribute.DataType.STRING
import dsenta.cachito.model.clazz.Clazz
import dsenta.cachito.model.resource.info.ResourceInfo
import java.util.*

open class Person {
    var id: Long? = null
    var firstName: String? = null
    var lastName: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null

    override fun equals(other: Any?): Boolean {
        val person = other as Person
        return id == person.id && firstName == person.firstName && lastName == person.lastName
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (lastName?.hashCode() ?: 0)
        return result
    }

    companion object : Model {
        override fun clazz(): Clazz = Clazz().let {
            it.apply {
                resourceInfo = ResourceInfo().apply {
                    name = "Person"
                    fileName = "Person.cache"
                    key = "123"
                    clazz = it
                }
                attributes = mapOf(
                        "firstName" to Attribute().apply {
                            name = "firstName"
                            dataType = STRING
                            defaultValue = "INVALID_FIRST_NAME"
                            isFilterable = true
                            propertyIndex = 0
                        },
                        "lastName" to Attribute().apply {
                            name = "lastName"
                            dataType = STRING
                            defaultValue = "INVALID_LAST_NAME"
                            isFilterable = true
                            propertyIndex = 1
                        },
                        "createdAt" to Attribute().apply {
                            name = "createdAt"
                            dataType = DATE
                            defaultValue = "now"
                            propertyIndex = 2
                        },
                        "updatedAt" to Attribute().apply {
                            name = "updatedAt"
                            dataType = DATE
                            defaultValue = "now"
                            propertyIndex = 3
                        }
                )
            }
        }
    }
}