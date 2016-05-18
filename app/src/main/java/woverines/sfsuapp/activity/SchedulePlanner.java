package woverines.sfsuapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.ALERTS_TABLE;
import woverines.sfsuapp.database.Alert;
import woverines.sfsuapp.database.COURSE_TABLE;
import woverines.sfsuapp.database.Course;
import woverines.sfsuapp.database.Event;

public class SchedulePlanner extends AppCompatActivity {

    private static final int REQUEST_CODE_ALERTS = 1;
    private static final String PREF_COURSES = "PrefCourses";
    private static final String COURSES_IDS = "MyCourses";
    SharedPreferences sharedPrefs;

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());

    private ScheduleListAdapter scheduleAdapter;
    public ArrayList<Course> courseArrayList;
    public ListView courseListView;
    public Dialog courseDetailDialog;
    public Button add_alert_b;

    TextView detailNameTV;
    TextView detailTimeTV;
    TextView detailNumberTV;
    TextView detailInstructorTV;
    TextView detailDescriptionTV;
    TextView detailMeetDaysTV;
    Button detailCancelB;
    Button detailAddEventB;
    ImageButton detailDeleteCourseIB;

    private ImageButton refreshCourses;

    private ViewGroup detailAlerts;

//    private
    public ArrayList<Event> eventArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create array of myCourses
        courseArrayList = new ArrayList<>();


        courseListView = (ListView) findViewById(R.id.course_list_view);

        //SET UP REVIEW DIALOG
        courseDetailDialog = new Dialog(SchedulePlanner.this);
        courseDetailDialog.setContentView(R.layout.dialog_schedule_planner);
        courseDetailDialog.setTitle("Course Options");

        //connecting TextViews to dialog
        detailNumberTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_number);
        detailNameTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_name);
        detailInstructorTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_instructor);
        detailTimeTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_time);
        detailDescriptionTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_description);
        detailMeetDaysTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_meet_days);
        detailAlerts = (ViewGroup) courseDetailDialog.findViewById(R.id.listView);
        detailCancelB = (Button) courseDetailDialog.findViewById(R.id.dialog_cancel_button);
        detailAddEventB = (Button) courseDetailDialog.findViewById(R.id.dialog_add_event);
        detailDeleteCourseIB = (ImageButton) courseDetailDialog.findViewById(R.id.delete_course_ib);

        ImageButton add_class_ib = (ImageButton) findViewById(R.id.add_course_ib);
        add_class_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCatalog();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshCourses = (ImageButton) findViewById(R.id.refresh_courses_ib);

        refreshCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCourses();
                displaySchedule();
            }
        });



        getCourses();
        displaySchedule();

     }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        scheduleAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ALERTS:
                handleAlert(resultCode, data);
                break;
        }

        super.onActivityResult(resultCode, resultCode, data);
    }




    /**Retrieve all* courses from database (THESE ARE 'MY SCHEDULE' courses only!)
     * currently "1" is being set in as a selectionArgs value
     *  this can be anything we want if we need to filter classes to show
     *
     */
    private void getCourses() {
        //we can add filtering for courses for today... ect here
//        courseArrayList.clear();
        courseArrayList = COURSE_TABLE.getCourses(this, "1");

    }


    /**Add a course to COURSE_TABLE DB
     *   pass in params
     *  spearatly and perform any parsing before creating a Course
     *  and passing it to COURSE_TABLE.addCourse
     * @param id
     * @param department
     * @param number
     * @param section
     * @param name
     * @param meetDays
     * @param meetTime
     * @param meetRoom
     * @param description
     * @return db table id
     */
    private long addCourseToPlanner(int id, String department, String number, String section, String name, String meetDays, String meetTime, String meetRoom, String instructor, String description){

        Course course = new Course(id, department, number, section, name, meetDays, meetTime, meetRoom, instructor, description);

        return COURSE_TABLE.addCourse(this, course);
    }

    /**add a course by sending in a Course
     *
     * @param course
     * @return db table id
     */
    private long addCourseToPlanner(Course course){
        return COURSE_TABLE.addCourse(this, course);
    }


    public void displaySchedule() {

        //Populate the list
        if (scheduleAdapter == null) {  //if first time opening activity
            scheduleAdapter = new ScheduleListAdapter(this, R.layout.course_list_item, courseArrayList);
            courseListView.setAdapter(scheduleAdapter);
        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            scheduleAdapter.notifyDataSetChanged();
                        }
                    });
            return;
        }


        //creating onClick for each item in courseList
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Course course = courseArrayList.get(position);
                final String name = course.getName();
                //setting up courseDetailsDialog
                String numberText = course.getDepartment() + " " + course.getNumber();
                if (course.getSection() != null && !course.getSection().isEmpty()) {
                    numberText += "." + course.getSection();
                }
                detailNumberTV.setText(numberText);
                detailNameTV.setText(course.getName());
                detailInstructorTV.setText(course.getInstructor());
                detailTimeTV.setText(course.getMeetTime());
                detailDescriptionTV.setText(course.getDescription());
                detailMeetDaysTV.setText(course.getMeetDays());

                final int courseId = course.getId();
                refreshAlerts(courseId);

                //add go to addEven activity
                detailAddEventB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToAlerts(courseId, null);
                    }
                });

                //hide dialog button
                detailCancelB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        courseDetailDialog.dismiss();
                    }
                });

                detailDeleteCourseIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteCourseByName(name);
                        getCourses();
                        displaySchedule();
                        Toast.makeText(getApplicationContext(), "Course deleted from schedule", Toast.LENGTH_SHORT).show();

                        courseDetailDialog.dismiss();
                        finish();

                    }
                });
                //dialog has been prepared, display it.
                courseDetailDialog.show();
            }

        });
    }

    /**
     *
     * @param name
     */
    public void deleteCourseByName(String name){
        COURSE_TABLE.deleteCourse(getApplicationContext(), name);
//        refreshCourseList();
    }

    /**
     *
     */
    public void refreshCourseList(){
        courseArrayList.clear();
        getCourses();
        scheduleAdapter.notifyDataSetChanged();
    }


    public void refreshAlerts(int courseId) {
        detailAlerts.removeAllViews();

        List<Alert> alerts = ALERTS_TABLE.getAlerts(getApplicationContext(), courseId, 0);
        for (Alert alert : alerts) {
            insertAlert(alert, -1);
        }
    }

    public void goToAlerts(int courseId, Alert alert) {
        Intent goToAlertsIntent = new Intent(this, AlertsActivity.class);
        goToAlertsIntent.putExtra(AlertsActivity.EXTRA_COURSE_ID, courseId);
        goToAlertsIntent.putExtra(AlertsActivity.EXTRA_ALERT, alert);

        startActivityForResult(goToAlertsIntent, REQUEST_CODE_ALERTS);
    }

    public void handleAlert(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int courseId = data.getIntExtra(AlertsActivity.EXTRA_COURSE_ID, 0);
            Alert alert = data.getParcelableExtra(AlertsActivity.EXTRA_ALERT);

            if (alert != null) {
                refreshAlerts(courseId);
            }
        }
    }

    private void insertAlert(final Alert alert, int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alerts_list_item, detailAlerts, false);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(alert.alertText);

        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(DATE_FORMAT.format(alert.time));

        if (alert.time < System.currentTimeMillis()) {
            int color = getResources().getColor(android.R.color.darker_gray);

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setColorFilter(color);

            text.setTextColor(color);
            time.setTextColor(color);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlerts(alert.courseId, alert);
            }
        });

        if (position < 0) {
            detailAlerts.addView(view);
        } else {
            detailAlerts.addView(view, position);
        }
    }

    public void goToCatalog()
    {
        Intent goToCatalog = new Intent(this, ClassCatalog.class);
        startActivity(goToCatalog);
    }


    /**
     * Custom List Array Adapter
     *
     * @author Andrey Barsukov
     *         This is a custom adapter that uses a system of recycling a single view
     *         to fill up a scrollListView.
     *         <p/>
     *         This class uses a custom view holder class that stores only the nessesary values of
     *         a course that are required for a Course list view item.
     *         <p/>
     *         Layout Recycling system:
     *         - create a single instance of a ListView item layout course_list_itemml.
     *         if(ListView is empty){
     *         create a new instance of all views inside course_list_itemml
     *         create an instance of a ScheduleViewHolder class
     *         -set values of ScheduleViewHolder with the first course is courseList
     *         -attach each element of ScheduleViewHolder to its corresponding element in course_list_item.xml    *         -send the current instance of ScheduleViewHolder to the ListView Adapter
     *         }
     *         else{
     *         -set values of ScheduleViewHolder with the (position)th course is couseList
     *         -attach each element of ScheduleViewHolder to its corresponding element in course_list_itemml
     *         -send the current instance of ScheduleViewHolder to the ListView Adapter
     *         <p/>
     *         return the current instance of ScheduleViewHolder
     *         }
     *         <p/>
     *         RESULT: Only one instance of course_list_item.xmlnd each of its view is created
     *         -this since instance of it gets recycles for each aditional item in the ListView
     */
    private class ScheduleListAdapter extends ArrayAdapter<Course> {
        private int layout;      //stores references to recipe_list_item layouts

        public ScheduleListAdapter(Context context, int resource, ArrayList<Course> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        //setting up items in list
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //instead of creating a new course_list_item view, check if it exists,
            //if exits: reuse the view with new data - using .from(getContext()), context returned by super call
            //if does not exist: use inflater to create it (inflater is very taxing)
            Course thisCourse = getItem(position);
            ScheduleViewHolder mainViewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ScheduleViewHolder viewHolder = new ScheduleViewHolder();


                //linking widgets
                viewHolder.number = (TextView) convertView.findViewById(R.id.course_number);
                viewHolder.name = (TextView) convertView.findViewById(R.id.course_title);
                viewHolder.instructor = (TextView) convertView.findViewById(R.id.dialog_course_instructor);
                viewHolder.meetTime = (TextView) convertView.findViewById(R.id.course_meet_time);
                viewHolder.meetDays = (TextView) convertView.findViewById(R.id.course_meet_days);
//                viewHolder.meetRoom = (TextView) convertView.findViewById(R.id.course_meet_room);


                //setting info
                viewHolder.name.setText(thisCourse.getName());
                String numberText = thisCourse.getDepartment() + " " + thisCourse.getNumber();
                if (thisCourse.getSection() != null && !thisCourse.getSection().isEmpty()) {
                    numberText += "." + thisCourse.getSection();
                }
                viewHolder.number.setText(numberText);
                viewHolder.instructor.setText(thisCourse.getInstructor());
                viewHolder.meetTime.setText(thisCourse.getMeetTime());
//                viewHolder.meetRoom.setText(thisCourse.getMeetRoom());
                viewHolder.meetDays.setText(thisCourse.getMeetDays());
                    /*   add a reference of this object into convertView, so we convertView != null
                        we can retrieve the object and directly set the new data to the ScheduleViewHolder items
                         (recyle this object)
                     */
                convertView.setTag(viewHolder);
            } else {  //converView != null --> retrieve viewHolder (using tag), assign it to externally declared ScheduleViewHolder (mainViewHolder)

                mainViewHolder = (ScheduleViewHolder) convertView.getTag();
                //manually set the data of list view items here//
                String numberText = thisCourse.getDepartment() + " " + thisCourse.getNumber();
                if (thisCourse.getSection() != null && !thisCourse.getSection().isEmpty()) {
                    numberText += "." + thisCourse.getSection();
                }
                mainViewHolder.number.setText(numberText);
                mainViewHolder.name.setText(thisCourse.getName());
                mainViewHolder.instructor.setText(thisCourse.getInstructor());
                mainViewHolder.meetTime.setText(thisCourse.getMeetTime());
//                mainViewHolder.meetRoom.setText(thisCourse.getMeetRoom());
                mainViewHolder.meetDays.setText(thisCourse.getMeetDays());
            }

            return convertView;
            //return super.getView(position, convertView, parent);
        }
    }

    /**
     * Holds values for ListView adapter
     *
     * @author Andrey Barsukov
     *         viewHolder class
     *         used to hold the references of data in List<Course> array so we dont have to call the array
     *         items by id everytime.
     */

    public class ScheduleViewHolder {
        TextView number;
        TextView name;
        TextView instructor;
        TextView meetTime;
        TextView meetRoom;
        TextView meetDays;
    }

}


