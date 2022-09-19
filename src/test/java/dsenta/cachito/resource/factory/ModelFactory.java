package dsenta.cachito.resource.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dsenta.cachito.mapper.objectinstance.DateMapper;
import dsenta.cachito.resource.model.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModelFactory {

    public static Article createPizza(Long lot) {
        Article article = new Article();
        article.setName("pizza");
        article.setLot(lot);
        article.setPrice(49.99);
        return article;
    }

    public static Article createSalad(Long lot) {
        Article article = new Article();
        article.setName("salad");
        article.setLot(lot);
        article.setPrice(9.99);
        return article;
    }

    public static Article createMeat(Long lot) {
        Article article = new Article();
        article.setName("meat");
        article.setLot(lot);
        article.setPrice(25.99);
        return article;
    }

    public static Article createBanana(Long lot) {
        Article article = new Article();
        article.setName("banana");
        article.setLot(lot);
        article.setPrice(5.99);
        return article;
    }

    public static Article createBread(Long lot) {
        Article article = new Article();
        article.setName("bread");
        article.setLot(lot);
        article.setPrice(8.99);
        return article;
    }

    public static Article createMilk(Long lot) {
        Article article = new Article();
        article.setName("milk");
        article.setLot(lot);
        article.setPrice(3.99);
        return article;
    }

    public static Bill createBill(Person person, List<Article> articles, String date) {
        Bill bill = new Bill();
        bill.setArticles(articles);
        bill.setBuyer(person);
        bill.setDate(DateMapper.toDate(date));
        articles.forEach(article -> article.setBill(bill));
        return bill;
    }

    public static Manager createIvoBilicAsManager() {
        Manager manager = new Manager();
        manager.setManagerId("1");
        manager.setEmployeeId("2");
        manager.setFirstName("Ivo");
        manager.setLastName("Bilic");
        manager.setOib("987654321");
        manager.setCreatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));
        manager.setUpdatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));

        return manager;
    }

    public static Manager createMatkoZvonicAsManager() {
        Manager manager = new Manager();
        manager.setManagerId("2");
        manager.setEmployeeId("3");
        manager.setFirstName("Matko");
        manager.setLastName("Zvonić");
        manager.setOib("198765432");
        manager.setCreatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));
        manager.setUpdatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));

        return manager;
    }

    public static Employee createAnteMilinAsEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId("1");
        employee.setFirstName("Ante");
        employee.setLastName("Milin");
        employee.setOib("123456789");
        employee.setCreatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));
        employee.setUpdatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));

        return employee;
    }

    public static Employee createZvonimirAnticevicAsEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId("4");
        employee.setFirstName("Zvonimir");
        employee.setLastName("Anticevic");
        employee.setOib("345678912");
        employee.setCreatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));
        employee.setUpdatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));

        return employee;
    }

    public static Person createAnteMilinAsPerson() {
        Person person = new Person();
        person.setFirstName("Ante");
        person.setLastName("Milin");
        person.setOib("123456789");
        person.setCreatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));
        person.setUpdatedAt(Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()));

        return person;
    }
}