package com.sms.backend; 
public class Student {
    private int id;
    private String name;
    private String enrollmentNumber;
    private String course;
    private double cgpa;
    private String registrationDate;

    public Student(int id, String name, String enrollmentNumber, String course, double cgpa, String registrationDate) {
        this.id = id;
        this.name = name;
        this.enrollmentNumber = enrollmentNumber;
        this.course = course;
        this.cgpa = cgpa;
        this.registrationDate = registrationDate; 
    }

    
    public Student(String name, String enrollmentNumber, String course, double cgpa) {
        this.name = name;
        this.enrollmentNumber = enrollmentNumber;
        this.course = course;
        this.cgpa = cgpa;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEnrollmentNumber() { return enrollmentNumber; }
    public String getCourse() { return course; }
    public double getCgpa() { return cgpa; }

    public String getRegistrationDate() { return registrationDate; }
}