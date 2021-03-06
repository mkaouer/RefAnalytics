package net.sqlitetutorial;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.Format;
import java.time.format.DateTimeFormatter;

// Function to remove duplicates from an ArrayList 



public class SQLiteJDBC {
	
	
	 public static void main( String args[] ) {

		 ArrayList<String> commitstemp = new ArrayList();
		 ArrayList<String> namestemp = new ArrayList();
		 Set<String> commits;
		 Set<String> projects;
		 ArrayList<Refactoring> refactorings = new ArrayList();
		 
		 ArrayList<String> refactoringTypes = new ArrayList();
		 ArrayList<Integer> refactoringTypeOccinTest = new ArrayList();
		 ArrayList<Integer> refactoringTypeOccinProd = new ArrayList();
		 ArrayList<Integer> refactoringTypeOccinBoth = new ArrayList();
		 
		 
		 ArrayList<String> commitTypes = new ArrayList();
		 ArrayList<Integer> commitTypeslabeled = new ArrayList();
		 
		 ArrayList<Pattern> patterns = new ArrayList();
		 
		 ArrayList<CommitNonRef> commitsNonRef = new ArrayList();
		 
		 Connection c = null;
		 Connection cNonRef = null;
		 Statement stmt = null;
		 Statement stmt2 = null;
		 try {
			   
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:C:/refdb/TOSEM.db");
		      cNonRef = DriverManager.getConnection("jdbc:sqlite:C:/refdb/nonref.db");
		      c.setAutoCommit(false);
		      cNonRef.setAutoCommit(false);
		      System.out.println("Refactoring database opened successfully");

		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM miner_refactoring;" );
		      stmt2 = cNonRef.createStatement();
		      ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM random_nonrefactoring_commit;" );
		      
		      ArrayList<String> commitsofref = new ArrayList();
		      ArrayList<CommitRef> CR = new ArrayList();
     
		      while ( rs.next() ) {
		    	 
		    	  Refactoring r = new Refactoring();
		    	  CommitRef temprefcommit = new CommitRef();
		    	  
		    	 commitstemp.add(rs.getString("CommitId"));
		    	 r.CommitId = rs.getString("CommitId");
		         r.RefactoringType = rs.getString("RefactoringType");
		         r.FilePath = rs.getString("FilePath");
		         r.Class = rs.getString("Class");
		         r.RefactoringDetail = rs.getString("RefactoringDetail");
		         r.Message = rs.getString("Message");
		         r.Name = rs.getString("Name");
		         namestemp.add(rs.getString("Name"));
		         refactorings.add(r);
		         
		         //if (!commitsofref.contains(r.CommitId)) {
		        //	 commitsofref.add(r.CommitId);
		        //	 temprefcommit.CommitSHA = new String(r.CommitId);
		        //	 temprefcommit.Message = new String(r.Message);
		        //	 temprefcommit.Name = new String(r.Name);
		        //	 CR.add(temprefcommit);
		    	//  }
		      }
		      
		      
		      ArrayList<String> nonrefproj = new ArrayList(); 
		      
		      while ( rs2.next() ) {
		    	  
		    	  CommitNonRef temp = new CommitNonRef();
		    	  
		    			  
		    	  temp.CommitSHA = new String(rs2.getString("CommitSHA"));
		    	  temp.Name = new String(rs2.getString("Name"));
		    	  temp.Message = new String(rs2.getString("Message"));
		    	  
		    	  //if (!nonrefproj.contains(temp.Name)) {
		    		  nonrefproj.add(temp.Name);
		    	  //}
		    	  
		    	  commitsNonRef.add(temp);
		    	  
		      }
		      
		      System.out.println("Number of non ref projects: "+nonrefproj.size());
		      
		      HashSet nonrefprojectshash = new HashSet<String>(nonrefproj);
		      
		      nonrefproj = new ArrayList(nonrefprojectshash);
		      
		      // extractRefactoringTypes(refactoringTypes, refactoringTypeOccinTest, refactoringTypeOccinProd, refactoringTypeOccinBoth, commitTypes, commitTypeslabeled, c, stmt);
		      // Calculating coverages
		      // calculateCoverage(refactorings, refactoringTypes);  
		      
		      // removing duplicates
		      commits = new HashSet<String>(commitstemp);
		      projects = new HashSet<String>(namestemp);
		      
		      // Calculating test vs production commits
		      /*
		      System.out.print("Filtering commits... ");
		      for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
		    	  if(!commits.contains(refactorings.get(counter2).CommitId)) {
		    		  commits.add(refactorings.get(counter2).CommitId);
		    	  }
		    	  
		      }
		      System.out.println("Done!");
		      */
		      
		      
		      
		      List<String> commitIds = new ArrayList<String>(commits);
		      
		      // Creating an iterator 
		      //Iterator commitIterator = commitIds.iterator();
		      // creating indicators
		      ArrayList<Boolean> refactorTestFiles = new ArrayList();
		      ArrayList<Boolean> refactorProdFiles = new ArrayList();
		     
		      // ---- setting indicators to false
		      for (int counter = 0; counter < commitIds.size(); counter++) {
		    	  refactorTestFiles.add(false);
		    	  refactorProdFiles.add(false);
		      }
		      
		  
		      
		      
		      
		      printStats(commits, refactorings, refactorTestFiles, refactorProdFiles);
		      
		      // detectProductionVsTestCommits(refactorings, commitIds, refactorTestFiles, refactorProdFiles);
		      
			  // celculateInstancesPerType(refactorings, refactoringTypes, refactoringTypeOccinTest, refactoringTypeOccinProd, refactoringTypeOccinBoth, commitTypes, commitTypeslabeled);
		      
		      // printInstancesPerType(refactoringTypes, refactoringTypeOccinTest, refactoringTypeOccinProd, refactoringTypeOccinBoth);	  
		      
		      readPatternsCSV(patterns);
		      printPatterns(patterns);
		      
		      // preparing stuff for patterns
		      
		      Vector projectss = new Vector(projects);
		      Vector patternOccurrence = new Vector();
		      Vector patternOccurrenceNonRef = new Vector();
		      
		      Vector commitsCountedRef = new Vector();
		      Vector commitsCountedNonRef = new Vector();
		      
		      System.out.println("Number of refactoring projects: "+projects.size());
		      
		      for (int counter = 0; counter < projectss.size(); counter++) {
		    	  patternOccurrence.add(counter,0);
		    	  patternOccurrenceNonRef.add(counter,0);
		      }
		      
		      Map<String, Integer> aMap = new HashMap<String, Integer>();
		      
		      System.out.println("aMap done!");
		      for (int counter = 0; counter < projectss.size(); counter++) {
		    	  
		    	  aMap.put(projectss.get(counter).toString(),0);
		      }
		      
		      
		      
		      for (int counter = 0; counter < patterns.size(); counter++) {
		    	  
		    	  System.out.println("For refactoring:");
		    	  for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
		    		  
		    		  
		    		  if(refactorings.get(counter2).Message.toLowerCase().contains(patterns.get(counter).pattern))
		    		  {
		    			  System.out.println("Adding to aMap "+aMap.get(refactorings.get(counter2).Name.toLowerCase()));
		    			  aMap.replace(refactorings.get(counter2).Name.toLowerCase(),aMap.get(refactorings.get(counter2).Name.toLowerCase())+1); 
		    			  
		    			  int indexProject = projectss.indexOf(refactorings.get(counter2).Name);
		    			  
		    			  patternOccurrence.set(indexProject, ((int)patternOccurrence.get(indexProject))+1);
		    			  
		    			  patterns.get(counter).occurrenceInRefCommits.add(refactorings.get(counter2).Name);
		    			  
		    			  System.out.println(projectss.indexOf(refactorings.get(counter2).Name+" contains "+patternOccurrence.get(indexProject))+" and we are adding this commit "+CR.get(counter2).CommitSHA);
		    		  }
		    	  }
		    	  	System.out.println("For non refactoring:");
		    	  	
		    	  	for (int counter2 = 0; counter2 < commitsNonRef.size(); counter2++) {
		    		  
		    		  if(commitsNonRef.get(counter2).Message.toLowerCase().contains(patterns.get(counter).pattern))
		    		  {
		    			  
		    			  int indexProject = projectss.indexOf(commitsNonRef.get(counter2).Name);
		    			  
		    			  patternOccurrenceNonRef.set(indexProject, ((int)patternOccurrenceNonRef.get(indexProject))+1);
		    			  
		    			  patterns.get(counter).occurrenceInNonCommits.add(commitsNonRef.get(counter2).Name);
		    			  
		    			 // System.out.println(projectss.indexOf(commitsNonRef.get(counter2).Name+" contains "+patternOccurrenceNonRef.get(indexProject))+" and we are adding this commit "+commitsNonRef.get(counter2).CommitSHA);
			    		  
		    		  }
		    	  }
		    	  	
		    	  for (String name: aMap.keySet()){
		                String key = name.toString();
		                String value = aMap.get(name).toString();  
		                System.out.println(key + " " + value);  
		    	  }	
		    	  
		    	  patterns.get(counter).occurrenceInRefProjects = new ArrayList(patternOccurrence);
		    	  patterns.get(counter).occurrenceInNonProjects = new ArrayList(patternOccurrenceNonRef);
		      }
		      
		      for (int counter = 0; counter < patterns.size(); counter++) 
		      {
		    	  int occref = 0;
		    	  int occnon = 0;
		    	  
		    	  for (int counter2 = 0; counter2 < projects.size(); counter2++) 
		    	  {
		    		  occref = (int) Collections.frequency(patterns.get(counter).occurrenceInRefCommits, projectss.get(counter2));
		    		  occnon = (int) Collections.frequency(patterns.get(counter).occurrenceInNonCommits, projectss.get(counter2));
		    	  }
		    	  patterns.get(counter).occurrenceInRefProjects2.add(occref);
		    	  patterns.get(counter).occurrenceInNonProjects2.add(occnon);
		    	  // System.out.println("For pattern "+patterns.get(counter).pattern+" ther number of ref projects "+patterns.get(counter).occurrenceInRefProjects2.get(patterns.get(counter).occurrenceInRefProjects2.size()-1)+"number of non projects "+patterns.get(counter).occurrenceInNonProjects2.get(patterns.get(counter).occurrenceInNonProjects2.size()-1));
		      }
		      
		      	  
		      
		      // Test
		      printPatternsOcc(patterns,projectss,commitsCountedRef,commitsCountedNonRef);
		      
		      rs.close();
			  stmt.close();
			  rs2.close();
			  stmt2.close();
			  c.close();
			  cNonRef.close();
		      
		   } catch ( Exception e ) 
		    {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		   System.out.println("Operation done successfully");
		  }

	private static void calculateCoverage(ArrayList<Refactoring> refactorings, ArrayList<String> refactoringTypes) {
		for (int counter = 0; counter < refactoringTypes.size(); counter++) {
			  
			  int match = 0;
			  int all = 0;
			  int refactMentions = 0;
			  
		      System.out.println("Calculating coverage of "+refactoringTypes.get(counter)); 
		      
		      for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
		    	  
		    	  if (refactorings.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter)))
		    		  all++;
		    	  if ( (refactorings.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter))) 
		    			  && (refactorings.get(counter2).Message.contains(refactoringTypes.get(counter)))
		    			  )
		    	  {
		    		  match++;
		    	  }
		    	  
		    	  if ( (refactorings.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter))) 
		    			  && (refactorings.get(counter2).Message.contains("refact"))
		    			  )
		    	  {
		    		  refactMentions++;
		    	  }
		      }
		      
		      System.out.println("All "+all);
		      System.out.println("Match "+match+" Ratio "+(float)match/all);
		      System.out.println("Refactoring mentions "+refactMentions+" Ratio "+(float)refactMentions/all);
		      // System.out.println(refactoringTypes.get(counter)+" = "+(float)match/all);
		      // System.out.println(refactoringTypes.get(counter)+" = "+(float)refactMentions/all);
		  }
	}

	private static void extractRefactoringTypes(ArrayList<String> refactoringTypes,
			ArrayList<Integer> refactoringTypeOccinTest, ArrayList<Integer> refactoringTypeOccinProd,
			ArrayList<Integer> refactoringTypeOccinBoth, ArrayList<String> commitTypes,
			ArrayList<Integer> commitTypeslabeled, Connection c, Statement stmt) throws SQLException {
		ResultSet rs;
		rs = stmt.executeQuery( "SELECT * FROM commit_type;" );
		  
		  while ( rs.next() ) {
		    	 
			 commitTypes.add(rs.getString("CommitId"));
			 commitTypeslabeled.add(rs.getInt("RefType"));
		  }
		  
		  rs = stmt.executeQuery( "select distinct RefactoringType from miner_refactoring;" );
		  
		  while ( rs.next() ) {
		    	 
			  refactoringTypes.add(rs.getString("RefactoringType"));
			  refactoringTypeOccinTest.add(0);
			  refactoringTypeOccinProd.add(0);
			  refactoringTypeOccinBoth.add(0);
			  
			  
		  }
		  
		  
		  rs.close();
		  stmt.close();
		  c.close();
	}

	private static void printStats(Set<String> commits, ArrayList<Refactoring> refactorings,
			ArrayList<Boolean> refactorTestFiles, ArrayList<Boolean> refactorProdFiles) {
		System.out.println("*** Some statistics ***");
		  System.out.println("The number of commits: "+commits.size());
		  System.out.println("The number of test booleans: "+refactorTestFiles.size());
		  System.out.println("The number of prod booleans: "+refactorProdFiles.size());
		  System.out.println("The number of refactorings: "+refactorings.size());
	}

	private static void detectProductionVsTestCommits(ArrayList<Refactoring> refactorings, List<String> commitIds,
			ArrayList<Boolean> refactorTestFiles, ArrayList<Boolean> refactorProdFiles) throws IOException {
		// detection of test vs. production commits
		System.out.print("Detecting testing only commits... "); 
		for (int counter = 0; counter < commitIds.size(); counter++) {
		    
			// testing with only 1000 instances
			//for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
			for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
				
		    	  if(commitIds.get(counter).equals(refactorings.get(counter2).CommitId)) {
		    		  
		    		  if(refactorings.get(counter2).FilePath.contains("test") || refactorings.get(counter2).FilePath.contains("Test")
		    		     || refactorings.get(counter2).Class.contains("test") || refactorings.get(counter2).Class.contains("Test")	  
		    				  ) {
		    		  
		    			  refactorTestFiles.set(counter, true);
		    		  }
		    		  else {
		    			  refactorProdFiles.set(counter, true);
		    		  }
		    	  }
		    	  
		      }
			
		} 
		System.out.println("Done");
     
		int testOnly = 0;
		int prodOnly = 0;
		int both = 0;
		
		DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		//System.out.println(timeStampPattern.format(java.time.LocalDateTime.now()));
		
		System.out.print("Creating output files... ");
		FileWriter csvWriterTest = new FileWriter("output/testCommits"+timeStampPattern.format(java.time.LocalDateTime.now())+".csv");
		FileWriter csvWriterProd = new FileWriter("output/prodCommits"+timeStampPattern.format(java.time.LocalDateTime.now())+".csv");
		FileWriter csvWriterBoth = new FileWriter("output/bothCommits"+timeStampPattern.format(java.time.LocalDateTime.now())+".csv");

		
		for (int counter = 0; counter < commitIds.size(); counter++) 
		{
			
			if(refactorTestFiles.get(counter).equals(true))
			{
				if(refactorProdFiles.get(counter).equals(false)) 
				{
					testOnly++;
					csvWriterTest.append(commitIds.get(counter));
					csvWriterTest.append("\n");
				}
				else
				{
					both++;
					csvWriterBoth.append(commitIds.get(counter));
					csvWriterBoth.append("\n");
				}
			}
			else 
			{
				if(refactorProdFiles.get(counter).equals(true)) 
				{
					prodOnly++;
					csvWriterProd.append(commitIds.get(counter));
					csvWriterProd.append("\n");
				}
			}
		}
		
		System.out.println("*** Some more statistics ***");
		System.out.println("The number of commits refactoring tests: "+testOnly);
		System.out.println("The number of commits refactoring prods: "+prodOnly);
		System.out.println("The number of commits refactoring both: "+both);
		
		csvWriterTest.flush();
		csvWriterTest.close();
		csvWriterProd.flush();
		csvWriterProd.close();
		csvWriterBoth.flush();
		csvWriterBoth.close();
		System.out.println("Done");
	}

	private static void printPatternsOcc(ArrayList<Pattern> patterns,Vector projectss, Vector commitsCountedRef, Vector commitsCountedNonRef) throws IOException {
		DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		  System.out.print("Creating patterns output files... ");
		  FileWriter csvWriter,csvWriter2,csvWriterRefCommits,csvWriterNonCommits;
		  
		  String time = new String(timeStampPattern.format(java.time.LocalDateTime.now()));
		  File file = new File("output/patterns/"+time+"/");
	      //Creating the directory
	      boolean bool = file.mkdir();
		  
		  for (int counter0 = 0; counter0 < patterns.size(); counter0++) {
			  
			  csvWriter = new FileWriter("output/patterns/"+time+"/"+patterns.get(counter0).pattern+"-"+time+".csv");
			  csvWriter2 = new FileWriter("output/patterns/"+time+"/0 "+patterns.get(counter0).pattern+"-"+time+".csv");
			  csvWriterRefCommits = new FileWriter("output/patterns/"+time+"/"+patterns.get(counter0).pattern+"- RefCommits - "+time+".csv");
			  csvWriterNonCommits = new FileWriter("output/patterns/"+time+"/"+patterns.get(counter0).pattern+"- NonCommits - "+time+".csv");
			  
			  csvWriter.append(patterns.get(counter0).pattern);
			  csvWriter.append("\n");

			  for (int counter = 0; counter < patterns.get(counter0).occurrenceInRefProjects.size(); counter++) 
			  {
				  
				  csvWriter.append(projectss.get(counter).toString());
				  csvWriter.append(",");
				  csvWriter.append(patterns.get(counter0).occurrenceInRefProjects.get(counter).toString());
				  csvWriter.append(",");
				  csvWriter.append(patterns.get(counter0).occurrenceInNonProjects.get(counter).toString());
				  csvWriter.append("\n");
			  }
			  
			  // updated count
			  
			  csvWriter2.append(patterns.get(counter0).pattern);
			  csvWriter2.append("\n");

			  for (int counter = 0; counter < patterns.get(counter0).occurrenceInRefProjects2.size(); counter++) 
			  {
				  
				  csvWriter2.append(projectss.get(counter).toString());
				  csvWriter2.append(",");
				  csvWriter2.append(patterns.get(counter0).occurrenceInRefProjects2.get(counter).toString());
				  csvWriter2.append(",");
				  csvWriter2.append(patterns.get(counter0).occurrenceInNonProjects2.get(counter).toString());
				  csvWriter2.append("\n");
			  }
			  
			  // extra verification
			  
			  csvWriterRefCommits.append(patterns.get(counter0).pattern);
			  csvWriterNonCommits.append(patterns.get(counter0).pattern);
			  csvWriterRefCommits.append("\n");
			  csvWriterNonCommits.append("\n");

			  for (int counter = 0; counter < patterns.get(counter0).occurrenceInRefCommits.size(); counter++) 
			  {
				  
				  csvWriterRefCommits.append(patterns.get(counter0).occurrenceInRefCommits.get(counter).toString());
				  csvWriterRefCommits.append("\n");
			  }
			  
			  for (int counter = 0; counter < patterns.get(counter0).occurrenceInNonCommits.size(); counter++) 
			  {
				  
				  csvWriterNonCommits.append(patterns.get(counter0).occurrenceInNonCommits.get(counter).toString());
				  csvWriterNonCommits.append("\n");
			  }
			  
			  
			  csvWriterRefCommits.flush();
			  csvWriterRefCommits.close();
			  csvWriterNonCommits.flush();
			  csvWriterNonCommits.close();
			  csvWriter.flush();
			  csvWriter.close();
			  csvWriter2.flush();
			  csvWriter2.close();
		  }
		  System.out.println("Done");
	}
	
	private static void printInstancesPerType(ArrayList<String> refactoringTypes,
			ArrayList<Integer> refactoringTypeOccinTest, ArrayList<Integer> refactoringTypeOccinProd,
			ArrayList<Integer> refactoringTypeOccinBoth) throws IOException {
		DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		  System.out.print("Creating output files... ");
		  FileWriter csvWriter = new FileWriter("output/refactoringTypes"+timeStampPattern.format(java.time.LocalDateTime.now())+".csv");
		  csvWriter.append("RefactoringType");
		  csvWriter.append(",");
		  csvWriter.append("Test");
		  csvWriter.append(",");
		  csvWriter.append("Prod");
		  csvWriter.append(",");
		  csvWriter.append("Both");
		  csvWriter.append("\n");  
		  
		  for (int counter = 0; counter < refactoringTypes.size(); counter++) 
		  {
			  csvWriter.append(refactoringTypes.get(counter));
			  csvWriter.append(",");
		      csvWriter.append(refactoringTypeOccinTest.get(counter).toString());
		      csvWriter.append(",");
		      csvWriter.append(refactoringTypeOccinProd.get(counter).toString());
		      csvWriter.append(",");
		      csvWriter.append(refactoringTypeOccinBoth.get(counter).toString());
		      csvWriter.append("\n");
			  System.out.println(refactoringTypes.get(counter));
			  System.out.println(refactoringTypeOccinTest.get(counter)+ " "+refactoringTypeOccinProd.get(counter)+" "+ refactoringTypeOccinBoth.get(counter));
		  }
		  csvWriter.flush();
		  csvWriter.close();
	}

	private static void celculateInstancesPerType(ArrayList<Refactoring> refactorings,
			ArrayList<String> refactoringTypes, ArrayList<Integer> refactoringTypeOccinTest,
			ArrayList<Integer> refactoringTypeOccinProd, ArrayList<Integer> refactoringTypeOccinBoth,
			ArrayList<String> commitTypes, ArrayList<Integer> commitTypeslabeled) {
		
		System.out.print("Calculating instances per type... ");  
		  for (int counter = 0; counter < refactorings.size(); counter++) {
  
			int index = commitTypes.indexOf(refactorings.get(counter).CommitId);
			
			if (commitTypeslabeled.get(index)==0) {
					  
				int index2 = refactoringTypes.indexOf(refactorings.get(counter).RefactoringType);
		    	  refactoringTypeOccinTest.set(index2, refactoringTypeOccinTest.get(index2)+1);
			  }
			else if (commitTypeslabeled.get(index)==1) {
					  
		    		int index2 = refactoringTypes.indexOf(refactorings.get(counter).RefactoringType);
			    	  refactoringTypeOccinProd.set(index2, refactoringTypeOccinProd.get(index2)+1);
		    	  }
			else if (commitTypeslabeled.get(index)==2) {
				  
				int index2 = refactoringTypes.indexOf(refactorings.get(counter).RefactoringType);
		    	  refactoringTypeOccinBoth.set(index2, refactoringTypeOccinBoth.get(index2)+1);
			  }
			 	  
			  
		  }
		  System.out.println("Done");
	}
	
	
	public static void printPatterns(ArrayList<Pattern> patterns) {
		System.out.println("List of Patterns:");
		for (int counter = 0; counter < patterns.size(); counter++) {
			System.out.println(patterns.get(counter).pattern);
		}
		
	}
	public static void readPatternsCSV(ArrayList<Pattern> patterns) {

		System.out.print("Reading Patterns... ");
        String csvFile = "C:/refdb/patterns.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\n";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
            	Pattern temp = new Pattern( " " +line);
            	patterns.add(temp);

                

            }
            System.out.println("Done");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
	
}