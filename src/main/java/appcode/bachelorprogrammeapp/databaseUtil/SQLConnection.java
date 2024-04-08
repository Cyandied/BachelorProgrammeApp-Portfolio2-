package appcode.bachelorprogrammeapp.databaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLConnection {

    private Connection c;

    public SQLConnection(){
        c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.sqlite");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void createTable(String tableName, List<String> fields) throws SQLException {
        if(!tableName.contains("bachelorProgramme")){
            c.createStatement().executeUpdate("drop table if exists "+tableName+";");
        }
        String start = "create table if not exists "+tableName+"(id integer primary key AUTOINCREMENT,";
        String rows = fields.stream().map(String::valueOf).collect(Collectors.joining(","));
        c.createStatement().executeUpdate(start+rows+");");
    }

    public int appendToTable(String tableName,List<String> fields,List<Object> dataSet) throws SQLException {
        String start = "insert into "+tableName+" "+fields.stream().map(String::valueOf).collect(Collectors.joining(",","(",")"))+" values ";
        String values = dataSet.stream().map(data -> "'"+data+"'").collect(Collectors.joining(",","(",")"));
        c.createStatement().executeUpdate(start+values+";");
        return findLatestPrimKey(tableName);
    }

    public int findLatestPrimKey(String tableName) throws SQLException {
        ResultSet res = c.createStatement().executeQuery("SELECT * FROM "+tableName+" ORDER BY id DESC LIMIT 1;");
        return res.getInt("id");

    }

    public int findPrimKeyFromColum(String tableName,String colum, Object target) throws SQLException {
        Statement stm = c.createStatement();
        if(target instanceof String){
            target = "'"+target+"'";
        }
        return stm.executeQuery("Select id from "+tableName+" where "+colum+" == "+target+";").getInt(1);
    }

    public List<String> getprojectList(String table,String target) throws SQLException {
        Statement stm = c.createStatement();
        int targetId = findPrimKeyFromColum(table, "name", target);
        ResultSet res = null;
        if (table.equals("programs")) {
            res = stm.executeQuery("SELECT * from projects inner join programProjects on projects.id = programProjects.project AND programProjects.program = " + targetId + ";");
        } else {
            res = stm.executeQuery("SELECT * from projects inner join subModProjects on projects.id = subModProjects.project AND subModProjects.subMod = " + targetId + ";");
        }
        ArrayList<String> courses = new ArrayList<String>();
        while (res.next()) {
            String name = res.getString("name");
            courses.add(name);
        }
        return courses;
    }

    public List<String> getCourseList(String table,String target) throws SQLException {
        Statement stm = c.createStatement();
        int targetId = findPrimKeyFromColum(table,"name",target);
        ResultSet res = null;
        if(table.equals("programs")){
            res = stm.executeQuery("SELECT * from courses inner join programCourses on courses.id = programCourses.course AND programCourses.program = "+targetId+";");
        }
        else {
            res = stm.executeQuery("SELECT * from courses inner join subModCourses on courses.id = subModCourses.course AND subModCourses.subMod = "+targetId+";");
        }
        ArrayList<String> courses = new ArrayList<String>();
        while (res.next()){
            String name = res.getString("name");
            courses.add(name);
        }
        return courses;
    }

    public int getCourseweight(String courseName) throws SQLException {
        Statement stm = c.createStatement();
        return stm.executeQuery("SELECT ECTs FROM courses WHERE name = '"+courseName+"' LIMIT 1;").getInt(1);
    }

    public void removeTargetRow(String table, String col,Object target) throws SQLException {
        Statement stm = c.createStatement();
        if(target instanceof String){
            target = "'"+target+"'";
        }
        stm.executeUpdate("DELETE FROM "+table+" WHERE "+col+" = "+target+";");
    }

    public void removeCourseBachProgramme(int studentNr, String courseName) throws SQLException {
        int courseId = findPrimKeyFromColum("courses","name",courseName);
        int bachProgId = findPrimKeyFromColum("bachelorProgrammes","studentNr",studentNr);
        Statement stm = c.createStatement();
        stm.executeUpdate("DELETE FROM bachelorProgrammeCourses WHERE course = "+courseId+" AND bachelorProgramme = "+bachProgId+";");
    }

    public List<String> getAllProgramOrSubMod(String table) throws SQLException {
        Statement stm = c.createStatement();
        ResultSet res = stm.executeQuery("SELECT * FROM "+table+";");
        ArrayList<String> resultList = new ArrayList<String>();
        while (res.next()){
            String name = res.getString("name");
            resultList.add(name);
        }
        return resultList;
    }

    public boolean exsists(String table, String col, Object target) throws SQLException {
        Statement stm = c.createStatement();
        if(target instanceof String){
            target = "'"+target+"'";
        }
        int exists = stm.executeQuery("SELECT EXISTS(SELECT 1 FROM "+table+" WHERE "+col+" = "+target+");").getInt(1);
        return exists == 1;
    }

    public int sumBachProgramECTs (int studentNr) throws SQLException {
        Statement stm = c.createStatement();
        int bachelorProgrammeId = stm.executeQuery("Select id from bachelorProgrammes where studentNr = '"+studentNr+"';").getInt(1);

        ResultSet resCourse = stm.executeQuery("SELECT SUM(ECTs) as total from courses inner join bachelorProgrammeCourses on courses.id = bachelorProgrammeCourses.course AND bachelorProgrammeCourses.bachelorProgramme = "+bachelorProgrammeId+";");
        ResultSet resProject = stm.executeQuery("SELECT SUM(ECTs) as total from projects inner join bachelorProgrammeProjects on projects.id = bachelorProgrammeProjects.project AND bachelorProgrammeProjects.bachelorProgramme = "+bachelorProgrammeId+";");

        return resCourse.getInt("total")+resProject.getInt("total");
    }

}
