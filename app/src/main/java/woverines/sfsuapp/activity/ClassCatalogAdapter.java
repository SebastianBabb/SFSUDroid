package woverines.sfsuapp.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.Course;

public class ClassCatalogAdapter extends RecyclerView.Adapter<ClassCatalogAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private CatalogClickListener listener;

    public interface CatalogClickListener {
        void onCatalogItemClicked (int position);
    }

    public ClassCatalogAdapter(CatalogClickListener listener, List<Course> courses) {
        this.listener = listener;
        this.courseList = courses;
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_catalog_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        String courseNumber = course.getDepartment() + " " + course.getNumber();
        if (course.getSection() != null && !course.getSection().isEmpty()) {
            courseNumber += "-" + course.getSection();
        }
        holder.courseNumber.setText(courseNumber);
        holder.courseTitle.setText(course.getName());
        holder.courseMeetDays.setText(course.getMeetDays());
        holder.courseMeetTime.setText(course.getMeetTime());
//        holder.courseMeetRoom.setText(course.getMeetRoom());
        holder.courseInstructor.setText(course.getInstructor());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView courseNumber;
        public TextView courseTitle;
        public TextView courseMeetDays;
        public TextView courseMeetTime;
        public TextView courseMeetRoom;
        public TextView courseInstructor;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseNumber = (TextView) itemView.findViewById(R.id.course_number);
            courseTitle = (TextView) itemView.findViewById(R.id.course_title);
            courseMeetDays = (TextView) itemView.findViewById(R.id.course_meet_days);
            courseMeetTime = (TextView) itemView.findViewById(R.id.course_meet_time);
            courseMeetRoom = (TextView) itemView.findViewById(R.id.course_meet_room);
            courseInstructor = (TextView) itemView.findViewById(R.id.dialog_course_instructor);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onCatalogItemClicked(getAdapterPosition());
        }
    }
}
