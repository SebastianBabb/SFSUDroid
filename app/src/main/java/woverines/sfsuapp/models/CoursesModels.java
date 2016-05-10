package woverines.sfsuapp.models;


import java.util.List;

public class CoursesModels {
    public List<Course> classes;

    public class Course {
        public int course_id;
        public int course_number;
        public String course_description;
        public String course_name;
        public String course_time;
        //TODO ADD more stuff.
    }
}
