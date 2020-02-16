package net.sqlitetutorial;

import java.util.ArrayList;

public class Pattern {

	String pattern;
    ArrayList<Integer> occurrenceInProjects;
    
    public Pattern()
    {
    	this.pattern = new String();
    	this.occurrenceInProjects = new ArrayList();
    }

	public Pattern(String line) {
		// FiXED
		this.pattern = new String(line);
    	this.occurrenceInProjects = new ArrayList();
	}
	
    
    
}
