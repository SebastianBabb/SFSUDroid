package woverines.sfsuapp.models;


import java.util.List;

public class CoursesModels {
    public List<Course> classes;

    public class Course {
        public int course_id;
        public String course_number;
        public String section_number;
        public String course_name;
        public String course_subject;
        public String course_description;
        public String course_time;
        public String course_meeting_day;
        public String course_teacher_name;
        public String teacher_first_name;
        public String teacher_id;
        public String teacher_last_name;
    }
}
