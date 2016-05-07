package woverines.sfsuapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import woverines.sfsuapp.R;
import woverines.sfsuapp.activity.StaffDirectoryAdapter.StaffDirectoryListener;
import woverines.sfsuapp.database.Staff;

public class StaffDirectory extends AppCompatActivity implements OnItemSelectedListener,
        StaffDirectoryListener {

    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> departments = new ArrayList<>();
    private Map<String, String> departmentMap = new HashMap<>();

    private StaffDirectoryAdapter adapter;
    private List<Staff> directory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_staff_directory);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setDepartmentMap();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.directory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new StaffDirectoryAdapter(directory, this));

        demo();
    }

    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        String department = departments.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView parent) {

    }

    @Override
    public void onClick(int position) {
        Staff staff = directory.get(position);
    }

    private void setDepartmentMap() {
        String[] array = new String[]{
            "Asian American Studies", "AA S",
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
            "All University", "A U",
            "Broadcast and Electronic Communication Arts", "BECA",
            "Biology", "BIOL",
            "Business", "BUS",
            "Child and Adolescent Development ", "CAD",
            "Communicative Disorders", "C D",
            "Consumer and Family Studies", "CFS",
            "Chemistry", "CHEM",
            "Chinese", "CHIN",
            "Cinema", "CINE",
            "Criminal Justice", "C J",
            "Classical Archeology", "CLAR",
            "Classics", "CLAS",
            "Clinical Laboratory Science", "CLS",
            "Communication Studies", "COMM",
            "Counseling", "COUN",
            "Computer Science", "CSC",
            "Critical Social Thought", "CST",
            "Creative Writing", "C W",
            "Comparative and World Literature", "CWL",
            "Design and Industry", "DAI",
            "Dance", "DANC",
            "Dietetics and Food Management", "DFM",
            "Decision Science", "DS",
            "Economics", "ECON",
            "Educational Administration", "EDAD",
            "Educational Leadership", "EDDL",
            "Education", "EDUC",
            "Elementary Education", "E ED",
            "Engineering", "ENGR",
            "Environmental Studies", "ENVS",
            "Earth Sciences", "ERTH",
            "Ethnic Studies", "ETHS",
            "Finance", "FIN",
            "Foreign Language", "F L",
            "French", "FR",
            "Geography", "GEOG",
            "German", "GER",
            "Global Peace Studies", "GPS",
            "Greek", "GRE",
            "Gerontology", "GRN",
            "Hebrew", "HEBR",
            "Health Education", "H ED",
            "Holistic Health", "HH",
            "History", "HIST",
            "Health and Social Sciences", "HSS",
            "Hospitality and Tourism Management", "HTM",
            "Humanities", "HUM",
            "International Business", "IBUS",
            "Interior Design", "ID",
            "International Relations", "I R",
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
            "Museum Studies", "M S",
            "Marine Science", "MSCI",
            "Music", "MUS",
            "Nursing", "NURS",
            "Public Administration", "P A",
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
            "Secondary Education", "S ED",
            "Sociology", "SOC",
            "Spanish", "SPAN",
            "Special Education", "SPED",
            "Social Work", "S W",
            "Sexuality Studies", "SXS",
            "Theatre Arts", "TH A",
            "Technical and Professional Writing", "TPW",
            "Urban Studies and Planning", "USP",
            "Women and Gender Studies", "WGS"
        };

        for (int i = 0; i < array.length; i += 2) {
            departmentMap.put(array[i], array[i + 1]);
        }

        departments = new ArrayList<>(departmentMap.keySet());
        Collections.sort(departments);
    }

    public void demo() {
        for (int i = 0; i < 20; i++) {
            int random = (int) (Math.random() * 10000);
            directory.add(new Staff("Name " + random, "(555) 555-" + random, "name" + random + "@sfsu.edu"));
        }

        adapter.notifyDataSetChanged();
    }
}
