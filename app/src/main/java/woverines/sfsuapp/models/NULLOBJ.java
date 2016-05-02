package woverines.sfsuapp.models;

import java.util.List;

public class NULLOBJ {
    public int classId = 999999;
    public int professorId = 99999;
    public String className = "Not available";
    public String firstName = "Not available";
    public String lastName = "Not available";
    public List<Course> courses;
    public List<Forum> forums;
    public List<Review> reviews;

    public class Course{
        public int classNumber = 9999;
        public String className = "Not available";
        public String classPereq = "Not available";
        public String classDate = "Not available";
        public String classMeetingDay = "Not available";
    }

    public class Forum {
        public int forumId = 99999;
        public String time = "00/00/0000";
        public String message = "No message";
    }

    public class Review{
        public int reviewId = 99999;
        public String time = "00/00/0000";
        public String className = "Not available";
        public String review = "Not available";
    }

}
