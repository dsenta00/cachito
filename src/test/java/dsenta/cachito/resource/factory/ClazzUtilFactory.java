package dsenta.cachito.resource.factory;

import dsenta.cachito.factory.resourceinfo.ResourceInfoFactory;
import dsenta.cachito.model.clazz.Clazz;
import dsenta.cachito.model.schema.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import static dsenta.cachito.factory.attribute.AttributeFactory.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClazzUtilFactory {

    public static Clazz createManager(Clazz employee) {
        var clazz = new Clazz();

        clazz.setAttributeList(List.of(
                asString("managerId", 0, "NO_NAME"),
                asRelationshipZeroMany("employees", 1, employee)
        ));
        clazz.setSchema(new Schema("schema"));

        var resourceInfo = ResourceInfoFactory.create("manager", clazz);
        clazz.setResourceInfo(resourceInfo);
        clazz.setParentClazz(employee);

        return clazz;
    }

    public static Clazz createArticle() {
        var clazz = new Clazz();

        clazz.setAttributeList(List.of(
                asInt("lot", 0, 0L),
                asFilterableString("name", 1, "INVALID_NAME"),
                asFilterableFloat("price", 2, 0.0)
        ));
        clazz.setSchema(new Schema("schema"));

        var resourceInfo = ResourceInfoFactory.create("article", clazz);
        clazz.setResourceInfo(resourceInfo);

        return clazz;
    }

    public static Clazz createBill() {
        var clazz = new Clazz();

        clazz.setAttributeList(List.of(
                asRelationshipOne("buyer", 0, createPerson()),
                asRelationshipOneMany("articles", 1, createArticle()),
                asFilterableDate("date", 2, new Date())
        ));
        clazz.setSchema(new Schema("schema"));

        var resourceInfo = ResourceInfoFactory.create( "bill", clazz);
        clazz.setResourceInfo(resourceInfo);

        return clazz;
    }

    public static Clazz createEmployee(Clazz person) {
        var clazz = new Clazz();

        clazz.setAttributeList(List.of(
                asFilterableUniqueString("firstName", 0, "NO_NAME_2"),
                asFilterableUniqueString("lastName", 1, "NO_NAME_2"),
                asString("employeeId", 2, "0000")
        ));
        clazz.setSchema(new Schema("schema"));

        var resourceInfo = ResourceInfoFactory.create( "employee", clazz);
        clazz.setResourceInfo(resourceInfo);
        clazz.setParentClazz(person);

        return clazz;
    }

    public static Clazz createPerson() {
        var clazz = new Clazz();

        clazz.setAttributeList(List.of(
                asFilterableString("firstName", 0, "NO_NAME"),
                asFilterableString("lastName", 1, "NO_NAME"),
                asString("oib", 2, "INVALID_OIB"),
                asDate("createdAt", 3, new Date()),
                asDate("updatedAt", 4, new Date())
        ));
        clazz.setSchema(new Schema("schema"));

        var resourceInfo = ResourceInfoFactory.create( "person", clazz);
        clazz.setResourceInfo(resourceInfo);

        return clazz;
    }
}