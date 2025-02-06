

package com.apigenerator.core;

import com.apigenerator.models.EntityModel;
import com.apigenerator.models.FieldModel;
import com.apigenerator.models.RelationshipType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityAnalyzerTest {

    private final EntityAnalyzer analyzer = new EntityAnalyzer();

    @Test
    void analyzeEntity_simpleEntity() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "@Table(name = \"users\")\n" +
                "public class User {\n" +
                "    @Id\n" +
                "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                "    private Long id;\n" +
                "    @Column(name = \"username\", nullable = false)\n" +
                "    private String username;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);

        assertNotNull(entityModel);
        assertEquals("com.example", entityModel.getPackageName());
        assertEquals("User", entityModel.getClassName());
        assertEquals("users", entityModel.getTableName());
        assertFalse(entityModel.isHasLombok());
        assertFalse(entityModel.isHasAuditing());

        List<FieldModel> fields = entityModel.getFields();
        assertEquals(2, fields.size());

        FieldModel idField = fields.get(0);
        assertEquals("id", idField.getName());
        assertEquals("Long", idField.getType());
        assertTrue(idField.isPrimary());
        assertNull(idField.getColumnName());

        FieldModel usernameField = fields.get(1);
        assertEquals("username", usernameField.getName());
        assertEquals("String", usernameField.getType());
        assertFalse(usernameField.isPrimary());
        assertEquals("username", usernameField.getColumnName());
        assertFalse(usernameField.isNullable());
    }

    @Test
    void analyzeEntity_entityWithLombokAndAuditing() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "import lombok.Data;\n" +
                "import org.springframework.data.jpa.domain.support.AuditingEntityListener;\n" +
                "\n" +
                "@Entity\n" +
                "@Table(name = \"products\")\n" +
                "@Data\n" +
                "@EntityListeners(AuditingEntityListener.class)\n" +
                "public class Product {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @Column(name = \"name\")\n" +
                "    private String name;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);

        assertTrue(entityModel.isHasLombok());
        assertTrue(entityModel.isHasAuditing());
    }
    @Test
    void analyzeEntity_entityWithRelationships() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class Order {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @ManyToOne(targetEntity = Customer.class)\n" +
                "    @JoinColumn(name = \"customer_id\")\n" +
                "    private Customer customer;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        assertEquals(2, fields.size());

        FieldModel customerField = fields.get(1);
        assertEquals("customer", customerField.getName());
        assertEquals("Customer", customerField.getType());
        assertEquals(RelationshipType.MANY_TO_ONE, customerField.getRelationshipType());
        assertEquals("Customer", customerField.getTargetEntity());
    }


    @Test
    void analyzeEntity_entityWithoutTableAnnotation() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class Category {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    private String name;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);

        assertNull(entityModel.getTableName());
    }

    @Test
    void analyzeEntity_fieldWithLength() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class Item {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @Column(name = \"description\", length = 255)\n" +
                "    private String description;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel descriptionField = fields.get(1);
        assertEquals("255", descriptionField.getLength());
    }

    @Test
    void analyzeEntity_relationshipWithTargetEntity() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class CartItem {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @ManyToOne(targetEntity = Product.class)\n" +
                "    private Product product;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel productField = fields.get(1);
        assertEquals("Product", productField.getTargetEntity());
    }

    @Test
    void analyzeEntity_fieldWithNoColumnName() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class Tag {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @Column\n" +
                "    private String name;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel nameField = fields.get(1);
        assertNull(nameField.getColumnName());
    }

    @Test
    void analyzeEntity_oneToOneRelationship() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "\n" +
                "@Entity\n" +
                "public class Person {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @OneToOne\n" +
                "    @JoinColumn(name = \"passport_id\")\n" +
                "    private Passport passport;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel passportField = fields.get(1);
        assertEquals(RelationshipType.ONE_TO_ONE, passportField.getRelationshipType());
    }

    @Test
    void analyzeEntity_oneToManyRelationship() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Entity\n" +
                "public class Post {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @OneToMany(mappedBy = \"post\")\n" +
                "    private List<Comment> comments;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel commentsField = fields.get(1);
        assertEquals(RelationshipType.ONE_TO_MANY, commentsField.getRelationshipType());
    }

    @Test
    void analyzeEntity_manyToManyRelationship() {
        String sourceCode = "package com.example;\n" +
                "import javax.persistence.*;\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Entity\n" +
                "public class Student {\n" +
                "    @Id\n" +
                "    private Long id;\n" +
                "    @ManyToMany\n" +
                "    @JoinTable(name = \"student_course\",\n" +
                "            joinColumns = @JoinColumn(name = \"student_id\"),\n" +
                "            inverseJoinColumns = @JoinColumn(name = \"course_id\"))\n" +
                "    private List<Course> courses;\n" +
                "}";

        EntityModel entityModel = analyzer.analyzeEntity(sourceCode);
        List<FieldModel> fields = entityModel.getFields();

        FieldModel coursesField = fields.get(1);
        assertEquals(RelationshipType.MANY_TO_MANY, coursesField.getRelationshipType());
    }
}