package appcode.bachelorprogrammeapp;

import java.util.List;

public class Controller {

    static Model model;

    public Controller(){
        model = new Model();
    }

    public List<String> getProgrammes(){
        return model.getOptions("program");
    }

    public List<String> getSubMods(){
        return model.getOptions("subMod");
    }

    public int getCoursesWeight(List<String> courseNames){
        int total = 0;
        for(String courseName : courseNames){
            total += model.getCourseweight(courseName);
        }
        return total;
    }
    public List<String> getProgramProjects(String program){
        return model.getProjects("program",program);
    }
    public List<String> getSubmodProjects(String subMod){
        return model.getProjects("subMod",subMod);
    }
    public List<String> getSubModCourses(String subMod){
        return model.getCourses("subMod",subMod);
    }
    public List<String> getProgramCourses(String programme){
        return model.getCourses("program",programme);
    }
    public void createBachProgram(int studentNr){
        model.submitBachelorProgram(studentNr);
    }

    public void putCourseInBachProgram(int studentNr,String courseName){
        model.appendCourseToBachProgram(studentNr,courseName);
    }
    public void putProjectInBachProgram(int studentNr,String projectName){
        model.appendProjectToBachProgram(studentNr,projectName);
    }
    public void removeCourseInBachProgram(int studentNr,String courseName){
        model.removeFromBachProgramme(studentNr,courseName);
    }
    public int getTotalBachProgramECTs(int studentNr){
        return model.getBachProgeECTs(studentNr);
    }


}
