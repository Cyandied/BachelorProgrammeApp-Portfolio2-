package appcode.bachelorprogrammeapp.databaseUtil;

import java.sql.ResultSet;

public class SQLResult {

    ResultSet res;

    public SQLResult(ResultSet res) {
        this.res = res;
    }

    public String get_string(String colum_name) {
        String result = "";
        try {
            result = res.getString(colum_name);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public int get_int(String colum_name) {
        int result = 0;
        try {
            result = res.getInt(colum_name);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

}
