package woverines.sfsuapp.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import woverines.sfsuapp.R;
import woverines.sfsuapp.api.API_RequestBuilder;
import woverines.sfsuapp.api.Callback;
import woverines.sfsuapp.api.HttpRequestorManager;
import woverines.sfsuapp.database.Course;
import woverines.sfsuapp.models.CoursesModels;
import woverines.sfsuapp.models.DepartmentsModel;
import woverines.sfsuapp.models.NULLOBJ;

public class ClassCatalog extends AppCompatActivity implements ClassCatalogAdapter.CatalogClickListener {
//    private static Map<String, String> departmentMap;
    private List<String> departmentList;

    private List<Course> courseList;
    private ClassCatalogAdapter adapter;
    private RecyclerView courseListView;
    private TextView departmentInput;
    private TextView courseNumberInput;
    private Dialog courseDetailDialog;
    private TextView detailNumberTV;
    private TextView detailNameTV;
    private TextView detailInstructorTV;
    private TextView detailTimeTV;
    private TextView detailDescriptionTV;

    private API_RequestBuilder api_requestBuilder;
    private DepartmentsModel departments;
    private CoursesModels courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setDemoCourseList();
        setDemoDepartmentMap();
        courseList = new ArrayList<>();
        adapter = new ClassCatalogAdapter(this, courseList);
        courseListView = (RecyclerView) findViewById(R.id.catalog_course_list);
        courseListView.setLayoutManager(new LinearLayoutManager(this));
        courseListView.setAdapter(adapter);
        departmentInput = (AutoCompleteTextView) findViewById(R.id.department_input);

        courseDetailDialog = new Dialog(this);
        courseDetailDialog.setContentView(R.layout.catalog_details_dialog);
        courseDetailDialog.setTitle("Course Options");
        //connecting TextViews to dialog
        detailNumberTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_number);
        detailNameTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_name);
        detailInstructorTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_instructor);
        detailTimeTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_meet_time);
        detailDescriptionTV = (TextView) courseDetailDialog.findViewById(R.id.dialog_course_description);
        //connecting Buttons to dialog
        Button detailCancelB = (Button) courseDetailDialog.findViewById(R.id.dialog_cancel_button);
        detailCancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseDetailDialog.dismiss();
            }
        });
        Button detailAddEventB = (Button) courseDetailDialog.findViewById(R.id.dialog_add_class);
        detailAddEventB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add course to schedule
                Toast toast = Toast.makeText(getApplicationContext(), "*Class added (not functional)", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        final AutoCompleteTextView departmentInput = (AutoCompleteTextView) findViewById(R.id.department_input);
//        departmentList = new ArrayList<>(departmentMap.keySet());
        setDepartmentList();
//        Collections.sort(departmentList);
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<String>(this, R.layout.department_autocomplete_popup_item, departmentList);
        departmentInput.setAdapter(departmentAdapter);
        departmentInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    departmentInput.showDropDown();
                }
            }
        });

        HttpRequestorManager.initialize(this);
        api_requestBuilder = new API_RequestBuilder();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setCourseListFromCoursesModels(CoursesModels models, String department) {
        courseList.clear();
        for (CoursesModels.Course apiCourse : models.classes) {
            Course course = new Course(0, department, apiCourse.course_number+"", "", apiCourse.course_description, "", "", "", "", "");
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
        api_requestBuilder.populateModel(selectedDepartment, this.courses, new Callback() {
            @Override
            public void response(Object object) {
                courses  = (CoursesModels) object;
                setCourseListFromCoursesModels((CoursesModels) object, selectedDepartment);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void error(NULLOBJ nullObj) {

            }
        });
    }

    public void showCourseDetails(View view) {

    }

    private void setDemoCourseList() {
        courseList = new ArrayList<>();
        courseList.add(new Course(2365, "CSC", "668", "01", "Advanced Object Oriented Programming", "Tu, Th", "11:00AM - 12:10PM", "Thornton Hall 310", "Barry Levine", "Students will learn advanced object-oriented programming techniques through group-work"));
        courseList.add(new Course(2361, "CSC", "256", "03", "Machine Structure", "Th", "4:10PM - 6:55PM", "Science Building 101", "Tsun-Yuk Hsu", "Prerequisite: CSC 230 or CSC 330 with grade of C or better. Digital logic circuits; data representation; assembly language programming; subroutine linkage; machine language encoding; interrupt/exception handling; memory system concepts; CPU organization and performance."));
        courseList.add(new Course(2372, "CSC", "413", "02", "Software Development", "M, W, F", "12:10PM - 1:00PM", "Thornton Hall 329", "Marc Sosnick", "Prerequisites: CSC 340 and CSC 412 with grades of C or better. \n" +
                "Modern software applications. Object-oriented techniques: encapsulation, inheritance, and poly-morphism as mechanism for data design and problem solution. Software design, debugging, testing, and UI design. Software maintenance. Software development tools. Extra fee required. (Plus-minus letter grade only)"));
        courseList.add(new Course(2378, "CSC", "667", "01", "Internet Application Design and Development", "M", "7:00PM - 9:45PM", "Thornton Hall 210", "John Roberts", "Prerequisite: CSC 413 with grade of C or better or consent of instructor.\n" +
                "Fundamental technologies on which WWW is based. Extra fee required.\n" +
                "(CSC 667/CSC 867 is a paired course offering. Students who complete the course at one level may not repeat the course at the other level.)"));
        courseList.add(new Course(4789, "SW", "352", "01", "Gender, Sexism, and Social Welfare", "Th", "9:35AM - 12:20PM", "Burk Hall 251", "Jocelyn Hermoso", "Prerequisite: Restricted to upper division social work majors.\n" +
                "Sex role stereotyping in the policies, practices, and organization of social welfare institutions; practice of social workers."));
        courseList.add(new Course(8597, "PHIL", "110", "39", "Introduction to Critical Thinking I", "Tu, Th", "2:10PM - 3:25PM", "Thornton Hall 428", "Staff", "Skills involved in understanding, criticizing, and constructing arguments--and providing foundation for further work not only in philosophy but in other fields as well. \n" +
                "(Note: Students enrolled at a CSU or CA Community College as of Fall 2016 must earn a C or better in this course to fulfill the General Education requirement.  Students admitted prior to Fall 2016 may earn a C-.)"));
        courseList.add(new Course(9124, "ISYS", "464", "01", "Managing Enterprise Data", "M, W", "2:10PM - 3:25PM", "Business Building 108", "Robert Nickerson", "Prerequisites: ISYS 363 and ISYS 350 with grades of C- or better.\n" +
                "Principles and use of database management systems in business. Database design and implementation. Database definition, manipulation and control using SQL. Classwork, 2 units; laboratory, 1 unit. Plus-minus letter grade only."));
        courseList.add(new Course(3714, "ISYS", "565", "03", "Managing Enterprise Networks", "Tu", "7:00PM - 9:45PM", "Business Building 116", "Sameer Verma", "\t\n" +
                "Prerequisite: ISYS 363 with grade of C- or better.\n" +
                "Hardware and software for communications and their application to the distributed data processing environment. Terminal-to-host communication, local and wide area networks, transaction processing monitors. Classwork, 2 units; laboratory, 1 unit."));
    }

    private void setDemoDepartmentMap() {
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

    }

    private void setDepartmentList() {
        String[] departmentCodes  = new String[] {
                "AA S",
                "ACCT",
                "ADM",
                "AFRS",
                "AIS",
                "AMST",
                "ANTH",
                "ARAB",
                "ART",
                "ASTR",
                "ATHL",
                "A U",
                "BECA",
                "BIOL",
                "BUS",
                "CAD",
                "C D",
                "CFS",
                "CHEM",
                "CHIN",
                "CINE",
                "C J",
                "CLAR",
                "CLAS",
                "CLS",
                "COMM",
                "COUN",
                "CSC",
                "CST",
                "C W",
                "CWL",
                "DAI",
                "DANC",
                "DFM",
                "DS",
                "ECON",
                "EDAD",
                "EDDL",
                "EDUC",
                "E ED",
                "ENGR",
                "ENVS",
                "ERTH",
                "ETHS",
                "FIN",
                "F L",
                "FR",
                "GEOG",
                "GER",
                "GPS",
                "GRE",
                "GRN",
                "HEBR",
                "H ED",
                "HH",
                "HIST",
                "HSS",
                "HTM",
                "HUM",
                "IBUS",
                "ID",
                "I R",
                "ISED",
                "ISYS",
                "ITAL",
                "ITEC",
                "JAPN",
                "JOUR",
                "JS",
                "KIN",
                "LABR",
                "LATN",
                "LCA",
                "LS",
                "LTNS",
                "MATH",
                "MEIS",
                "MGMT",
                "MGS",
                "MKTG",
                "M S",
                "MSCI",
                "MUS",
                "NURS",
                "P A",
                "PHIL",
                "PHYS",
                "PLSI",
                "PRSN",
                "PSY",
                "PT",
                "RELS",
                "RPT",
                "RRS",
                "RUSS",
                "SCI",
                "S ED",
                "SOC",
                "SPAN",
                "SPED",
                "S W",
                "SXS",
                "TH A",
                "TPW",
                "USP",
                "WGS"
        };
        departmentList = Arrays.asList(departmentCodes);
    }

    @Override
    public void onCatalogItemClicked(int position) {
        Course course = courseList.get(position);
        detailNumberTV.setText(course.getDepartment() + " " + course.getNumber() + "-" + course.getSection());
        detailNameTV.setText(course.getName());
        detailInstructorTV.setText(course.getInstructor());
        detailTimeTV.setText(course.getMeetTime());
        detailDescriptionTV.setText(course.getDescription());
        courseDetailDialog.show();
    }
}
