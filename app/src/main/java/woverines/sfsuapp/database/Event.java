package woverines.sfsuapp.database;

/**
 * Created by andre_000 on 4/14/2016.
 *
 * Placeholder class for an event,
 * even is an alert/alarm/due date that student can set for each class
 */
public class Event {

    private int id;
    private double time;
    private String name;

    public Event()
    {
        this.id = 0;
        this.name = "New Event";
        this.time = 00.00;

    }

    public Event(int id, double time, String name)
    {
        this.id = id;
        this.time = time;
        this.name = name;
    }

    public int getId(){return this.id;}
    public double getTime(){return this.time;}
    public String getName(){return this.name;}





}
