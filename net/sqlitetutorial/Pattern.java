package net.sqlitetutorial;

import java.util.ArrayList;

public class Pattern {

	String pattern;
    ArrayList<Integer> occurrenceInRefProjects;
    ArrayList<Integer> occurrenceInNonProjects;
    
    ArrayList<String> occurrenceInRefCommits;
    ArrayList<String> occurrenceInNonCommits;
    
    ArrayList<Integer> occurrenceInRefProjects2;
    ArrayList<Integer> occurrenceInNonProjects2;
    
    public Pattern()
    {
    	this.pattern = new String();
    	this.occurrenceInRefProjects = new ArrayList();
    	this.occurrenceInNonProjects = new ArrayList();
    	this.occurrenceInRefCommits = new ArrayList();
    	this.occurrenceInNonCommits = new ArrayList();
    	this.occurrenceInRefProjects2 = new ArrayList();
    	this.occurrenceInNonProjects2 = new ArrayList();
    }

	public Pattern(String line) {
		// FiXED
		this.pattern = new String(line);
    	this.occurrenceInRefProjects = new ArrayList();
    	this.occurrenceInNonProjects = new ArrayList();
    	this.occurrenceInRefCommits = new ArrayList();
    	this.occurrenceInNonCommits = new ArrayList();
    	this.occurrenceInRefProjects2 = new ArrayList();
    	this.occurrenceInNonProjects2 = new ArrayList();
	}
	
    
    
}
