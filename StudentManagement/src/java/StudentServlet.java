package com.sms.backend;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "StudentServlet", urlPatterns = {"/api/students"})
public class StudentServlet extends HttpServlet {

    private Gson gson;

    @Override
    public void init() {
        gson = new Gson();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/university_db";
        String jdbcUsername = "root";
        String jdbcPassword = "";
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String sql = "SELECT * FROM students ORDER BY Course, name";
        Map<String, List<Student>> groupedStudents = new LinkedHashMap<>();

        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String course = rs.getString("Course");
                Student student = new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("Enrollment_number"),
                    course,
                    rs.getDouble("CGPA"),
                    rs.getTimestamp("Registration_Date").toString()
                );
                groupedStudents.computeIfAbsent(course, k -> new ArrayList<>()).add(student);
            }
            response.getWriter().write(gson.toJson(groupedStudents));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error fetching students.\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        if (action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"No action specified.\"}");
            out.flush();
            return;
        }

        try {
            switch (action) {
                case "add":
                    addStudent(request, response);
                    break;
                case "search":
                    searchStudent(request, response);
                    break;
                case "update":
                    updateStudent(request, response);
                    break;
                case "delete":
                    deleteStudent(request, response);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Invalid action.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Operation failed: " + e.getMessage().replace("\"", "'") + "\"}");
            e.printStackTrace();
        }
        out.flush();
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String sql = "INSERT INTO students (name, Enrollment_number, Course, CGPA) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getParameter("name"));
            ps.setString(2, request.getParameter("enrollmentNumber"));
            ps.setString(3, request.getParameter("course"));
            ps.setDouble(4, Double.parseDouble(request.getParameter("cgpa")));
            ps.executeUpdate();
            response.getWriter().print("{\"message\": \"Student added successfully.\"}");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().print("{\"error\": \"This Enrollment Number already exists.\"}");
            } else {
                throw e;
            }
        }
    }
    
    private void searchStudent(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String enrollmentNumber = request.getParameter("enrollmentNumber");
        Student student = null;
        String sql = "SELECT * FROM students WHERE Enrollment_number = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enrollmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("Enrollment_number"),
                        rs.getString("Course"),
                        rs.getDouble("CGPA"),
                        rs.getTimestamp("Registration_Date").toString()
                    );
                }
            }
        }
        if (student != null) {
            response.getWriter().print(gson.toJson(student));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().print("{\"error\": \"Student not found.\"}");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String sql = "UPDATE students SET name = ?, Enrollment_number = ?, Course = ?, CGPA = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getParameter("name"));
            ps.setString(2, request.getParameter("enrollmentNumber"));
            ps.setString(3, request.getParameter("course"));
            ps.setDouble(4, Double.parseDouble(request.getParameter("cgpa")));
            ps.setInt(5, Integer.parseInt(request.getParameter("id")));
            ps.executeUpdate();
            response.getWriter().print("{\"message\": \"Student details updated.\"}");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().print("{\"error\": \"This Enrollment Number is already used by another student.\"}");
            } else {
                throw e;
            }
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().print("{\"message\": \"Student deleted successfully.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print("{\"error\": \"Student not found for deletion.\"}");
            }
        }
    }
}