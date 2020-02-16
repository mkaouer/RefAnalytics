package net.sqlitetutorial;

import java.util.ArrayList;

public class Pattern {

	String pattern;
    ArrayList<Integer> occurrenceInRefProjects;
    ArrayList<Integer> occurrenceInNonProjects;
    
    public Pattern()
    {
    	this.pattern = new String();
    	this.occurrenceInRefProjects = new ArrayList();
    	this.occurrenceInNonProjects = new ArrayList();
    }

	public Pattern(String line) {
		// FiXED
		this.pattern = new String(line);
    	this.occurrenceInRefProjects = new ArrayList();
    	this.occurrenceInNonProjects = new ArrayList();
	}
	
    
    
}
