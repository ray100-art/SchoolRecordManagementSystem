package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.dao.DatabaseManager;
import com.example.schoolmanagementsystem.dao.StudentDAO;
import com.example.schoolmanagementsystem.model.Student;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentDAO
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDAOTest {

    private static StudentDAO studentDAO;

    @BeforeAll
    static void setUp() {
        DatabaseManager.getInstance().initializeDatabase();
        studentDAO = new StudentDAO();
    }

    @Test
    @Order(1)
    void testAddStudent() {
        Student s = new Student("TEST001", "Test", "User", "test@test.com",
                "0700000000", "Male", LocalDate.of(2005, 1, 1),
                "Nairobi", "Form 3", "Guardian Test", "0711111111",
                LocalDate.now(), "Active");
        boolean result = studentDAO.addStudent(s);
        assertTrue(result, "Student should be added successfully");
    }

    @Test
    @Order(2)
    void testGetAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }

    @Test
    @Order(3)
    void testSearchStudents() {
        List<Student> results = studentDAO.searchStudents("Test");
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    @Order(4)
    void testGenerateStudentId() {
        String id = studentDAO.generateNextStudentId();
        assertNotNull(id);
        assertTrue(id.startsWith("STD"));
    }
}
