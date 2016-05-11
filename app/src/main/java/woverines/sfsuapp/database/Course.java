package woverines.sfsuapp.database;

/**
 * Created by andre_000 on 4/12/2016.
 */
public class Course {

    private int    id;
    private String number;
    private String section;
    private String department;
    private String name;
    private String instructor;
    private String meetTime;
    private String meetDays;
    private String meetRoom;
    private String description;



    public Course()
    {
        this.id = 0;
        this.number = "000";
        this.name = "noname";
        this.instructor = "none";
        this.meetTime = "00:00";
        this.description = "blahblah";
    }
//    public Course(int id, String number, String name, String instructor, String meetTime, String description)
//    {
//        this.id = id;
//        this.number = number;
//        this.name = name;
//        this.instructor = instructor;
//        this.meetTime = meetTime;
//        this.description = description;
//    }

    public Course(int id, String department, String number, String section, String name, String meetDays, String meetTime, String meetRoom, String instructor, String description)
    {
        this.id = id;
        this.department = department;
        this.number = number;
        this.section = section;
        this.name = name;
        this.meetDays = meetDays;
        this.meetTime = meetTime;
        this.meetRoom = meetRoom;
        this.instructor = instructor;
        this.description = description;
    }


    //GETTERS
    public int    getId() {return this.id;}
    public String getNumber() {return this.number;}

    public String getSection() {
        return section;
    }

    public String getName() {return this.name;}
    public String getInstructor() {return this.instructor;}
    public String getMeetTime() {return this.meetTime;}
    public String getDescription() {return this.description;}

    public String getDepartment() {
        return this.department;
    }

    public String getMeetDays() {
        return this.meetDays;
    }

    public String getMeetRoom() {
        return this.meetRoom;
    }

//SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setMeetTime(String meetTime) {
        this.meetTime = meetTime;
    }

    public void setMeetDays(String meetDays) {
        this.meetDays = meetDays;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMeetRoom(String meetRoom) {
        this.meetRoom = meetRoom;
    }
}
