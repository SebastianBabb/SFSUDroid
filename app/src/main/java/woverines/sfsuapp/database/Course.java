package woverines.sfsuapp.database;

//for testing
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;


/**
 * Created by andre_000 on 4/12/2016.
 */
public class Course {

    private int    id;
    private String number;
    private String name;
    private String instructor;
    private String meetTime;
    private String description;


    //for testing
    private RandomString ranString;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public Course()
    {
        this.id = 0;
        this.number = "000";
        this.name = "noname";
        this.instructor = "none";
        this.meetTime = "00:00";
        this.description = "blahblah";
    }

    public Course(int id, String number, String name, String instructor, String meetTime, String description)
    {
        this.id = id;
        this.number = number;
        this.name = name;
        this.instructor = instructor;
        this.meetTime = meetTime;
        this.description = description;
    }


    //for testing to gen random classes
    public void genRandomCourse()
    {
        this.number = UUID.randomUUID().toString();
        this.name = UUID.randomUUID().toString();
        this.instructor = UUID.randomUUID().toString();
        this.meetTime = UUID.randomUUID().toString();

    }

    public void genRandomCourse2()
    {
        this.number = randomString(3);
        this.name = randomString(15);
        this.instructor = randomString(10);
        this.meetTime = randomString(4);
    }

    public static class RandomString {

        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }


    //GETTERS
    public int    getId() {return this.id;}
    public String getNumber() {return this.number;}
    public String getName() {return this.name;}
    public String getInstructor() {return this.instructor;}
    public String getMeetTime() {return this.meetTime;}
    public String getDescription() {return this.description;}

}
