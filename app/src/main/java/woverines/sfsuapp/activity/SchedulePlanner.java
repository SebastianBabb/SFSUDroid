package woverines.sfsuapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.ALERTS_TABLE;
import woverines.sfsuapp.database.Alerts;
import woverines.sfsuapp.database.Course;
import woverines.sfsuapp.database.Event;

public class SchedulePlanner extends AppCompatActivity {

    private static final int REQUEST_CODE_ALERTS = 1;

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

    private ViewGroup detailAlerts;

//    private
    public ArrayList<Event> eventArray;

    //for testing
    Button genRadomClassesB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_planner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create array of myCourses
        courseArrayList = new ArrayList<>();

        genRadomClassesB = (Button) findViewById(R.id.gen_random_classes);
        genRadomClassesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateClasses();
            }
        });

        courseListView = (ListView) findViewById(R.id.course_list_view);

        //SET UP REVIEW DIALOG
        courseDetailDialog = new Dialog(SchedulePlanner.this);
        courseDetailDialog.setContentView(R.layout.course_details_dialog);
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

        ImageButton add_class_ib = (ImageButton) findViewById(R.id.add_course_ib);
        add_class_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCatalog();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        add_class_ib = (Button) findViewById(R.id.)

        setDemoCourseList();
        displaySchedule();
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ALERTS:
                handleAlert(resultCode, data);
                break;
        }

        super.onActivityResult(resultCode, resultCode, data);
    }

    private void setDemoCourseList() {
        courseArrayList = new ArrayList<>();
        courseArrayList.add(new Course(2365, "CSC", "668", "01", "Advanced Object Oriented Programming", "Tu, Th", "11:00AM - 12:10PM", "Thornton Hall 310", "Barry Levine", "Students will learn advanced object-oriented programming techniques through group-work"));
        courseArrayList.add(new Course(2361, "CSC", "256", "03", "Machine Structure", "Th", "4:10PM - 6:55PM", "Science Building 101", "Tsun-Yuk Hsu", "Prerequisite: CSC 230 or CSC 330 with grade of C or better. Digital logic circuits; data representation; assembly language programming; subroutine linkage; machine language encoding; interrupt/exception handling; memory system concepts; CPU organization and performance."));
        courseArrayList.add(new Course(2372, "CSC", "413", "02", "Software Development", "M, W, F", "12:10PM - 1:00PM", "Thornton Hall 329", "Marc Sosnick", "Prerequisites: CSC 340 and CSC 412 with grades of C or better. \n" +
                "Modern software applications. Object-oriented techniques: encapsulation, inheritance, and poly-morphism as mechanism for data design and problem solution. Software design, debugging, testing, and UI design. Software maintenance. Software development tools. Extra fee required. (Plus-minus letter grade only)"));
        }


    public void generateClasses()
    {
        scheduleAdapter.clear();

        Course tempCourse = new Course(1, "668", "Advanced OOP", "Levine", "11:00 - 12:15", "somethign something...");
        courseArrayList.add(tempCourse);

        Random rnd = new Random();

        for(int i = 0; i < rnd.nextInt(15); i++)
        {
            tempCourse = new Course();
            tempCourse.genRandomCourse2();
            courseArrayList.add(tempCourse);
        }

        scheduleAdapter.notifyDataSetChanged();
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
                //setting up courseDetailsDialog
                detailNumberTV.setText(course.getDepartment() + " " + course.getNumber() + "." + course.getSection());
                detailNameTV.setText(course.getName());
                detailInstructorTV.setText(course.getInstructor());
                detailTimeTV.setText(course.getMeetTime());
                detailDescriptionTV.setText(course.getDescription());
                detailMeetDaysTV.setText(course.getMeetDays());


                detailAlerts.removeAllViews();

                final int courseId = course.getId();
                List<Alerts> alerts = ALERTS_TABLE.getAlerts(getApplicationContext(), courseId, 0);

                if (!alerts.isEmpty()) {
                    for (Alerts alert : alerts) {
                        insertAlert(alert, -1);
                    }
                }

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
                //dialog has been prepared, display it.
                courseDetailDialog.show();
            }

        });
    }

    public void goToAlerts(int courseId, Alerts alert)
    {
        AlertsActivity.Alert instance = null;
        if (alert != null) {
            instance = new AlertsActivity.Alert(alert);
        }

        Intent goToAlertsIntent = new Intent(this, AlertsActivity.class);
        goToAlertsIntent.putExtra(AlertsActivity.EXTRA_COURSE_ID, courseId);
        goToAlertsIntent.putExtra(AlertsActivity.EXTRA_ALERT, instance);

        startActivityForResult(goToAlertsIntent, REQUEST_CODE_ALERTS);
    }

    public void handleAlert(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int courseId = data.getIntExtra(AlertsActivity.EXTRA_COURSE_ID, 0);
            AlertsActivity.Alert alert = data.getParcelableExtra(AlertsActivity.EXTRA_ALERT);

            if (alert != null) {
                Alerts instance = new Alerts(
                    alert.id,
                    courseId,
                    alert.time,
                    alert.alertText,
                    alert.reminder,
                    alert.sound != null ? alert.sound.toString() : null,
                    alert.vibrate ? 1 : 0,
                    alert.repeat ? 1 : 0
                );

                insertAlert(instance, 0);
            }
        }
    }

    private void insertAlert(final Alerts alert, int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alerts_list_item, detailAlerts, false);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(alert.getText());

        TextView time = (TextView) view.findViewById(R.id.time);
        time.setText(DATE_FORMAT.format(alert.getmTime()));

        if (alert.getmTime() < System.currentTimeMillis()) {
            int color = getResources().getColor(android.R.color.darker_gray);

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setColorFilter(color);

            text.setTextColor(color);
            time.setTextColor(color);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlerts(alert.getCourseID(), alert);
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
                //                viewHolder.clock = (ImageView) convertView.findViewById(R.id.recipe_list_item_clock);
                viewHolder.name = (TextView) convertView.findViewById(R.id.course_title);
                viewHolder.instructor = (TextView) convertView.findViewById(R.id.dialog_course_instructor);
                viewHolder.meetTime = (TextView) convertView.findViewById(R.id.course_meet_time);
                viewHolder.meetDays = (TextView) convertView.findViewById(R.id.course_meet_days);
                viewHolder.meetRoom = (TextView) convertView.findViewById(R.id.course_meet_room);


                //setting info
                viewHolder.name.setText(thisCourse.getName());
                viewHolder.number.setText(thisCourse.getDepartment() + " " + thisCourse.getNumber() + "-" + thisCourse.getSection());
                viewHolder.instructor.setText(thisCourse.getInstructor());
                viewHolder.meetTime.setText(thisCourse.getMeetTime());
                viewHolder.meetRoom.setText(thisCourse.getMeetRoom());
                viewHolder.meetDays.setText(thisCourse.getMeetDays());
                    /*   add a reference of this object into convertView, so we convertView != null
                        we can retrieve the object and directly set the new data to the ScheduleViewHolder items
                         (recyle this object)
                     */
                convertView.setTag(viewHolder);
            } else {  //converView != null --> retrieve viewHolder (using tag), assign it to externally declared ScheduleViewHolder (mainViewHolder)

                mainViewHolder = (ScheduleViewHolder) convertView.getTag();
                //manually set the data of list view items here//
                mainViewHolder.number.setText(thisCourse.getDepartment() + " " + thisCourse.getNumber() + "." +  thisCourse.getSection());
                mainViewHolder.name.setText(thisCourse.getName());
                mainViewHolder.instructor.setText(thisCourse.getInstructor());
                mainViewHolder.meetTime.setText(thisCourse.getMeetTime());
                mainViewHolder.meetRoom.setText(thisCourse.getMeetRoom());
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


