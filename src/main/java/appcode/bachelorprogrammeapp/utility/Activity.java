package appcode.bachelorprogrammeapp.utility;

public abstract class Activity {
    String name;
    int ECTs;

    public Activity(String name, int ECTs){
        this.name = name;
        this.ECTs = ECTs;
    }

    public String getName(){
        return name;
    }

    public int getECTs(){
        return ECTs;
    }
}

