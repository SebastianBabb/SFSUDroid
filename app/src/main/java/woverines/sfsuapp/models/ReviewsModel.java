package woverines.sfsuapp.models;


import java.util.List;

public class ReviewsModel {
    public int professerId;
    public String firstName;
    public String lastName;
    public List<Review> reviews;

    public class Review{
        public int reviewId;
        public String time;
        public String className;
        public String review;
    }
}
