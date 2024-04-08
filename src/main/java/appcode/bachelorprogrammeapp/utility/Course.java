package appcode.bachelorprogrammeapp.utility;

public class Course extends Activity {

    public Course(String name, int ECTs) {
        super(name, ECTs);
    }

    public String getBlock() {
        return name;
    }
}
