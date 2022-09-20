package dsenta.cachito.resource;

import dsenta.cachito.exception.resource.CannotDropParentResourceException;
import dsenta.cachito.exception.resource.IdAlreadyExistsInChildResourceException;
import dsenta.cachito.exception.resource.PostForceIdException;
import dsenta.cachito.exception.resource.UniqueConstraintException;
import dsenta.cachito.handler.resource.alter.ResourceAlterHandler;
import dsenta.cachito.handler.resource.delete.ResourceDeleteHandler;
import dsenta.cachito.handler.resource.get.ResourceGetHandler;
import dsenta.cachito.handler.resource.post.ResourcePostHandler;
import dsenta.cachito.handler.resource.put.ResourcePutHandler;
import dsenta.cachito.mapper.entity.EntityToStringMapper;
import dsenta.cachito.model.clazzalter.ClazzAlter;
import dsenta.cachito.model.dimension.Dimension;
import dsenta.cachito.model.entity.Entity;
import dsenta.cachito.model.filter.AndWhere;
import dsenta.cachito.model.filter.Operator;
import dsenta.cachito.model.group.GroupPeriod;
import dsenta.cachito.model.groupby.GroupBy;
import dsenta.cachito.model.groupby.GroupByDate;
import dsenta.cachito.model.leftjoin.LeftJoinWithClazz;
import dsenta.cachito.model.pagination.Pagination;
import dsenta.cachito.model.resource.Resource;
import dsenta.cachito.repository.resource.PersistableResource;
import dsenta.cachito.resource.model.Bill;
import dsenta.cachito.resource.model.Employee;
import dsenta.cachito.resource.model.Manager;
import dsenta.cachito.resource.model.Person;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static dsenta.cachito.factory.attribute.AttributeFactory.*;
import static dsenta.cachito.handler.resource.drop.ResourceDropHandler.drop;
import static dsenta.cachito.resource.factory.ModelFactory.*;
import static dsenta.cachito.resource.util.AssertUtil.assertQuery;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ResourceTest extends RestTestBase {

    @Test
    void givenPersonIsSet_whenPost_thenPersonIsPersisted() {
        var person = createAnteMilinAsPerson();
        var resource = PersistableResource.get(getPersonClazz(), getPersistence());
        ResourcePostHandler.post(resource, person, getPersistence());

        var idList = resource.getDimensions().values().stream().map(Dimension::getAsc).map(list -> list.get(0)).collect(Collectors.toList());

        assertThat(idList)
                .isNotNull()
                .containsExactly(List.of(1L), List.of(1L));
    }

    @Test
    void givenPersonIsSet_whenPostDynamoDb_thenPersonIsPersisted() {
        var person = createAnteMilinAsPerson();
        var resource = PersistableResource.get(getPersonClazz(), getPersistence());
        ResourcePostHandler.post(resource, person, getPersistence());

        var idList = resource.getDimensions().values().stream().map(Dimension::getAsc).map(list -> list.get(0)).collect(Collectors.toList());

        assertThat(idList)
                .isNotNull()
                .containsExactly(List.of(1L), List.of(1L));
    }

    @Test
    void givenEmployeeIsSet_whenPost_thenEmployeeIsPersisted() {
        Employee employee = createAnteMilinAsEmployee();

        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Entity persistedEmployee = ResourcePostHandler.post(employeeResource, employee, getPersistence());

        assertNotNull(persistedEmployee);
        assertNotNull(persistedEmployee.getClazz());
        assertEquals(0, getEmployeeClazz().compareTo(persistedEmployee.getClazz()));
        assertEquals(Long.valueOf(1), persistedEmployee.getId());
        assertEquals("1", persistedEmployee.getAttributeValue("employeeId"));

        assertNotNull(persistedEmployee.getParentEntity());
        Entity persistedPerson = persistedEmployee.getParentEntity();
        assertEquals(Long.valueOf(1), persistedPerson.getId());
        assertEquals("Ante", persistedPerson.getAttributeValue("firstName"));
        assertEquals("Milin", persistedPerson.getAttributeValue("lastName"));
        assertEquals("123456789", persistedPerson.getAttributeValue("oib"));
    }

    @Test
    void givenEmployeeWithNoFirstNameAndLastNameIsSet_whenPost_thenEmployeWithNoName2IsPersisted() {
        Employee employee = createAnteMilinAsEmployee();
        employee.setFirstName(null);
        employee.setLastName(null);

        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Entity persistedEmployee = ResourcePostHandler.post(employeeResource, employee, getPersistence());

        assertNotNull(persistedEmployee);
        assertNotNull(persistedEmployee.getClazz());
        assertEquals(0, getEmployeeClazz().compareTo(persistedEmployee.getClazz()));
        assertEquals(Long.valueOf(1), persistedEmployee.getId());
        assertEquals("1", persistedEmployee.getAttributeValue("employeeId"));

        assertNotNull(persistedEmployee.getParentEntity());
        Entity persistedPerson = persistedEmployee.getParentEntity();
        assertEquals(Long.valueOf(1), persistedPerson.getId());
        assertEquals("NO_NAME_2", persistedPerson.getAttributeValue("firstName"));
        assertEquals("NO_NAME_2", persistedPerson.getAttributeValue("lastName"));
        assertEquals("123456789", persistedPerson.getAttributeValue("oib"));
    }

    @Test
    void givenPersonWithNoFirstNameAndLastNameIsSet_whenPost_thenEmployeeWithNoNameIsPersisted() {
        Person person = createAnteMilinAsPerson();
        person.setFirstName(null);
        person.setLastName(null);

        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());
        Entity persistedPerson = ResourcePostHandler.post(personResource, person, getPersistence());

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getClazz());
        assertEquals(0, getPersonClazz().compareTo(persistedPerson.getClazz()));
        assertEquals(Long.valueOf(1), persistedPerson.getId());
        assertEquals("NO_NAME", persistedPerson.getAttributeValue("firstName"));
        assertEquals("NO_NAME", persistedPerson.getAttributeValue("lastName"));
        assertEquals("123456789", persistedPerson.getAttributeValue("oib"));
        assertNull(persistedPerson.getParentEntity());
    }

    @Test
    void givenManagerIsSet_whenPost_thenManagerIsPersisted() {
        Manager manager = createIvoBilicAsManager();

        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Entity persistedManager = ResourcePostHandler.post(managerResource, manager, getPersistence());

        assertNotNull(persistedManager);
        assertNotNull(persistedManager.getClazz());
        assertEquals(0, getManagerClazz().compareTo(persistedManager.getClazz()));
        assertEquals(Long.valueOf(1), persistedManager.getId());
        assertEquals("1", persistedManager.getAttributeValue("managerId"));
        assertEquals("2", persistedManager.getAttributeValue("employeeId"));
        assertEquals(List.of(), persistedManager.getAttributeValue("employees"));

        assertNotNull(persistedManager.getParentEntity());
        Entity persistedPerson = persistedManager.getParentEntity();
        assertEquals(Long.valueOf(1), persistedPerson.getId());
        assertEquals("Ivo", persistedPerson.getAttributeValue("firstName"));
        assertEquals("Bilic", persistedPerson.getAttributeValue("lastName"));
        assertEquals("987654321", persistedPerson.getAttributeValue("oib"));
    }

    @Test
    void givenManagerWithEmployeeIsSet_whenPost_thenManagerIsPersisted() {
        Manager manager = createIvoBilicAsManager();
        Employee employee = createAnteMilinAsEmployee();
        manager.getEmployees().add(employee);

        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Entity persistedManager = ResourcePostHandler.post(managerResource, manager, getPersistence());

        assertNotNull(persistedManager);
        assertNotNull(persistedManager.getClazz());
        assertEquals(0, getManagerClazz().compareTo(persistedManager.getClazz()));
        assertEquals(Long.valueOf(1), persistedManager.getId());
        assertEquals("1", persistedManager.getAttributeValue("managerId"));
        assertEquals("2", persistedManager.getAttributeValue("employeeId"));

        assertNotNull(persistedManager.getParentEntity());
        Entity persistedManagerAsPerson = persistedManager.getParentEntity();
        assertEquals(Long.valueOf(1), persistedManagerAsPerson.getId());
        assertEquals("Ivo", persistedManagerAsPerson.getAttributeValue("firstName"));
        assertEquals("Bilic", persistedManagerAsPerson.getAttributeValue("lastName"));
        assertEquals("987654321", persistedManagerAsPerson.getAttributeValue("oib"));

        List<Entity> persistedEmployees = persistedManager.getRelatedEntities("employees");
        assertNotNull(persistedEmployees);
        assertEquals(1, persistedEmployees.size());
        Entity persistedEmployee = persistedEmployees.get(0);

        assertNotNull(persistedEmployee.getClazz());
        assertEquals(0, getEmployeeClazz().compareTo(persistedEmployee.getClazz()));
        assertEquals(Long.valueOf(2), persistedEmployee.getId());
        assertEquals("1", persistedEmployee.getAttributeValue("employeeId"));

        assertNotNull(persistedEmployee.getParentEntity());
        Entity persistedEmployeeAsPerson = persistedEmployee.getParentEntity();
        assertEquals(Long.valueOf(2), persistedEmployeeAsPerson.getId());
        assertEquals("Ante", persistedEmployeeAsPerson.getAttributeValue("firstName"));
        assertEquals("Milin", persistedEmployeeAsPerson.getAttributeValue("lastName"));
        assertEquals("123456789", persistedEmployeeAsPerson.getAttributeValue("oib"));
    }

    @Test
    void givenTwoManagerWithSameEmployeeIsSet_whenPost_thenBothManagerManagesSameEmployee() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());

        Manager managerIvo = createIvoBilicAsManager();
        Employee employee = createAnteMilinAsEmployee();
        managerIvo.getEmployees().add(employee);
        ResourcePostHandler.post(managerResource, managerIvo, getPersistence());

        Manager managerMatko = createMatkoZvonicAsManager();
        employee.setId(2L);
        managerMatko.getEmployees().add(employee);
        Entity persistedMatko = ResourcePostHandler.post(managerResource, managerMatko, getPersistence());
        assertNotNull(persistedMatko);
        assertNotNull(persistedMatko.getId());
        assertEquals(Long.valueOf(3), persistedMatko.getId());

        List<Entity> persistedEmployees = persistedMatko.getRelatedEntities("employees");
        assertNotNull(persistedEmployees);
        assertEquals(1, persistedEmployees.size());
        Entity persistedEmployee = persistedEmployees.get(0);

        assertNotNull(persistedEmployee.getClazz());
        assertEquals(0, getEmployeeClazz().compareTo(persistedEmployee.getClazz()));
        assertEquals(Long.valueOf(2), persistedEmployee.getId());
        assertEquals("1", persistedEmployee.getAttributeValue("employeeId"));

        assertNotNull(persistedEmployee.getParentEntity());
        Entity persistedEmployeeAsPerson = persistedEmployee.getParentEntity();
        assertEquals(Long.valueOf(2), persistedEmployeeAsPerson.getId());
        assertEquals("Ante", persistedEmployeeAsPerson.getAttributeValue("firstName"));
        assertEquals("Milin", persistedEmployeeAsPerson.getAttributeValue("lastName"));
        assertEquals("123456789", persistedEmployeeAsPerson.getAttributeValue("oib"));

        Resource peopleResource = PersistableResource.get(getPersonClazz(), getPersistence());
        assertEquals(3, ResourceGetHandler.get(peopleResource, getPersistence()).size());
    }

    @Test
    void givenEmployeeWithForcedIdIsSet_whenPost_thenExceptionIsThrown() {
        Employee employee = createAnteMilinAsEmployee();
        employee.setId(15L);
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        assertThrows(PostForceIdException.class, () -> ResourcePostHandler.post(employeeResource, employee, getPersistence()));
    }

    @Test
    void givenManagerWithForcedIdIsSet_whenPost_thenExceptionIsThrown() {
        Manager manager = createMatkoZvonicAsManager();
        manager.setId(15L);
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        assertThrows(PostForceIdException.class, () -> ResourcePostHandler.post(managerResource, manager, getPersistence()));
    }

    @Test
    void givenManagerWithIsSet_whenPostTwiceSameManager_thenConstraintExceptionIsThrown() {
        Manager manager = createMatkoZvonicAsManager();
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());
        ResourcePostHandler.post(managerResource, manager, getPersistence());
        manager.setId(null);
        assertThrows(UniqueConstraintException.class, () -> ResourcePostHandler.post(managerResource, manager, getPersistence()));
        assertEquals(1, ResourceGetHandler.get(managerResource, getPersistence()).size());
        assertEquals(1, ResourceGetHandler.get(employeeResource, getPersistence()).size());
        assertEquals(1, ResourceGetHandler.get(personResource, getPersistence()).size());
    }

    @Test
    void givenTwoManagersAreSet_whenPutFirstToSameNameAsSecond_thenConstraintExceptionIsThrown() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());

        Manager matkoZvonicAsManager = createMatkoZvonicAsManager();
        Entity matkoZvonicAsEntity = ResourcePostHandler.post(managerResource, matkoZvonicAsManager, getPersistence());
        assertNotNull(matkoZvonicAsEntity);
        assertNotNull(matkoZvonicAsEntity.getId());

        Manager ivoBilicAsManager = createIvoBilicAsManager();
        Entity ivoBilicAsEntity = ResourcePostHandler.post(managerResource, ivoBilicAsManager, getPersistence());
        assertNotNull(ivoBilicAsEntity);
        assertNotNull(ivoBilicAsEntity.getId());

        matkoZvonicAsManager.setId(ivoBilicAsEntity.getId());
        assertThrows(UniqueConstraintException.class, () -> ResourcePutHandler.put(managerResource, matkoZvonicAsManager, getPersistence()));

        assertQuery(
                List.of(
                        new AndWhere("firstName", Operator.EQUALS, "Ivo"),
                        new AndWhere("lastName", Operator.EQUALS, "Bilic")
                ),
                getManagerClazz(),
                entities -> assertEquals(1, entities.size())
        );

        assertQuery(
                List.of(
                        new AndWhere("firstName", Operator.EQUALS, "Ivo"),
                        new AndWhere("lastName", Operator.EQUALS, "Bilic")
                ),
                getEmployeeClazz(),
                entities -> assertEquals(1, entities.size())
        );

        assertQuery(
                List.of(
                        new AndWhere("firstName", Operator.EQUALS, "Ivo"),
                        new AndWhere("lastName", Operator.EQUALS, "Bilic")
                ),
                getPersonClazz(),
                entities -> assertEquals(1, entities.size())
        );

        assertQuery(
                List.of(
                        new AndWhere("firstName", Operator.EQUALS, "Ivo"),
                        new AndWhere("lastName", Operator.EQUALS, "Anic")
                ),
                getPersonClazz(),
                entities -> assertEquals(0, entities.size())
        );
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenGet_thenGetReturnsDataAsExpected() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        matkoZvonic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        assertQuery(
                new Pagination(3, 1),
                getEmployeeClazz(),
                entities -> {
                    assertEquals(3, entities.size());
                    assertEquals(0, entities.get(0).compareTo(ResourceGetHandler.getById(employeeResource, matkoZvonicEntity.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, entities.get(1).compareTo(ResourceGetHandler.getById(employeeResource, ivoBilicEntity.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, entities.get(2).compareTo(ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new Pagination(3, 2),
                getEmployeeClazz(),
                entities -> {
                    assertEquals(1, entities.size());
                    assertEquals(0, entities.get(0).compareTo(ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("employees", null, null, null, true),
                getManagerClazz(),
                group -> {
                    assertNotNull(group.getGroupPeriods());
                    List<GroupPeriod> groupPeriods = group.getGroupPeriods();
                    assertEquals(2, groupPeriods.size());

                    GroupPeriod anteMilinGroupPeriod = groupPeriods.get(0);
                    assertTrue(anteMilinGroupPeriod.getFrom() instanceof Entity);
                    assertEquals(0, ((Entity) anteMilinGroupPeriod.getFrom()).compareTo(ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    List<Entity> anteMilinRecords = anteMilinGroupPeriod.getRecords();
                    assertEquals(2, anteMilinRecords.size());
                    assertEquals(0, anteMilinRecords.get(0).compareTo(ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, anteMilinRecords.get(1).compareTo(ResourceGetHandler.getById(managerResource, matkoZvonic.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod zvonimirAnticevicGroupPeriod = groupPeriods.get(1);
                    assertTrue(zvonimirAnticevicGroupPeriod.getFrom() instanceof Entity);
                    assertEquals(0, ((Entity) zvonimirAnticevicGroupPeriod.getFrom()).compareTo(ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    List<Entity> zvonimirAnticevicRecords = zvonimirAnticevicGroupPeriod.getRecords();
                    assertEquals(1, zvonimirAnticevicRecords.size());
                    assertEquals(0, zvonimirAnticevicRecords.get(0).compareTo(ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        List<Entity> peopleEntities = ResourceGetHandler.get(personResource, getPersistence());
        assertEquals(4, peopleEntities.size());
        assertEquals(0, anteMilinEntity.getParentEntity().compareTo(peopleEntities.get(3)));
        assertEquals(0, zvonimirAnticevicEntity.getParentEntity().compareTo(peopleEntities.get(2)));
        assertEquals(0, ivoBilicEntity.getParentEntity().getParentEntity().compareTo(peopleEntities.get(1)));
        assertEquals(0, matkoZvonicEntity.getParentEntity().getParentEntity().compareTo(peopleEntities.get(0)));

        List<Entity> employeeEntities = ResourceGetHandler.get(employeeResource, getPersistence());
        assertEquals(4, employeeEntities.size());
        assertEquals(0, anteMilinEntity.compareTo(employeeEntities.get(3)));
        assertEquals(0, zvonimirAnticevicEntity.compareTo(employeeEntities.get(2)));
        assertEquals(0, ivoBilicEntity.getParentEntity().compareTo(employeeEntities.get(1)));
        assertEquals(0, matkoZvonicEntity.getParentEntity().compareTo(employeeEntities.get(0)));

        List<Entity> managerEntities = ResourceGetHandler.get(managerResource, getPersistence());
        assertEquals(2, managerEntities.size());
        assertEquals(0, ivoBilicEntity.compareTo(managerEntities.get(1)));
        assertEquals(0, matkoZvonicEntity.compareTo(managerEntities.get(0)));

        Optional<Entity> anteMilinOptional = ResourceGetHandler.getById(personResource, 1L, getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(peopleEntities.get(3)));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenGetById_thenGetReturnsDataAsExpected() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        // PERSON
        Optional<Entity> anteMilinOptional = ResourceGetHandler.getById(personResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity.getParentEntity()));

        Optional<Entity> zvonimirAnticevicOptional = ResourceGetHandler.getById(personResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity.getParentEntity()));

        Optional<Entity> ivoBilicOptional = ResourceGetHandler.getById(personResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity().getParentEntity()));

        Optional<Entity> matkoZvonicOptional = ResourceGetHandler.getById(personResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity().getParentEntity()));

        // EMPLOYEE
        anteMilinOptional = ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity));

        zvonimirAnticevicOptional = ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity));

        ivoBilicOptional = ResourceGetHandler.getById(employeeResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity()));

        matkoZvonicOptional = ResourceGetHandler.getById(employeeResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity()));

        // MANAGER
        anteMilinOptional = ResourceGetHandler.getById(managerResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        zvonimirAnticevicOptional = ResourceGetHandler.getById(managerResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isEmpty());

        ivoBilicOptional = ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity));

        matkoZvonicOptional = ResourceGetHandler.getById(managerResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenDeleteManagerById_thenGetReturnsDataAsExpected() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        ResourceDeleteHandler.delete(managerResource, matkoZvonic.getId(), getPersistence());

        // PERSON
        Optional<Entity> anteMilinOptional = ResourceGetHandler.getById(personResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity.getParentEntity()));

        Optional<Entity> zvonimirAnticevicOptional = ResourceGetHandler.getById(personResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity.getParentEntity()));

        Optional<Entity> ivoBilicOptional = ResourceGetHandler.getById(personResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity().getParentEntity()));

        Optional<Entity> matkoZvonicOptional = ResourceGetHandler.getById(personResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isEmpty());

        // EMPLOYEE
        anteMilinOptional = ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity));

        zvonimirAnticevicOptional = ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity));

        ivoBilicOptional = ResourceGetHandler.getById(employeeResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity()));

        matkoZvonicOptional = ResourceGetHandler.getById(employeeResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isEmpty());

        // MANAGER
        anteMilinOptional = ResourceGetHandler.getById(managerResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        zvonimirAnticevicOptional = ResourceGetHandler.getById(managerResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isEmpty());

        ivoBilicOptional = ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity));

        matkoZvonicOptional = ResourceGetHandler.getById(managerResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isEmpty());
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenDeleteEmployeeById_thenGetReturnsDataAsExpected() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        ResourceDeleteHandler.delete(employeeResource, anteMilin.getId(), getPersistence());

        // PERSON
        Optional<Entity> anteMilinOptional = ResourceGetHandler.getById(personResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        Optional<Entity> zvonimirAnticevicOptional = ResourceGetHandler.getById(personResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity.getParentEntity()));

        Optional<Entity> ivoBilicOptional = ResourceGetHandler.getById(personResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity().getParentEntity()));

        Optional<Entity> matkoZvonicOptional = ResourceGetHandler.getById(personResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity().getParentEntity()));

        // EMPLOYEE
        anteMilinOptional = ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        zvonimirAnticevicOptional = ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity));

        ivoBilicOptional = ResourceGetHandler.getById(employeeResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity()));

        matkoZvonicOptional = ResourceGetHandler.getById(employeeResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity()));

        // MANAGER
        anteMilinOptional = ResourceGetHandler.getById(managerResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        zvonimirAnticevicOptional = ResourceGetHandler.getById(managerResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isEmpty());

        matkoZvonicOptional = ResourceGetHandler.getById(managerResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        Entity matkoZvonicById = matkoZvonicOptional.get();
        assertEquals(List.of(), matkoZvonicById.getRelatedEntities("employees"));

        ivoBilicOptional = ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        Entity ivoBilicById = ivoBilicOptional.get();
        assertEquals(0, ivoBilicById.getRelatedEntities("employees").get(0).compareTo(zvonimirAnticevicEntity));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenDeleteEmployeeAsManagerById_thenExceptionIsThrown() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        assertThrows(IdAlreadyExistsInChildResourceException.class, () -> ResourceDeleteHandler.delete(employeeResource, ivoBilic.getId(), getPersistence()));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenDropEmployees_thenExceptionIsThrown() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        assertThrows(CannotDropParentResourceException.class, () -> drop(getEmployeeClazz(), getPersistence()));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenPutManagerAsEmployee_thenExceptionIsThrown() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        matkoZvonic.setFirstName("Marko");
        matkoZvonic.setLastName("Markić");

        assertThrows(IdAlreadyExistsInChildResourceException.class, () -> ResourcePutHandler.put(employeeResource, matkoZvonic, getPersistence()));
    }

    @Test
    void givenManagersAndEmployeesAreSet_whenPutManagerAsEmployee_thenManagerIsUpdated() {
        Resource managerResource = PersistableResource.get(getManagerClazz(), getPersistence());
        Resource employeeResource = PersistableResource.get(getEmployeeClazz(), getPersistence());
        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        Employee anteMilin = createAnteMilinAsEmployee();
        Entity anteMilinEntity = ResourcePostHandler.post(employeeResource, anteMilin, getPersistence());
        assertNotNull(anteMilinEntity);
        assertNotNull(anteMilinEntity.getId());
        anteMilin.setId(anteMilinEntity.getId());

        Employee zvonimirAnticevic = createZvonimirAnticevicAsEmployee();
        Entity zvonimirAnticevicEntity = ResourcePostHandler.post(employeeResource, zvonimirAnticevic, getPersistence());
        assertNotNull(zvonimirAnticevicEntity);
        assertNotNull(zvonimirAnticevicEntity.getId());
        zvonimirAnticevic.setId(zvonimirAnticevicEntity.getId());

        Manager ivoBilic = createIvoBilicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        ivoBilic.getEmployees().add(zvonimirAnticevic);
        Entity ivoBilicEntity = ResourcePostHandler.post(managerResource, ivoBilic, getPersistence());
        assertNotNull(ivoBilicEntity);
        assertNotNull(ivoBilicEntity.getId());
        ivoBilic.setId(ivoBilicEntity.getId());

        Manager matkoZvonic = createMatkoZvonicAsManager();
        ivoBilic.getEmployees().add(anteMilin);
        Entity matkoZvonicEntity = ResourcePostHandler.post(managerResource, matkoZvonic, getPersistence());
        assertNotNull(matkoZvonicEntity);
        assertNotNull(matkoZvonicEntity.getId());
        matkoZvonic.setId(matkoZvonicEntity.getId());

        matkoZvonic.setFirstName("Marko");
        matkoZvonic.setLastName("Markić");
        matkoZvonicEntity = ResourcePutHandler.put(managerResource, matkoZvonic, getPersistence());

        // PERSON
        Optional<Entity> anteMilinOptional = ResourceGetHandler.getById(personResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity.getParentEntity()));

        Optional<Entity> zvonimirAnticevicOptional = ResourceGetHandler.getById(personResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity.getParentEntity()));

        Optional<Entity> ivoBilicOptional = ResourceGetHandler.getById(personResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity().getParentEntity()));

        Optional<Entity> matkoZvonicOptional = ResourceGetHandler.getById(personResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity().getParentEntity()));

        // EMPLOYEE
        anteMilinOptional = ResourceGetHandler.getById(employeeResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isPresent());
        assertEquals(0, anteMilinOptional.get().compareTo(anteMilinEntity));

        zvonimirAnticevicOptional = ResourceGetHandler.getById(employeeResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isPresent());
        assertEquals(0, zvonimirAnticevicOptional.get().compareTo(zvonimirAnticevicEntity));

        ivoBilicOptional = ResourceGetHandler.getById(employeeResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity.getParentEntity()));

        matkoZvonicOptional = ResourceGetHandler.getById(employeeResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity.getParentEntity()));

        // MANAGER
        anteMilinOptional = ResourceGetHandler.getById(managerResource, anteMilin.getId(), getPersistence());
        assertTrue(anteMilinOptional.isEmpty());

        zvonimirAnticevicOptional = ResourceGetHandler.getById(managerResource, zvonimirAnticevic.getId(), getPersistence());
        assertTrue(zvonimirAnticevicOptional.isEmpty());

        ivoBilicOptional = ResourceGetHandler.getById(managerResource, ivoBilic.getId(), getPersistence());
        assertTrue(ivoBilicOptional.isPresent());
        assertEquals(0, ivoBilicOptional.get().compareTo(ivoBilicEntity));

        matkoZvonicOptional = ResourceGetHandler.getById(managerResource, matkoZvonic.getId(), getPersistence());
        assertTrue(matkoZvonicOptional.isPresent());
        assertEquals(0, matkoZvonicOptional.get().compareTo(matkoZvonicEntity));
        assertEquals(matkoZvonic.getFirstName(), matkoZvonicOptional.get().getAttributeValue("firstName"));
        assertEquals(matkoZvonic.getLastName(), matkoZvonicOptional.get().getAttributeValue("lastName"));
    }

    @Test
    void givenBillsAreSet_whenGetByQuery_thenGetIsOk() {
        Person anteMilin = createAnteMilinAsPerson();
        Person ivoBilic = createIvoBilicAsManager();

        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        anteMilin.setId(Objects.requireNonNull(ResourcePostHandler.post(personResource, anteMilin, getPersistence())).getId());
        ivoBilic.setId(Objects.requireNonNull(ResourcePostHandler.post(personResource, ivoBilic, getPersistence())).getId());

        Resource billResource = PersistableResource.get(getBillClazz(), getPersistence());

        Bill anteMilinBillMorning = createBill(
                anteMilin,
                List.of(
                        createBanana(1L),
                        createSalad(1L),
                        createBread(1L),
                        createMilk(1L)
                ),
                "2018-01-01T09:00:00Z"
        );
        anteMilinBillMorning.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillMorning, getPersistence())).getId());

        Bill anteMilinBillNoon = createBill(
                anteMilin,
                List.of(
                        createMeat(1L),
                        createMeat(2L),
                        createBread(2L),
                        createMilk(2L)
                ),
                "2018-01-01T12:00:00Z"
        );
        anteMilinBillNoon.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillNoon, getPersistence())).getId());

        Bill anteMilinBillEvening = createBill(
                anteMilin,
                List.of(
                        createPizza(1L),
                        createSalad(1L),
                        createBread(3L)
                ),
                "2018-03-01T18:00:00Z"
        );
        anteMilinBillEvening.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillEvening, getPersistence())).getId());

        Bill ivoBilicBillMorning = createBill(
                ivoBilic,
                List.of(
                        createBanana(2L)
                ),
                "2019-01-01T09:00:00Z"
        );
        ivoBilicBillMorning.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillMorning, getPersistence())).getId());

        Bill ivoBilicBillNoon = createBill(
                ivoBilic,
                List.of(
                        createBanana(3L)
                ),
                "2019-04-15T12:00:00Z"
        );
        ivoBilicBillNoon.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillNoon, getPersistence())).getId());

        Bill ivoBilicBillEvening = createBill(
                ivoBilic,
                List.of(
                        createPizza(1L),
                        createSalad(1L),
                        createBread(3L)
                ),
                "2020-02-01T18:00:00Z"
        );
        ivoBilicBillEvening.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillEvening, getPersistence())).getId());

        assertQuery(
                List.of(
                        new AndWhere("name", Operator.NOT_EQUALS, "banana")
                ),
                getArticleClazz(),
                articles -> {
                    assertEquals(13, articles.size());
                    assertTrue(articles.stream().noneMatch(article -> article.getAttributeValue("name").equals("banana")));
                    assertEquals("salad", articles.get(0).getAttributeValue("name"));
                    assertEquals("salad", articles.get(1).getAttributeValue("name"));
                    assertEquals("salad", articles.get(2).getAttributeValue("name"));
                    assertEquals("pizza", articles.get(3).getAttributeValue("name"));
                    assertEquals("pizza", articles.get(4).getAttributeValue("name"));
                    assertEquals("milk", articles.get(5).getAttributeValue("name"));
                    assertEquals("milk", articles.get(6).getAttributeValue("name"));
                    assertEquals("meat", articles.get(7).getAttributeValue("name"));
                    assertEquals("meat", articles.get(8).getAttributeValue("name"));
                    assertEquals("bread", articles.get(9).getAttributeValue("name"));
                    assertEquals("bread", articles.get(10).getAttributeValue("name"));
                    assertEquals("bread", articles.get(11).getAttributeValue("name"));
                    assertEquals("bread", articles.get(12).getAttributeValue("name"));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("name", Operator.NOT_EQUALS, "banana", true)
                ),
                getArticleClazz(),
                articles -> {
                    assertEquals(13, articles.size());
                    assertTrue(articles.stream().noneMatch(article -> article.getAttributeValue("name").equals("banana")));
                    assertEquals("bread", articles.get(0).getAttributeValue("name"));
                    assertEquals("bread", articles.get(1).getAttributeValue("name"));
                    assertEquals("bread", articles.get(2).getAttributeValue("name"));
                    assertEquals("bread", articles.get(3).getAttributeValue("name"));
                    assertEquals("meat", articles.get(4).getAttributeValue("name"));
                    assertEquals("meat", articles.get(5).getAttributeValue("name"));
                    assertEquals("milk", articles.get(6).getAttributeValue("name"));
                    assertEquals("milk", articles.get(7).getAttributeValue("name"));
                    assertEquals("pizza", articles.get(8).getAttributeValue("name"));
                    assertEquals("pizza", articles.get(9).getAttributeValue("name"));
                    assertEquals("salad", articles.get(10).getAttributeValue("name"));
                    assertEquals("salad", articles.get(11).getAttributeValue("name"));
                    assertEquals("salad", articles.get(12).getAttributeValue("name"));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("name", Operator.NOT_EQUALS, "banana")
                ),
                new LeftJoinWithClazz(
                        getBillClazz(),
                        "articles",
                        List.of(
                                new AndWhere("buyer.firstName", Operator.EQUALS, "Ivo"),
                                new AndWhere("buyer.lastName", Operator.EQUALS, "Bilic")
                        )
                ),
                getArticleClazz(),
                articles -> {
                    assertEquals(3, articles.size());
                    assertEquals("salad", articles.get(0).getAttributeValue("name"));
                    assertEquals("pizza", articles.get(1).getAttributeValue("name"));
                    assertEquals("bread", articles.get(2).getAttributeValue("name"));
                }
        );

        assertQuery(
                new GroupBy("price", null, null, null, true),
                getArticleClazz(),
                group -> {
                    assertEquals(6, group.getGroupPeriods().size());

                    GroupPeriod price399 = group.getGroupPeriods().get(0);
                    assertEquals("3.99", price399.getPeriodName());
                    List<Entity> price399Records = price399.getRecords();
                    assertEquals(2, price399Records.size());

                    GroupPeriod price599 = group.getGroupPeriods().get(1);
                    assertEquals("5.99", price599.getPeriodName());
                    List<Entity> price599Records = price599.getRecords();
                    assertEquals(3, price599Records.size());

                    GroupPeriod price899 = group.getGroupPeriods().get(2);
                    assertEquals("8.99", price899.getPeriodName());
                    List<Entity> price899Records = price899.getRecords();
                    assertEquals(4, price899Records.size());

                    GroupPeriod price999 = group.getGroupPeriods().get(3);
                    assertEquals("9.99", price999.getPeriodName());
                    List<Entity> price999Records = price999.getRecords();
                    assertEquals(3, price999Records.size());

                    GroupPeriod price2599 = group.getGroupPeriods().get(4);
                    assertEquals("25.99", price2599.getPeriodName());
                    List<Entity> price2599Records = price2599.getRecords();
                    assertEquals(2, price2599Records.size());

                    GroupPeriod price4999 = group.getGroupPeriods().get(5);
                    assertEquals("49.99", price4999.getPeriodName());
                    List<Entity> price4999Records = price4999.getRecords();
                    assertEquals(2, price4999Records.size());
                }
        );

        assertQuery(
                new GroupBy("price", "10", "0", "50", true),
                getArticleClazz(),
                group -> {
                    assertEquals(5, group.getGroupPeriods().size());

                    GroupPeriod price0010 = group.getGroupPeriods().get(0);
                    assertEquals("0.000000-10.000000", price0010.getPeriodName());
                    assertEquals(0.00, price0010.getFrom());
                    assertEquals(10.00, price0010.getTo());
                    List<Entity> price0010Records = price0010.getRecords();
                    assertEquals(12, price0010Records.size());

                    GroupPeriod price1020 = group.getGroupPeriods().get(1);
                    assertEquals(10.00, price1020.getFrom());
                    assertEquals(20.00, price1020.getTo());
                    assertEquals("10.000000-20.000000", price1020.getPeriodName());
                    List<Entity> price1020Records = price1020.getRecords();
                    assertEquals(0, price1020Records.size());

                    GroupPeriod price2030 = group.getGroupPeriods().get(2);
                    assertEquals(20.00, price2030.getFrom());
                    assertEquals(30.00, price2030.getTo());
                    assertEquals("20.000000-30.000000", price2030.getPeriodName());
                    List<Entity> price899Records = price2030.getRecords();
                    assertEquals(2, price899Records.size());

                    GroupPeriod price3040 = group.getGroupPeriods().get(3);
                    assertEquals(30.00, price3040.getFrom());
                    assertEquals(40.00, price3040.getTo());
                    assertEquals("30.000000-40.000000", price3040.getPeriodName());
                    List<Entity> price999Records = price3040.getRecords();
                    assertEquals(0, price999Records.size());

                    GroupPeriod price4050 = group.getGroupPeriods().get(4);
                    assertEquals(40.00, price4050.getFrom());
                    assertEquals(50.00, price4050.getTo());
                    assertEquals("40.000000-50.000000", price4050.getPeriodName());
                    List<Entity> price2599Records = price4050.getRecords();
                    assertEquals(2, price2599Records.size());
                }
        );

        assertQuery(
                new GroupBy("price", "10", "0", "50", true),
                List.of(
                        new AndWhere("price", Operator.BIGGER_THAN, 9.99)
                ),
                getArticleClazz(),
                group -> {
                    assertEquals(5, group.getGroupPeriods().size());

                    GroupPeriod price0010 = group.getGroupPeriods().get(0);
                    assertEquals("0.000000-10.000000", price0010.getPeriodName());
                    assertEquals(0.00, price0010.getFrom());
                    assertEquals(10.00, price0010.getTo());
                    List<Entity> price0010Records = price0010.getRecords();
                    assertEquals(0, price0010Records.size());

                    GroupPeriod price1020 = group.getGroupPeriods().get(1);
                    assertEquals(10.00, price1020.getFrom());
                    assertEquals(20.00, price1020.getTo());
                    assertEquals("10.000000-20.000000", price1020.getPeriodName());
                    List<Entity> price1020Records = price1020.getRecords();
                    assertEquals(0, price1020Records.size());

                    GroupPeriod price2030 = group.getGroupPeriods().get(2);
                    assertEquals(20.00, price2030.getFrom());
                    assertEquals(30.00, price2030.getTo());
                    assertEquals("20.000000-30.000000", price2030.getPeriodName());
                    List<Entity> price899Records = price2030.getRecords();
                    assertEquals(2, price899Records.size());

                    GroupPeriod price3040 = group.getGroupPeriods().get(3);
                    assertEquals(30.00, price3040.getFrom());
                    assertEquals(40.00, price3040.getTo());
                    assertEquals("30.000000-40.000000", price3040.getPeriodName());
                    List<Entity> price999Records = price3040.getRecords();
                    assertEquals(0, price999Records.size());

                    GroupPeriod price4050 = group.getGroupPeriods().get(4);
                    assertEquals(40.00, price4050.getFrom());
                    assertEquals(50.00, price4050.getTo());
                    assertEquals("40.000000-50.000000", price4050.getPeriodName());
                    List<Entity> price2599Records = price4050.getRecords();
                    assertEquals(2, price2599Records.size());
                }
        );

        assertQuery(
                new GroupBy("price", "10", "0", "50", true),
                List.of(
                        new AndWhere("price", Operator.BETWEEN, 9.99, 40.00)
                ),
                getArticleClazz(),
                group -> {
                    assertEquals(5, group.getGroupPeriods().size());

                    GroupPeriod price0010 = group.getGroupPeriods().get(0);
                    assertEquals("0.000000-10.000000", price0010.getPeriodName());
                    assertEquals(0.00, price0010.getFrom());
                    assertEquals(10.00, price0010.getTo());
                    List<Entity> price0010Records = price0010.getRecords();
                    assertEquals(3, price0010Records.size());

                    GroupPeriod price1020 = group.getGroupPeriods().get(1);
                    assertEquals(10.00, price1020.getFrom());
                    assertEquals(20.00, price1020.getTo());
                    assertEquals("10.000000-20.000000", price1020.getPeriodName());
                    List<Entity> price1020Records = price1020.getRecords();
                    assertEquals(0, price1020Records.size());

                    GroupPeriod price2030 = group.getGroupPeriods().get(2);
                    assertEquals(20.00, price2030.getFrom());
                    assertEquals(30.00, price2030.getTo());
                    assertEquals("20.000000-30.000000", price2030.getPeriodName());
                    List<Entity> price899Records = price2030.getRecords();
                    assertEquals(2, price899Records.size());

                    GroupPeriod price3040 = group.getGroupPeriods().get(3);
                    assertEquals(30.00, price3040.getFrom());
                    assertEquals(40.00, price3040.getTo());
                    assertEquals("30.000000-40.000000", price3040.getPeriodName());
                    List<Entity> price999Records = price3040.getRecords();
                    assertEquals(0, price999Records.size());

                    GroupPeriod price4050 = group.getGroupPeriods().get(4);
                    assertEquals(40.00, price4050.getFrom());
                    assertEquals(50.00, price4050.getTo());
                    assertEquals("40.000000-50.000000", price4050.getPeriodName());
                    List<Entity> price2599Records = price4050.getRecords();
                    assertEquals(0, price2599Records.size());
                }
        );

        assertQuery(
                new GroupBy("price", "10", "0", "50", true),
                List.of(
                        new AndWhere("name", Operator.EQUALS, "bread")
                ),
                getArticleClazz(),
                group -> {
                    assertEquals(5, group.getGroupPeriods().size());

                    GroupPeriod price0010 = group.getGroupPeriods().get(0);
                    assertEquals("0.000000-10.000000", price0010.getPeriodName());
                    assertEquals(0.00, price0010.getFrom());
                    assertEquals(10.00, price0010.getTo());
                    List<Entity> price0010Records = price0010.getRecords();
                    assertEquals(4, price0010Records.size());

                    GroupPeriod price1020 = group.getGroupPeriods().get(1);
                    assertEquals(10.00, price1020.getFrom());
                    assertEquals(20.00, price1020.getTo());
                    assertEquals("10.000000-20.000000", price1020.getPeriodName());
                    List<Entity> price1020Records = price1020.getRecords();
                    assertEquals(0, price1020Records.size());

                    GroupPeriod price2030 = group.getGroupPeriods().get(2);
                    assertEquals(20.00, price2030.getFrom());
                    assertEquals(30.00, price2030.getTo());
                    assertEquals("20.000000-30.000000", price2030.getPeriodName());
                    List<Entity> price899Records = price2030.getRecords();
                    assertEquals(0, price899Records.size());

                    GroupPeriod price3040 = group.getGroupPeriods().get(3);
                    assertEquals(30.00, price3040.getFrom());
                    assertEquals(40.00, price3040.getTo());
                    assertEquals("30.000000-40.000000", price3040.getPeriodName());
                    List<Entity> price999Records = price3040.getRecords();
                    assertEquals(0, price999Records.size());

                    GroupPeriod price4050 = group.getGroupPeriods().get(4);
                    assertEquals(40.00, price4050.getFrom());
                    assertEquals(50.00, price4050.getTo());
                    assertEquals("40.000000-50.000000", price4050.getPeriodName());
                    List<Entity> price2599Records = price4050.getRecords();
                    assertEquals(0, price2599Records.size());
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.YEAR.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(3, group.getGroupPeriods().size());

                    GroupPeriod year2018 = group.getGroupPeriods().get(0);
                    assertEquals("2018", year2018.getPeriodName());
                    List<Entity> year2018Records = year2018.getRecords();
                    assertEquals(3, year2018Records.size());
                    assertEquals(0, year2018Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2018Records.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2018Records.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod year2019 = group.getGroupPeriods().get(1);
                    assertEquals("2019", year2019.getPeriodName());
                    List<Entity> year2019Records = year2019.getRecords();
                    assertEquals(2, year2019Records.size());
                    assertEquals(0, year2019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2019Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod year2020 = group.getGroupPeriods().get(2);
                    assertEquals("2020", year2020.getPeriodName());
                    List<Entity> year2020Records = year2020.getRecords();
                    assertEquals(1, year2020Records.size());
                    assertEquals(0, year2020Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.YEAR.toString(), null, null, false),
                getBillClazz(),
                group -> {
                    assertEquals(3, group.getGroupPeriods().size());

                    GroupPeriod year2020 = group.getGroupPeriods().get(0);
                    assertEquals("2020", year2020.getPeriodName());
                    List<Entity> year2020Records = year2020.getRecords();
                    assertEquals(1, year2020Records.size());
                    assertEquals(0, year2020Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod year2019 = group.getGroupPeriods().get(1);
                    assertEquals("2019", year2019.getPeriodName());
                    List<Entity> year2019Records = year2019.getRecords();
                    assertEquals(2, year2019Records.size());
                    assertEquals(0, year2019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2019Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod year2018 = group.getGroupPeriods().get(2);
                    assertEquals("2018", year2018.getPeriodName());
                    List<Entity> year2018Records = year2018.getRecords();
                    assertEquals(3, year2018Records.size());
                    assertEquals(0, year2018Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2018Records.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2018Records.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.YEAR.toString(), "2019-01-01T00:00:00Z", "2020-01-01T00:00:00Z", true),
                getBillClazz(),
                group -> {
                    assertEquals(1, group.getGroupPeriods().size());

                    GroupPeriod year2019 = group.getGroupPeriods().get(0);
                    assertEquals("2019", year2019.getPeriodName());
                    List<Entity> year2019Records = year2019.getRecords();
                    assertEquals(2, year2019Records.size());
                    assertEquals(0, year2019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, year2019Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.WEEKYEAR.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(53, group.getGroupPeriods().size());

                    GroupPeriod week01 = group.getGroupPeriods().get(0);
                    assertEquals("01", week01.getPeriodName());
                    List<Entity> januaryRecords = week01.getRecords();
                    assertEquals(3, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week05 = group.getGroupPeriods().get(4);
                    assertEquals("05", week05.getPeriodName());
                    List<Entity> week05Records = week05.getRecords();
                    assertEquals(1, week05Records.size());
                    assertEquals(0, week05Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week09 = group.getGroupPeriods().get(8);
                    assertEquals("09", week09.getPeriodName());
                    List<Entity> week09Records = week09.getRecords();
                    assertEquals(1, week09Records.size());
                    assertEquals(0, week09Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week16 = group.getGroupPeriods().get(15);
                    assertEquals("16", week16.getPeriodName());
                    List<Entity> week16Records = week16.getRecords();
                    assertEquals(1, week16Records.size());
                    assertEquals(0, week16Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.WEEKYEAR.toString(), "2019-01-01T00:00:00Z", "2020-01-01T00:00:00Z", true),
                getBillClazz(),
                group -> {
                    assertEquals(53, group.getGroupPeriods().size());

                    GroupPeriod week01 = group.getGroupPeriods().get(0);
                    assertEquals("01", week01.getPeriodName());
                    List<Entity> januaryRecords = week01.getRecords();
                    assertEquals(1, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week16 = group.getGroupPeriods().get(15);
                    assertEquals("16", week16.getPeriodName());
                    List<Entity> week16Records = week16.getRecords();
                    assertEquals(1, week16Records.size());
                    assertEquals(0, week16Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.WEEKYEAR.toString(), null, null, false),
                getBillClazz(),
                group -> {
                    assertEquals(53, group.getGroupPeriods().size());

                    GroupPeriod week01 = group.getGroupPeriods().get(52);
                    assertEquals("01", week01.getPeriodName());
                    List<Entity> januaryRecords = week01.getRecords();
                    assertEquals(3, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week05 = group.getGroupPeriods().get(48);
                    assertEquals("05", week05.getPeriodName());
                    List<Entity> week05Records = week05.getRecords();
                    assertEquals(1, week05Records.size());
                    assertEquals(0, week05Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week09 = group.getGroupPeriods().get(44);
                    assertEquals("09", week09.getPeriodName());
                    List<Entity> week09Records = week09.getRecords();
                    assertEquals(1, week09Records.size());
                    assertEquals(0, week09Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod week16 = group.getGroupPeriods().get(37);
                    assertEquals("16", week16.getPeriodName());
                    List<Entity> week16Records = week16.getRecords();
                    assertEquals(1, week16Records.size());
                    assertEquals(0, week16Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.MONTHYEAR.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(12, group.getGroupPeriods().size());

                    GroupPeriod january = group.getGroupPeriods().get(0);
                    assertEquals(Month.JANUARY.name(), january.getPeriodName());
                    List<Entity> januaryRecords = january.getRecords();
                    assertEquals(3, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod february = group.getGroupPeriods().get(1);
                    assertEquals(Month.FEBRUARY.name(), february.getPeriodName());
                    List<Entity> februaryRecords = february.getRecords();
                    assertEquals(1, februaryRecords.size());
                    assertEquals(0, februaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod march = group.getGroupPeriods().get(2);
                    assertEquals(Month.MARCH.name(), march.getPeriodName());
                    List<Entity> marchRecords = march.getRecords();
                    assertEquals(1, marchRecords.size());
                    assertEquals(0, marchRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod april = group.getGroupPeriods().get(3);
                    assertEquals(Month.APRIL.name(), april.getPeriodName());
                    List<Entity> aprilRecords = april.getRecords();
                    assertEquals(1, aprilRecords.size());
                    assertEquals(0, aprilRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.MONTHYEAR.toString(), "2019-01-01T00:00:00Z", "2020-01-01T00:00:00Z", true),
                getBillClazz(),
                group -> {
                    assertEquals(12, group.getGroupPeriods().size());

                    GroupPeriod january = group.getGroupPeriods().get(0);
                    assertEquals(Month.JANUARY.name(), january.getPeriodName());
                    List<Entity> januaryRecords = january.getRecords();
                    assertEquals(1, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod february = group.getGroupPeriods().get(1);
                    assertEquals(Month.FEBRUARY.name(), february.getPeriodName());
                    List<Entity> februaryRecords = february.getRecords();
                    assertEquals(0, februaryRecords.size());

                    GroupPeriod march = group.getGroupPeriods().get(2);
                    assertEquals(Month.MARCH.name(), march.getPeriodName());
                    List<Entity> marchRecords = march.getRecords();
                    assertEquals(0, marchRecords.size());

                    GroupPeriod april = group.getGroupPeriods().get(3);
                    assertEquals(Month.APRIL.name(), april.getPeriodName());
                    List<Entity> aprilRecords = april.getRecords();
                    assertEquals(1, aprilRecords.size());
                    assertEquals(0, aprilRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.MONTHYEAR.toString(), null, null, false),
                getBillClazz(),
                group -> {
                    assertEquals(12, group.getGroupPeriods().size());

                    GroupPeriod january = group.getGroupPeriods().get(11);
                    assertEquals(Month.JANUARY.name(), january.getPeriodName());
                    List<Entity> januaryRecords = january.getRecords();
                    assertEquals(3, januaryRecords.size());
                    assertEquals(0, januaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, januaryRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod february = group.getGroupPeriods().get(10);
                    assertEquals(Month.FEBRUARY.name(), february.getPeriodName());
                    List<Entity> februaryRecords = february.getRecords();
                    assertEquals(1, februaryRecords.size());
                    assertEquals(0, februaryRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod march = group.getGroupPeriods().get(9);
                    assertEquals(Month.MARCH.name(), march.getPeriodName());
                    List<Entity> marchRecords = march.getRecords();
                    assertEquals(1, marchRecords.size());
                    assertEquals(0, marchRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod april = group.getGroupPeriods().get(8);
                    assertEquals(Month.APRIL.name(), april.getPeriodName());
                    List<Entity> aprilRecords = april.getRecords();
                    assertEquals(1, aprilRecords.size());
                    assertEquals(0, aprilRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.HOURDAY.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(24, group.getGroupPeriods().size());

                    GroupPeriod hours9 = group.getGroupPeriods().get(9);
                    assertEquals("09", hours9.getPeriodName());
                    List<Entity> hours09Records = hours9.getRecords();
                    assertEquals(2, hours09Records.size());
                    assertEquals(0, hours09Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, hours09Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod hours12 = group.getGroupPeriods().get(12);
                    assertEquals("12", hours12.getPeriodName());
                    List<Entity> hours12Records = hours12.getRecords();
                    assertEquals(2, hours12Records.size());
                    assertEquals(0, hours12Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, hours12Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod hours18 = group.getGroupPeriods().get(18);
                    assertEquals("18", hours18.getPeriodName());
                    List<Entity> hours18Records = hours18.getRecords();
                    assertEquals(2, hours18Records.size());
                    assertEquals(0, hours18Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, hours18Records.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DAYYEAR.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(366, group.getGroupPeriods().size());

                    GroupPeriod dayYear001 = group.getGroupPeriods().get(0);
                    assertEquals("001", dayYear001.getPeriodName());
                    List<Entity> dayYear001Records = dayYear001.getRecords();
                    assertEquals(3, dayYear001Records.size());
                    assertEquals(0, dayYear001Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, dayYear001Records.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, dayYear001Records.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod dayYear032 = group.getGroupPeriods().get(31);
                    assertEquals("032", dayYear032.getPeriodName());
                    List<Entity> dayYear032Records = dayYear032.getRecords();
                    assertEquals(1, dayYear032Records.size());
                    assertEquals(0, dayYear032Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod dayYear060 = group.getGroupPeriods().get(59);
                    assertEquals("060", dayYear060.getPeriodName());
                    List<Entity> dayYear060Records = dayYear060.getRecords();
                    assertEquals(1, dayYear060Records.size());
                    assertEquals(0, dayYear060Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod dayYear105 = group.getGroupPeriods().get(104);
                    assertEquals("105", dayYear105.getPeriodName());
                    List<Entity> dayYear105Records = dayYear105.getRecords();
                    assertEquals(1, dayYear105Records.size());
                    assertEquals(0, dayYear105Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DAYWEEK.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(7, group.getGroupPeriods().size());

                    GroupPeriod monday = group.getGroupPeriods().get(0);
                    assertEquals(DayOfWeek.MONDAY.name(), monday.getPeriodName());
                    List<Entity> mondayRecords = monday.getRecords();
                    assertEquals(3, mondayRecords.size());
                    assertEquals(0, mondayRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, mondayRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, mondayRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod tuesday = group.getGroupPeriods().get(1);
                    assertEquals(DayOfWeek.TUESDAY.name(), tuesday.getPeriodName());
                    List<Entity> tuesdayRecords = tuesday.getRecords();
                    assertEquals(1, tuesdayRecords.size());
                    assertEquals(0, tuesdayRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod thursday = group.getGroupPeriods().get(3);
                    assertEquals(DayOfWeek.THURSDAY.name(), thursday.getPeriodName());
                    List<Entity> thursdayRecords = thursday.getRecords();
                    assertEquals(1, thursdayRecords.size());
                    assertEquals(0, thursdayRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod saturday = group.getGroupPeriods().get(5);
                    assertEquals(DayOfWeek.SATURDAY.name(), saturday.getPeriodName());
                    List<Entity> saturdayRecords = saturday.getRecords();
                    assertEquals(1, saturdayRecords.size());
                    assertEquals(0, saturdayRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DAYMONTH.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(31, group.getGroupPeriods().size());

                    GroupPeriod day01 = group.getGroupPeriods().get(0);
                    assertEquals("01", day01.getPeriodName());
                    List<Entity> day01Records = day01.getRecords();
                    assertEquals(5, day01Records.size());
                    assertEquals(0, day01Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01Records.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01Records.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01Records.get(3).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01Records.get(4).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day15 = group.getGroupPeriods().get(14);
                    assertEquals("15", day15.getPeriodName());
                    List<Entity> day15Records = day15.getRecords();
                    assertEquals(1, day15Records.size());
                    assertEquals(0, day15Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DATE.toString(), null, null, true),
                getBillClazz(),
                group -> {
                    assertEquals(762, group.getGroupPeriods().size());

                    GroupPeriod day01012018 = group.getGroupPeriods().get(0);
                    assertEquals("2018-01-01", day01012018.getPeriodName());
                    List<Entity> day01012018Entities = day01012018.getRecords();
                    assertEquals(2, day01012018Entities.size());
                    assertEquals(0, day01012018Entities.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01012018Entities.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01032018 = group.getGroupPeriods().get(59);
                    assertEquals("2018-03-01", day01032018.getPeriodName());
                    List<Entity> day01032018Records = day01032018.getRecords();
                    assertEquals(1, day01032018Records.size());
                    assertEquals(0, day01032018Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01012019 = group.getGroupPeriods().get(365);
                    assertEquals("2019-01-01", day01012019.getPeriodName());
                    List<Entity> day01012019Records = day01012019.getRecords();
                    assertEquals(1, day01012019Records.size());
                    assertEquals(0, day01012019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day15042019 = group.getGroupPeriods().get(469);
                    assertEquals("2019-04-15", day15042019.getPeriodName());
                    List<Entity> day15042019Records = day15042019.getRecords();
                    assertEquals(1, day15042019Records.size());
                    assertEquals(0, day15042019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01022020 = group.getGroupPeriods().get(761);
                    assertEquals("2020-02-01", day01022020.getPeriodName());
                    List<Entity> day01022020Records = day01022020.getRecords();
                    assertEquals(1, day01022020Records.size());
                    assertEquals(0, day01022020Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DATE.toString(), "2019-01-01T00:00:00Z", "2020-01-01T00:00:00Z", true),
                getBillClazz(),
                group -> {
                    assertEquals(365, group.getGroupPeriods().size());

                    GroupPeriod day01012019 = group.getGroupPeriods().get(0);
                    assertEquals("2019-01-01", day01012019.getPeriodName());
                    List<Entity> day01012019Records = day01012019.getRecords();
                    assertEquals(1, day01012019Records.size());
                    assertEquals(0, day01012019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day15042019 = group.getGroupPeriods().get(104);
                    assertEquals("2019-04-15", day15042019.getPeriodName());
                    List<Entity> day15042019Records = day15042019.getRecords();
                    assertEquals(1, day15042019Records.size());
                    assertEquals(0, day15042019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("date", GroupByDate.DATE.toString(), null, null, false),
                getBillClazz(),
                group -> {
                    assertEquals(762, group.getGroupPeriods().size());

                    GroupPeriod day01012018 = group.getGroupPeriods().get(761);
                    assertEquals("2018-01-01", day01012018.getPeriodName());
                    List<Entity> day01012018Entities = day01012018.getRecords();
                    assertEquals(2, day01012018Entities.size());
                    assertEquals(0, day01012018Entities.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, day01012018Entities.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01032018 = group.getGroupPeriods().get(702);
                    assertEquals("2018-03-01", day01032018.getPeriodName());
                    List<Entity> day01032018Records = day01032018.getRecords();
                    assertEquals(1, day01032018Records.size());
                    assertEquals(0, day01032018Records.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01012019 = group.getGroupPeriods().get(396);
                    assertEquals("2019-01-01", day01012019.getPeriodName());
                    List<Entity> day01012019Records = day01012019.getRecords();
                    assertEquals(1, day01012019Records.size());
                    assertEquals(0, day01012019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day15042019 = group.getGroupPeriods().get(292);
                    assertEquals("2019-04-15", day15042019.getPeriodName());
                    List<Entity> day15042019Records = day15042019.getRecords();
                    assertEquals(1, day15042019Records.size());
                    assertEquals(0, day15042019Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod day01022020 = group.getGroupPeriods().get(0);
                    assertEquals("2020-02-01", day01022020.getPeriodName());
                    List<Entity> day01022020Records = day01022020.getRecords();
                    assertEquals(1, day01022020Records.size());
                    assertEquals(0, day01022020Records.get(0).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("buyer.firstName", Operator.EQUALS, "Ante"),
                        new AndWhere("articles.name", Operator.EQUALS, "salad")
                ),
                getBillClazz(),
                bills -> {
                    assertEquals(2, bills.size());
                    assertEquals(0, bills.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("buyer.firstName", Operator.EQUALS, "Ante")
                ),
                new Pagination(3, 1),
                getBillClazz(),
                bills -> {
                    assertEquals(3, bills.size());
                    assertEquals(0, bills.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("buyer.firstName", Operator.EQUALS, "Ante")
                ),
                new Pagination(2, 1),
                getBillClazz(),
                bills -> {
                    assertEquals(2, bills.size());
                    assertEquals(0, bills.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("buyer.firstName", Operator.EQUALS, "Ante")
                ),
                new Pagination(2, 2),
                getBillClazz(),
                bills -> {
                    assertEquals(1, bills.size());
                    assertEquals(0, bills.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("buyer.firstName", Operator.EQUALS, "Ante")
                ),
                new Pagination(2, 3),
                getBillClazz(),
                bills -> assertEquals(0, bills.size())
        );

        assertQuery(
                List.of(
                        new AndWhere("articles.name", Operator.EQUALS, "bread")
                ),
                getBillClazz(),
                bills -> {
                    assertEquals(4, bills.size());
                    assertEquals(0, bills.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bills.get(3).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                new GroupBy("articles.name", null, null, null, true),
                getBillClazz(),
                group -> {
                    assertNotNull(group.getGroupPeriods());
                    List<GroupPeriod> groupPeriods = group.getGroupPeriods();
                    assertEquals(6, groupPeriods.size());

                    GroupPeriod bananaPeriod = groupPeriods.get(0);
                    List<Entity> bananaPeriodRecords = bananaPeriod.getRecords();
                    assertEquals(3, bananaPeriodRecords.size());
                    assertEquals(0, bananaPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bananaPeriodRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, bananaPeriodRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod breadPeriod = groupPeriods.get(1);
                    List<Entity> breadPeriodRecords = breadPeriod.getRecords();
                    assertEquals(4, breadPeriodRecords.size());
                    assertEquals(0, breadPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, breadPeriodRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, breadPeriodRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, breadPeriodRecords.get(3).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod meatPeriod = groupPeriods.get(2);
                    List<Entity> meatPeriodRecords = meatPeriod.getRecords();
                    assertEquals(1, meatPeriodRecords.size());
                    assertEquals(0, meatPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod milkPeriod = groupPeriods.get(3);
                    List<Entity> milkPeriodRecords = milkPeriod.getRecords();
                    assertEquals(2, milkPeriodRecords.size());
                    assertEquals(0, milkPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, milkPeriodRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillNoon.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod pizzaPeriod = groupPeriods.get(4);
                    List<Entity> pizzaPeriodRecords = pizzaPeriod.getRecords();
                    assertEquals(2, pizzaPeriodRecords.size());
                    assertEquals(0, pizzaPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, pizzaPeriodRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));

                    GroupPeriod saladPeriod = groupPeriods.get(5);
                    List<Entity> saladPeriodRecords = saladPeriod.getRecords();
                    assertEquals(3, saladPeriodRecords.size());
                    assertEquals(0, saladPeriodRecords.get(0).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillMorning.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, saladPeriodRecords.get(1).compareTo(ResourceGetHandler.getById(billResource, anteMilinBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                    assertEquals(0, saladPeriodRecords.get(2).compareTo(ResourceGetHandler.getById(billResource, ivoBilicBillEvening.getId(), getPersistence()).orElseThrow(() -> new RuntimeException("Entity does not exist!"))));
                }
        );

        assertQuery(
                List.of(
                        new AndWhere("name", Operator.EQUALS, "bread", true)
                ),
                getArticleClazz(),
                articles -> {
                    assertEquals(4, articles.size());
                    assertEquals(1L, articles.get(0).getAttributeValue("lot"));
                    assertEquals(2L, articles.get(1).getAttributeValue("lot"));
                    assertEquals(3L, articles.get(2).getAttributeValue("lot"));
                    assertEquals(3L, articles.get(3).getAttributeValue("lot"));
                }
        );

        assertQuery(
                new GroupBy("name", null, null, null, true),
                getArticleClazz(),
                group -> {
                    assertNotNull(group.getGroupPeriods());
                    List<GroupPeriod> groupPeriods = group.getGroupPeriods();
                    assertEquals(6, groupPeriods.size());

                    GroupPeriod bananaPeriod = groupPeriods.get(0);
                    List<Entity> bananaPeriodRecords = bananaPeriod.getRecords();
                    assertEquals(3, bananaPeriodRecords.size());

                    GroupPeriod breadPeriod = groupPeriods.get(1);
                    List<Entity> breadPeriodRecords = breadPeriod.getRecords();
                    assertEquals(4, breadPeriodRecords.size());

                    GroupPeriod meatPeriod = groupPeriods.get(2);
                    List<Entity> meatPeriodRecords = meatPeriod.getRecords();
                    assertEquals(2, meatPeriodRecords.size());

                    GroupPeriod milkPeriod = groupPeriods.get(3);
                    List<Entity> milkPeriodRecords = milkPeriod.getRecords();
                    assertEquals(2, milkPeriodRecords.size());

                    GroupPeriod pizzaPeriod = groupPeriods.get(4);
                    List<Entity> pizzaPeriodRecords = pizzaPeriod.getRecords();
                    assertEquals(2, pizzaPeriodRecords.size());

                    GroupPeriod saladPeriod = groupPeriods.get(5);
                    List<Entity> saladPeriodRecords = saladPeriod.getRecords();
                    assertEquals(3, saladPeriodRecords.size());
                }
        );

        // TODO Test negative test cases
        // TODO Add unique constraint for relationship
        // TODO Add disallowed routes
        // TODO Add Webhooks internal and external
        // TODO add delete where and delete all with force or cascaded
        // TODO Add 'public' into resourceinfo ... make it available on API

    }

    @Test
    void givenBillsAreSet_whenAlterArticles_thenGetIsOk() {
        Person anteMilin = createAnteMilinAsPerson();
        Person ivoBilic = createIvoBilicAsManager();

        Resource personResource = PersistableResource.get(getPersonClazz(), getPersistence());

        anteMilin.setId(Objects.requireNonNull(ResourcePostHandler.post(personResource, anteMilin, getPersistence())).getId());
        ivoBilic.setId(Objects.requireNonNull(ResourcePostHandler.post(personResource, ivoBilic, getPersistence())).getId());

        Resource billResource = PersistableResource.get(getBillClazz(), getPersistence());

        Bill anteMilinBillMorning = createBill(
                anteMilin,
                List.of(
                        createBanana(1L),
                        createSalad(1L),
                        createBread(1L),
                        createMilk(1L)
                ),
                "2018-01-01T09:00:00Z"
        );
        anteMilinBillMorning.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillMorning, getPersistence())).getId());

        Bill anteMilinBillNoon = createBill(
                anteMilin,
                List.of(
                        createMeat(1L),
                        createMeat(2L),
                        createBread(2L),
                        createMilk(2L)
                ),
                "2018-01-01T12:00:00Z"
        );
        anteMilinBillNoon.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillNoon, getPersistence())).getId());

        Bill anteMilinBillEvening = createBill(
                anteMilin,
                List.of(
                        createPizza(1L),
                        createSalad(1L),
                        createBread(3L)
                ),
                "2018-01-01T18:00:00Z"
        );
        anteMilinBillEvening.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, anteMilinBillEvening, getPersistence())).getId());

        Bill ivoBilicBillMorning = createBill(
                ivoBilic,
                List.of(
                        createBanana(2L)
                ),
                "2018-01-01T09:00:00Z"
        );
        ivoBilicBillMorning.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillMorning, getPersistence())).getId());

        Bill ivoBilicBillNoon = createBill(
                ivoBilic,
                List.of(
                        createBanana(3L)
                ),
                "2018-01-01T12:00:00Z"
        );
        ivoBilicBillNoon.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillNoon, getPersistence())).getId());

        Bill ivoBilicBillEvening = createBill(
                ivoBilic,
                List.of(
                        createPizza(1L),
                        createSalad(1L),
                        createBread(3L)
                ),
                "2018-01-01T18:00:00Z"
        );
        ivoBilicBillEvening.setId(Objects.requireNonNull(ResourcePostHandler.post(billResource, ivoBilicBillEvening, getPersistence())).getId());

        // ACTION
        ResourceAlterHandler.alter(
                getArticleClazz(),
                new ClazzAlter(
                        List.of(
                                asRelationshipZeroOne("owner", -1, getPersonClazz())
                        ),
                        Map.of(
                                "lot", asFloat("lot", -1, 0.0),
                                "price", asString("priceStr", -1, "")
                        ),
                        List.of("bill")
                ),
                getPersistence()
        );

        List<Entity> articles = ResourceGetHandler.get(PersistableResource.get(getArticleClazz(), getPersistence()), getPersistence());

        assertEquals(16, articles.size());
        assertEquals(
                "[{\"id\":16,\"lot\":3.0,\"owner\":null,\"priceStr\":\"8.99\",\"name\":\"bread\"},{\"id\":15,\"lot\":1.0,\"owner\":null,\"priceStr\":\"9.99\",\"name\":\"salad\"},{\"id\":14,\"lot\":1.0,\"owner\":null,\"priceStr\":\"49.99\",\"name\":\"pizza\"},{\"id\":13,\"lot\":3.0,\"owner\":null,\"priceStr\":\"5.99\",\"name\":\"banana\"},{\"id\":12,\"lot\":2.0,\"owner\":null,\"priceStr\":\"5.99\",\"name\":\"banana\"},{\"id\":11,\"lot\":3.0,\"owner\":null,\"priceStr\":\"8.99\",\"name\":\"bread\"},{\"id\":10,\"lot\":1.0,\"owner\":null,\"priceStr\":\"9.99\",\"name\":\"salad\"},{\"id\":9,\"lot\":1.0,\"owner\":null,\"priceStr\":\"49.99\",\"name\":\"pizza\"},{\"id\":8,\"lot\":2.0,\"owner\":null,\"priceStr\":\"3.99\",\"name\":\"milk\"},{\"id\":7,\"lot\":2.0,\"owner\":null,\"priceStr\":\"8.99\",\"name\":\"bread\"},{\"id\":6,\"lot\":2.0,\"owner\":null,\"priceStr\":\"25.99\",\"name\":\"meat\"},{\"id\":5,\"lot\":1.0,\"owner\":null,\"priceStr\":\"25.99\",\"name\":\"meat\"},{\"id\":4,\"lot\":1.0,\"owner\":null,\"priceStr\":\"3.99\",\"name\":\"milk\"},{\"id\":3,\"lot\":1.0,\"owner\":null,\"priceStr\":\"8.99\",\"name\":\"bread\"},{\"id\":2,\"lot\":1.0,\"owner\":null,\"priceStr\":\"9.99\",\"name\":\"salad\"},{\"id\":1,\"lot\":1.0,\"owner\":null,\"priceStr\":\"5.99\",\"name\":\"banana\"}]",
                EntityToStringMapper.toString(articles)
        );
    }
}