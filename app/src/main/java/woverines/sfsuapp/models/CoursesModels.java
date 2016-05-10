package woverines.sfsuapp.models;


import java.util.List;

public class CoursesModels {
    public List<Course> classes;

    public class Course {
        public int course_id;
        public String course_number;
        public String course_section;
        public String course_title;
        public String course_department;
        public String course_description;
        public String course_time;
        public String course_meetTime;
        public String course_meetDays;
        public String course_instructor;
        public String course_meetRoom;

    }
}
