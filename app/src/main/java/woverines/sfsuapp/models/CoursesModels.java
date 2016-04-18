package woverines.sfsuapp.models;


import java.util.List;

public class CoursesModels {
    List<Course> courses;

    public class Course{
        public int classNumber;
        public String className;
        public String classPereq;
        public String classDate;
        public String classMeetingDay;
    }
}
