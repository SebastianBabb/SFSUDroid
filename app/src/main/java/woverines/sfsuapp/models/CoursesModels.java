package woverines.sfsuapp.models;


import java.util.List;

public class CoursesModels {
    public List<Course> classes;

    public class Course implements Comparable<Course>{
        public int course_id;
        public String course_description;
        public String course_meeting_day;
        public String course_name;
        public int course_number;
        public String course_subject;
        public String course_time;
        public String section_number;
        public String teacher_first_name;
        public int teacher_id;
        public String teacher_last_name;

        @Override
        public int compareTo(Course thatCourse) {
            int diff = this.course_subject.compareTo(thatCourse.course_subject);
            if (diff == 0) {
                diff = this.course_number - thatCourse.course_number;
                if (diff == 0) {
                    if (this.section_number != null && thatCourse.section_number != null) {
                        return this.section_number.compareTo(thatCourse.section_number);
                    }
                }
                return diff;
            }
            return diff;
        }
    }
}
