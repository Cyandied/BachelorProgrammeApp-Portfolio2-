package appcode.bachelorprogrammeapp;

import appcode.bachelorprogrammeapp.databaseUtil.SQLConnection;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Model{

  SQLConnection con;
  public Model(){
    con = new SQLConnection();

    try {
      con.createTable("courses", Arrays.asList("name text","ECTs integer"));
      con.createTable("projects",Arrays.asList("name text","ECTs integer"));
      con.createTable("subMods",Arrays.asList("name text"));
      con.createTable("programs",Arrays.asList("name text"));
      con.createTable("subModCourses",Arrays.asList("course integer","subMod integer","foreign key (course) references courses(id)","foreign key (subMod) references subMods(id)"));
      con.createTable("programCourses",Arrays.asList("course integer","program integer","foreign key (course) references courses(id)","foreign key (program) references programs(id)"));
      con.createTable("programProjects", Arrays.asList("project integer","program integer","foreign key (project) references projects(id)","foreign key (program) references programs(id)"));
      con.createTable("subModProjects", Arrays.asList("project integer","subMod integer","foreign key (project) references projects(id)","foreign key (subMod) references subMods(id)"));
      con.createTable("bachelorProgrammes",Arrays.asList("studentNr integer"));
      con.createTable("bachelorProgrammeCourses",Arrays.asList("course integer","bachelorProgramme integer","foreign key (course) references courses(id)","foreign key (bachelorProgramme) references bachelorProgrammes(id)"));
      con.createTable("bachelorProgrammeProjects",Arrays.asList("project integer","bachelorProgramme integer","foreign key (project) references projects(id)","foreign key (bachelorProgramme) references bachelorProgrammes(id)"));
      //Add all courses, and course connections

      for(String program : baseProgram()) {
        List<String> courseList = baseCourse(program);
        int programId = con.appendToTable("programs",Arrays.asList("name"),Arrays.asList(program));
        List<String> projectList = baseProject(program);
        for(String project : projectList){
          int projectId = con.appendToTable("projects",Arrays.asList("name","ECTs"),Arrays.asList(project,15));
          con.appendToTable("programProjects",Arrays.asList("project","program"),Arrays.asList(projectId,programId));
        }
        if (courseList == null){
          continue;
        }
        for (String course : courseList) {
          int id = con.appendToTable("courses", Arrays.asList("name","ECTs"),Arrays.asList(course,courseWeight(course)));
          con.appendToTable("programCourses",Arrays.asList("course","program"),Arrays.asList(id,programId));
        }
      }
      for(String subMod : subjectModule()) {
        List<String> courseList = subjectCourse(subMod);
        int subModId = con.appendToTable("subMods",Arrays.asList("name"),Arrays.asList(subMod));

        int projectId = con.appendToTable("projects",Arrays.asList("name","ECTs"),Arrays.asList(subjectProject(subMod),15));
        con.appendToTable("subModProjects",Arrays.asList("project","subMod"),Arrays.asList(projectId,subModId));

        if(courseList == null){
          continue;
        }
        for (String course : courseList) {
          int id = con.appendToTable("courses",Arrays.asList("name","ECTs"), Arrays.asList(course,courseWeight(course)));
          con.appendToTable("subModCourses",Arrays.asList("course","subMod"),Arrays.asList(id,subModId));
        }
      }

    }
    catch (SQLException e){
      System.out.println(e);
    }
  }
  List<String> baseProgram(){return Arrays.asList("NatBach","HumTek");}
  List<String> subjectModule(){return Arrays.asList("Computer Science","Informatik","Astrology","Physics","Pain and torture");}
  List<String> baseCourse(String base){
    if(base.equals("NatBach")) {
      return Arrays.asList(
          "BK1 Empirical Data",
          "BK2 Experimental Methods",
          "BK3 Theory of Natural Science",
          "Logic and Discrete Mathematics",
          "Functional Biology – Zoology",
          "Linear Algebra",
          "Organic Chemistry",
          "Biological Chemistry",
          "Statistical Models",
          "Functional Programming and Language Implementations",
          "Classical Mechanics",
          "Environmental Science",
          "Cell Biology",
          "Functional biology – Botany",
          "Supplementary Physics",
          "Calculus",
          "The Chemical Reaction",
          "Scientific Computing",
          "Energy and Climate Changes"
          );
      }
      if(base.equals("HumTek")){
        return Arrays.asList(
          "BK1",
          "BK2",
          "BK3",
          "Design og Konstruktion I+Workshop" ,
          "Subjektivitet, Teknologi og Samfund I" ,
          "Teknologiske systemer og artefakter I" ,
          "Videnskabsteori" ,
          "Design og Konstruktion II+Workshop" ,
          "Subjektivitet, Teknologi og Samfund II" ,
          "Bæredygtige teknologier" ,
          "Kunstig intelligens" ,
          "Medier og teknologi - datavisualisering" ,
          "Teknologiske Systemer og Artefakter II - Sundhedsteknologi" ,
          "Den (in)humane storby" ,
          "Interactive Design in the Field" ,
          "Organisation og ledelse af designprocesser"
      );
    }
    return null;
  }
  List<String> baseProject(String base) {
    return Arrays.asList("BP1 " + base, "BP2 " + base, "BP3 " + base, "Bachelorproject " + base);
  }
  List<String> subjectCourse(String base) {
    if (base.equals("Computer Science")) {
      return Arrays.asList("Essential Computing",
          "Software Development","Interactive Digital Systems" );
    }
    if (base.equals("Informatik")) {
      return Arrays.asList("Organisatorisk forandring og IT",
          "BANDIT","Interactive Digital Systems" );
    }
    if (base.equals("Astrology")) {
      return Arrays.asList("Essential Astrology",
          "Venus studies","Mars studies","Ascendant calculations" );
    }
    if (base.equals("Physics")) {
        return Arrays.asList("Thermodynamics","Electrodynamics","Quantum mechanics");
    }
    if(base.equals("Pain and torture")){
      return Arrays.asList("Long complex SQLite queries (that dont work)","Just math proofs","Semi functional ESP32s");
    }
    return null;
  }
  String subjectProject(String subject) {
    return "Subject module project in "+subject;
  }
  int courseWeight(String course){
    if(course.equals("Software Development"))return 10;
    if(course.equals("BANDIT"))return 10;
    if(course.equals("Quantum mechanics")) return 10;
    return 5;
  }
  boolean isProject(String s){
    for(String fm:subjectModule())if(s.equals(subjectProject(fm))) return true;
    for(String bs:baseProgram())if(baseProject(bs).contains(s))return true;
    return false;
  }

  public void appendCourseToBachProgram(int studentNr,String courseName){
    try{
      List<Object> values = Arrays.asList(con.findPrimKeyFromColum("courses","name",courseName),con.findPrimKeyFromColum("bachelorProgrammes","studentNr",studentNr));
      con.appendToTable("bachelorProgrammeCourses",Arrays.asList("course","bachelorProgramme"),values);
    }catch (SQLException e){
      System.err.println(e.getMessage());
    }
  }

  public void appendProjectToBachProgram(int studentNr,String projectName){
    try{
      List<Object> values = Arrays.asList(con.findPrimKeyFromColum("projects","name",projectName),con.findPrimKeyFromColum("bachelorProgrammes","studentNr",studentNr));
      con.appendToTable("bachelorProgrammeProjects",Arrays.asList("project","bachelorProgramme"),values);
    }catch (SQLException e){
      System.err.println(e.getMessage());
    }
  }

  public void removeFromBachProgramme(int studentNr, String courseName){
    try {
      con.removeCourseBachProgramme(studentNr,courseName);
    }catch (SQLException e){
      System.err.println(e.getMessage());
    }
  }

  public int getBachProgeECTs(int studentNr){
    try {
      return con.sumBachProgramECTs(studentNr);
    }catch (SQLException e){
      System.err.println(e.getMessage());
    }
    return 0;
  }

  public List<String> getCourses(String type, String name){
    try {
      if(type.equals("subMod")){
        return con.getCourseList("subMods",name);
      }
      if(type.equals("program")){
        return con.getCourseList("programs",name);
      }
    }
    catch (SQLException e){
      System.err.println(e.getMessage());
    }
    return null;
  }

  public List<String> getProjects(String type, String name){
    try {
      if(type.equals("subMod")){
        return con.getprojectList("subMods",name);
      }
      if(type.equals("program")){
        return con.getprojectList("programs",name);
      }
    }
    catch (SQLException e){
      System.err.println(e.getMessage());
    }
    return null;
  }

  public void submitBachelorProgram(int studentNr){
    try {
      if(con.exsists("bachelorProgrammes","studentNr",studentNr)){
        con.removeTargetRow("bachelorProgrammes","studentNr",studentNr);
      }
      List<Object> values = Arrays.asList(studentNr);
      con.appendToTable("bachelorProgrammes",Arrays.asList("studentNr"),values);
    }
    catch (SQLException e){
      System.err.println(e.getMessage());
    }
  }

  public List<String> getOptions(String type){
    try {
      if(type.equals("subMod")){
        return con.getAllProgramOrSubMod("subMods");
      }
      if(type.equals("program")){
        return con.getAllProgramOrSubMod("programs");
      }
    }
    catch (SQLException e){
      System.err.println(e.getMessage());
    }
    return null;
  }

  public int getCourseweight(String courseName){
    try {
      return con.getCourseweight(courseName);
    }
    catch (SQLException e){
      System.err.println(e.getMessage());
    }
    return 0;
  }

}
