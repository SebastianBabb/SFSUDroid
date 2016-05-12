package woverines.sfsuapp.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import woverines.sfsuapp.R;
import woverines.sfsuapp.api.API_RequestBuilder;
import woverines.sfsuapp.api.Callback;
import woverines.sfsuapp.api.HttpRequestorManager;
import woverines.sfsuapp.database.COURSE_TABLE;
import woverines.sfsuapp.database.Course;
import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.DepartmentsModel;
import woverines.sfsuapp.models.NULLOBJ;

public class ClassCatalog extends AppCompatActivity implements ClassCatalogAdapter.CatalogClickListener {
    private Map<String, String> departmentMap;
    private List<String> departmentList;

    private List<Course> courseList;
    private ClassCatalogAdapter adapter;
    private RecyclerView courseListView;
    private TextView departmentInput;
    private Dialog courseDetailDialog;
    private TextView detailNumberTV;
    private TextView detailNameTV;
    private TextView detailInstructorTV;
    private TextView detailDayTV;
    private TextView detailTimeTV;
    private TextView detailDescriptionTV;
    private Button detailCancelB;
    private Button detailAddEventB;

    private API_RequestBuilder api_requestBuilder;
    private DepartmentsModel departments;
    private CoursesModels courses;
    private List<Course> myCourseList;
    private Course selectedCourse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setDemoCourseList();
//        setDemoDepartmentMap();
        setDepartmentMap();
        courseList = new ArrayList<>();
        myCourseList = COURSE_TABLE.getCourses(this, "1");
        adapter = new ClassCatalogAdapter(this, courseList);
        courseListView = (RecyclerView) findViewById(R.id.catalog_course_list);
        courseListView.setLayoutManager(new LinearLayoutManager(this));
        courseListView.setAdapter(adapter);
        departmentInput = (AutoCompleteTextView) findViewById(R.id.department_input);

        courseDetailDialog = new Dialog(this);
        courseDetailDialog.setContentView(R.layout.catalog_details_dialog_new);
        courseDetailDialog.setTitle("Course Options");
        //connecting TextViews to dialog
        detailNumberTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_number);
        detailNameTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_title);
        detailInstructorTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_instructor);
        detailTimeTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_time);
        detailDayTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_days);
        detailDescriptionTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_description);
        //connecting Buttons to dialog
        detailCancelB = (Button) courseDetailDialog.findViewById(R.id.dialog_cancel_button);
        detailCancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseDetailDialog.dismiss();
            }
        });
        detailAddEventB = (Button) courseDetailDialog.findViewById(R.id.dialog_add_class);
        detailAddEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSelectedCourse();
                detailAddEventB.setEnabled(false);
            }
        });

        final AutoCompleteTextView departmentInput = (AutoCompleteTextView) findViewById(R.id.department_input);
//        departmentList = new ArrayList<>(departmentMap.keySet());

//        Collections.sort(departmentList);
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<String>(this, R.layout.department_autocomplete_popup_item, departmentList);
        departmentInput.setAdapter(departmentAdapter);
        departmentInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departmentInput.showDropDown();
            }
        });

        HttpRequestorManager.initialize(this);
        api_requestBuilder = new API_RequestBuilder();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void addSelectedCourse() {
        if (selectedCourse == null) {
            return;
        }
        COURSE_TABLE.addCourse(this, selectedCourse);
        myCourseList.add(selectedCourse);
        selectedCourse = null;
        Toast.makeText(this, "Course added to schedule", Toast.LENGTH_SHORT).show();
    }

    private void setCourseListFromCoursesModels(CoursesModels models) {
        courseList.clear();
        for (CoursesModels.Course apiCourse : models.classes) {
            int firstSpaceIndex = apiCourse.course_time.indexOf(' ');
            String courseDays = "", courseTime;
            if (firstSpaceIndex > 0) {
                courseDays = apiCourse.course_time.substring(0, firstSpaceIndex);
                courseTime = apiCourse.course_time.substring(firstSpaceIndex+1);
            } else {
                courseTime = apiCourse.course_time;
            }

            Course course = new Course(0, apiCourse.course_subject, apiCourse.course_number, apiCourse.section_number,
                    apiCourse.course_name, courseDays, courseTime, "", apiCourse.course_teacher_name, apiCourse.course_description);
            courseList.add(course);
        }
    }

    public void filterCourses(View view) {
        final String selectedDepartment = departmentInput.getText().toString();
        if (selectedDepartment.isEmpty()) {
            return;
        }
        if (!departmentList.contains(selectedDepartment)) {
            Toast.makeText(this, "Not a valid department. Please check your selection.", Toast.LENGTH_LONG).show();
        }
//        String selectedCourseNumber = courseNumberInput.getText().toString();

        this.courses = new CoursesModels();
        final String departmentCode = departmentMap.get(selectedDepartment);
        api_requestBuilder.populateModel(departmentCode, this.courses, new Callback() {
            @Override
            public void response(Object object) {
                courses  = (CoursesModels) object;
                setCourseListFromCoursesModels((CoursesModels) object);
                adapter.notifyDataSetChanged();
                Toast.makeText(ClassCatalog.this, "Courses retrieved from server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(NULLOBJ nullObj) {
                Toast.makeText(ClassCatalog.this, "Server error. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showCourseDetails(View view) {

    }

//    private void setDemoCourseList() {
//        courseList = new ArrayList<>();
//        courseList.add(new Course(2365, "CSC", "668", "01", "Advanced Object Oriented Programming", "Tu, Th", "11:00AM - 12:10PM", "Thornton Hall 310", "Barry Levine", "Students will learn advanced object-oriented programming techniques through group-work"));
//        courseList.add(new Course(2361, "CSC", "256", "03", "Machine Structure", "Th", "4:10PM - 6:55PM", "Science Building 101", "Tsun-Yuk Hsu", "Prerequisite: CSC 230 or CSC 330 with grade of C or better. Digital logic circuits; data representation; assembly language programming; subroutine linkage; machine language encoding; interrupt/exception handling; memory system concepts; CPU organization and performance."));
//        courseList.add(new Course(2372, "CSC", "413", "02", "Software Development", "M, W, F", "12:10PM - 1:00PM", "Thornton Hall 329", "Marc Sosnick", "Prerequisites: CSC 340 and CSC 412 with grades of C or better. \n" +
//                "Modern software applications. Object-oriented techniques: encapsulation, inheritance, and poly-morphism as mechanism for data design and problem solution. Software design, debugging, testing, and UI design. Software maintenance. Software development tools. Extra fee required. (Plus-minus letter grade only)"));
//        courseList.add(new Course(2378, "CSC", "667", "01", "Internet Application Design and Development", "M", "7:00PM - 9:45PM", "Thornton Hall 210", "John Roberts", "Prerequisite: CSC 413 with grade of C or better or consent of instructor.\n" +
//                "Fundamental technologies on which WWW is based. Extra fee required.\n" +
//                "(CSC 667/CSC 867 is a paired course offering. Students who complete the course at one level may not repeat the course at the other level.)"));
//        courseList.add(new Course(4789, "SW", "352", "01", "Gender, Sexism, and Social Welfare", "Th", "9:35AM - 12:20PM", "Burk Hall 251", "Jocelyn Hermoso", "Prerequisite: Restricted to upper division social work majors.\n" +
//                "Sex role stereotyping in the policies, practices, and organization of social welfare institutions; practice of social workers."));
//        courseList.add(new Course(8597, "PHIL", "110", "39", "Introduction to Critical Thinking I", "Tu, Th", "2:10PM - 3:25PM", "Thornton Hall 428", "Staff", "Skills involved in understanding, criticizing, and constructing arguments--and providing foundation for further work not only in philosophy but in other fields as well. \n" +
//                "(Note: Students enrolled at a CSU or CA Community College as of Fall 2016 must earn a C or better in this course to fulfill the General Education requirement.  Students admitted prior to Fall 2016 may earn a C-.)"));
//        courseList.add(new Course(9124, "ISYS", "464", "01", "Managing Enterprise Data", "M, W", "2:10PM - 3:25PM", "Business Building 108", "Robert Nickerson", "Prerequisites: ISYS 363 and ISYS 350 with grades of C- or better.\n" +
//                "Principles and use of database management systems in business. Database design and implementation. Database definition, manipulation and control using SQL. Classwork, 2 units; laboratory, 1 unit. Plus-minus letter grade only."));
//        courseList.add(new Course(3714, "ISYS", "565", "03", "Managing Enterprise Networks", "Tu", "7:00PM - 9:45PM", "Business Building 116", "Sameer Verma", "\t\n" +
//                "Prerequisite: ISYS 363 with grade of C- or better.\n" +
//                "Hardware and software for communications and their application to the distributed data processing environment. Terminal-to-host communication, local and wide area networks, transaction processing monitors. Classwork, 2 units; laboratory, 1 unit."));
//    }

//    private void setDemoDepartmentMap() {
//        HttpRequestorManager.initialize(this);
//        api_requestBuilder = new API_RequestBuilder();
//        this.departments = new DepartmentsModel();
//        api_requestBuilder.populateModel(null, this.departments, new Callback() {
//            @Override
//            public void response(Object object) {
//                departments  = (DepartmentsModel) object;
//            }
//
//            @Override
//            public void error(NULLOBJ nullObj) {
//
//            }
//        });
//    }

    private void setDepartmentMap() {
        String[] departments  = new String[] {
                "Asian American Studies", "AA%20S",
                "Accounting", "ACCT",
                "Apparel Design and Merchandising ", "ADM",
                "Africana Studies", "AFRS",
                "American Indian Studies", "AIS",
                "American Studies", "AMST",
                "Anthropology", "ANTH",
                "Arabic", "ARAB",
                "Art", "ART",
                "Astronomy", "ASTR",
                "Athletics", "ATHL",
                "All University", "A%20U",
                "Broadcast and Electronic Communication Arts", "BECA",
                "Biology", "BIOL",
                "Business", "BUS",
                "Child and Adolescent Development ", "CAD",
                "Communicative Disorders", "C%20D",
                "Consumer and Family Studies", "CFS",
                "Chemistry", "CHEM",
                "Chinese", "CHIN",
                "Cinema", "CINE",
                "Criminal Justice", "C%20J",
                "Classical Archeology", "CLAR",
                "Classics", "CLAS",
                "Clinical Laboratory Science", "CLS",
                "Communication Studies", "COMM",
                "Counseling", "COUN",
                "Computer Science", "CSC",
                "Critical Social Thought", "CST",
                "Creative Writing", "C%20W",
                "Comparative and World Literature", "CWL",
                "Design and Industry", "DAI",
                "Dance", "DANC",
                "Dietetics and Food Management", "DFM",
                "Decision Science", "DS",
                "Economics", "ECON",
                "Educational Administration", "EDAD",
                "Educational Leadership", "EDDL",
                "Education", "EDUC",
                "Elementary Education", "E%20ED",
                "Engineering", "ENGR",
                "Environmental Studies", "ENVS",
                "Earth Sciences", "ERTH",
                "Ethnic Studies", "ETHS",
                "Finance", "FIN",
                "Foreign Language", "F%20L",
                "French", "FR",
                "Geography", "GEOG",
                "German", "GER",
                "Global Peace Studies", "GPS",
                "Greek", "GRE",
                "Gerontology", "GRN",
                "Hebrew", "HEBR",
                "Health Education", "H%20ED",
                "Holistic Health", "HH",
                "History", "HIST",
                "Health and Social Sciences", "HSS",
                "Hospitality and Tourism Management", "HTM",
                "Humanities", "HUM",
                "International Business", "IBUS",
                "Interior Design", "ID",
                "International Relations", "I%20R",
                "Interdisciplinary Studies in Education", "ISED",
                "Information Systems", "ISYS",
                "Italian", "ITAL",
                "Instructional Technologies", "ITEC",
                "Japanese", "JAPN",
                "Journalism", "JOUR",
                "Jewish Studies", "JS",
                "Kinesiology", "KIN",
                "Labor Studies", "LABR",
                "Latin", "LATN",
                "Liberal & Creative Arts", "LCA",
                "Liberal Studies", "LS",
                "Latina/Latino Studies", "LTNS",
                "Mathematics", "MATH",
                "Middle East and Islamic Studies", "MEIS",
                "Management", "MGMT",
                "Modern Greek Studies", "MGS",
                "Marketing", "MKTG",
                "Museum Studies", "M%20S",
                "Marine Science", "MSCI",
                "Music", "MUS",
                "Nursing", "NURS",
                "Public Administration", "P%20A",
                "Philosophy", "PHIL",
                "Physics", "PHYS",
                "Political Science", "PLSI",
                "Persian", "PRSN",
                "Psychology", "PSY",
                "Physical Therapy", "PT",
                "Religious Studies", "RELS",
                "Recreation, Parks, and Tourism Administration", "RPT",
                "Race and Resistance Studies", "RRS",
                "Russian", "RUSS",
                "Science", "SCI",
                "Secondary Education", "S%20ED",
                "Sociology", "SOC",
                "Spanish", "SPAN",
                "Special Education", "SPED",
                "Social Work", "S%20W",
                "Sexuality Studies", "SXS",
                "Theatre Arts", "TH%20A",
                "Technical and Professional Writing", "TPW",
                "Urban Studies and Planning", "USP",
                "Women and Gender Studies", "WGS"
        };
        departmentMap = new HashMap<>();
        for (int i=0; i<departments.length; i+=2) {
            departmentMap.put(departments[i], departments[i+1]);
        }
        departmentList = new ArrayList<>(departmentMap.keySet());
        Collections.sort(departmentList);
    }

    @Override
    public void onCatalogItemClicked(int position) {
        selectedCourse = courseList.get(position);
        String numberString = "<b>" + selectedCourse.getDepartment() + " " + selectedCourse.getNumber() + "</b>";
        if (selectedCourse.getSection() != null && !selectedCourse.getSection().isEmpty()) {
            numberString += "-" + selectedCourse.getSection();
        }
        String titleString =  selectedCourse.getName();
        String dayString = "<b>Days: </b> " + selectedCourse.getMeetDays();
        String timeString = "<b>Time: </b>" + selectedCourse.getMeetTime();
        String instructorString = "<b>Instructor:</b> " + (selectedCourse.getInstructor()==null ? "" : selectedCourse.getInstructor());
        String descriptionString = "<b>Description:</b> " + (selectedCourse.getDescription()==null ? "" : selectedCourse.getDescription());
        detailNumberTV.setText(Html.fromHtml(numberString));
        detailNameTV.setText(Html.fromHtml(titleString));
        detailInstructorTV.setText(Html.fromHtml(instructorString));
        detailDayTV.setText(Html.fromHtml(dayString));
        detailTimeTV.setText(Html.fromHtml(timeString));
        detailDescriptionTV.setText(Html.fromHtml(descriptionString));
        if (isInMyCourseList (selectedCourse, myCourseList)) {
            detailAddEventB.setEnabled(false);
        } else {
            detailAddEventB.setEnabled(true);
        }
        courseDetailDialog.show();
    }

    private boolean isInMyCourseList (Course course, List<Course> myCourseList) {
        boolean isInList = true;
        for (Course listCourse : myCourseList) {
            if (stringEqual(course.getDepartment(), listCourse.getDepartment()) && stringEqual(course.getNumber(), listCourse.getNumber()) && stringEqual(course.getSection(), listCourse.getSection())) {
                return true;
            }
        }
        return false;
    }

    private boolean stringEqual (String a, String b) {
        return (a==null && b==null) || (a!=null && b!=null && a.equals(b));
    }
}
