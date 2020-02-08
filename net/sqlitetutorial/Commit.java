package net.sqlitetutorial;
import java.util.*;

public class Commit {

	String commitSHA;
    String  message;
    String  type;
    String preprocessed;
    
    ArrayList<Refactoring> refactorings;
    
    public Commit()
    {
    	this.commitSHA = new String();
    	this.message = new String();
    	this.type = new String();
    	this.preprocessed = new String();
    	this.refactorings = new ArrayList();
    }
    
	
}
