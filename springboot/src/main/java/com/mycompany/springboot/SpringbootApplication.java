package com.mycompany.springboot;
import com.sun.jersey.api.client.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;
public class SpringbootApplication {

    public static void main(String[] args) {
        JsonStudent database = new JsonStudent();
        try{
        database.populateStudents("https://hccs-advancejava.s3.amazonaws.com/student_course.json");
        } catch(ParseException e) {System.out.println("The parse failed.");}
        System.out.println(database);
        System.out.println(database.searchByName("Aida"));
        System.out.println(database.searchByCourseID("CS12"));
    }
}
class JsonStudent
{
    private ArrayList<Student> students;
    public void populateStudents(String url) throws ParseException
    {
        students = new ArrayList<Student>();
        Client client = Client.create();
        WebResource webResource = client.resource(url);
        
        ClientResponse clientResponse = webResource.accept("application/json").get(ClientResponse.class);
        if(clientResponse.getStatus() != 200)
        {
            throw new RuntimeException("Parse failed: " + clientResponse.toString());
        }
        
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(clientResponse.getEntity(String.class));
        
        Iterator<Object> it = jsonArray.iterator();
        
        while(it.hasNext())
        {
            JSONObject jsonObject = (JSONObject)it.next();
            ArrayList<Course> courseList = new ArrayList<Course>();
            JSONArray courseJsonArray = (JSONArray)jsonObject.get("course");
            if(courseJsonArray != null)
            {
                Iterator<Object> courseIt = courseJsonArray.iterator();
                while(courseIt.hasNext())
                {
                    JSONObject jsonCourse = (JSONObject)courseIt.next();
                    courseList.add(new Course((String)jsonCourse.get("courseNo"), ((String)jsonCourse.get("grade")).charAt(0), (long)jsonCourse.get("creditHours")));
                }
            }
            students.add(new Student((long)jsonObject.get("id"), (String)jsonObject.get("first_name"), courseList, (String)jsonObject.get("email"), (String)jsonObject.get("gender")));
        }
        for(int x = 0; x < students.size(); x++)
        {
            students.get(x).computeGPA();
        }
    }
    public ArrayList<Student> searchByName(String n)
    {
        ArrayList<Student> ans = new ArrayList<Student>();
        for(int x = 0; x < students.size(); x++)
        {
            if(students.get(x).getFirstName().equals(n))
                ans.add(students.get(x));
        }
        return ans;
    }
    public ArrayList<Student> searchByCourseID(String i)
    {
        ArrayList<Student> ans = new ArrayList<Student>();
        for(int x = 0; x < students.size(); x++)
        {
            for(int y = 0; y < students.get(x).getCourses().size(); y++)
            {
                if(students.get(x).getCourses().get(y).getIdNumber().equals(i))
                ans.add(students.get(x));
            }
        }
        return ans;
    }
    @Override 
    public String toString()
    {
        String all = "";
        for(int x = 0; x < students.size(); x++)
            all = all + students.get(x).toString() + '\n';
        return all;
    }
}
class Course
{
    private String idNumber;
    private char grade;
    private long hours;
    public Course(String i, char g, long h)
    {
        idNumber = i;
        grade = g;
        hours = h;
    }
    public String getIdNumber()
    {
        return idNumber;
    }
    public char getGrade()
    {
        return grade;
    }
    public long getHours()
    {
        return hours;
    }
    @Override 
    public String toString()
    {
        return "Course #" + idNumber + ". " + grade + " received, " + hours + " hours awarded.";
    }
}
class Student
{
    private long id;
    private String firstName;
    private double gpa;
    private String email;
    private String gender;
    private ArrayList<Course> courses;
    public Student(long i, String n, ArrayList<Course> c, String e, String d)
    {
        id = i;
        firstName = n;
        courses = c;
        email = e;
        gender = d;
    }
    public void computeGPA()
    {
        double tot = 0.;
        for(int x = 0; x < courses.size(); x++)
        {
            tot += (69. - courses.get(x).getGrade());
        }
        gpa = tot / (double)courses.size(); 
    }
    public long getID()
    {
        return id;
    }
    public double getGPA()
    {
        return gpa;
    }
    public String getFirstName()
    {
        return firstName;
    }
    public String getEmail()
    {
        return email;
    }
    public String getGender()
    {
        return gender;
    }
    public ArrayList<Course> getCourses()
    {
        return courses;
    }
    @Override 
    public String toString()
    {
        String courseList = "";
        for(int x = 0; x < courses.size(); x++)
            courseList = courseList + courses.get(x).toString() + '\n';
        return firstName + ", ID#" + id + ". " + gpa + " GPA, " + gender + ", " + email + ".\n" + courseList;
    }
}
