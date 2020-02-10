package net.sqlitetutorial;
import java.util.*;
import java.io.FileWriter;
import java.sql.*;
import java.text.Format;
import java.time.format.DateTimeFormatter;

// Function to remove duplicates from an ArrayList 



public class SQLiteJDBC {
	
	
	 public static void main( String args[] ) {

		 ArrayList<String> commitstemp = new ArrayList();
		 Set<String> commits;
		 ArrayList<Refactoring> refactorings = new ArrayList();
		 ArrayList<String> refactoringTypes = new ArrayList();
		 refactoringTypes.add("renam");
		 refactoringTypes.add("extract");
		 refactoringTypes.add("mov");
		 refactoringTypes.add("pull");
		 refactoringTypes.add("push");
		 
		 
		 Connection c = null;
		 Statement stmt = null;
		   try {
			   
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:C:/refdb/TOSEM.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");

		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM miner_refactoring;" );
		      
     
		      while ( rs.next() ) {
		    	 
		    	  Refactoring r = new Refactoring();
		    	  
		    	 commitstemp.add(rs.getString("CommitId"));
		    	 r.CommitId = rs.getString("CommitId");
		         r.RefactoringType = rs.getString("RefactoringType");
		         r.FilePath = rs.getString("FilePath");
		         r.Class = rs.getString("Class");
		         r.RefactoringDetail = rs.getString("RefactoringDetail");
		         r.Message = rs.getString("Message");
		         
		         refactorings.add(r);
		      }
		      
		      rs.close();
		      stmt.close();
		      c.close();
		      
		      // Calculating coverages
		      
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
		      
		      // removing duplicates
		      commits= new HashSet<String>(commitstemp);
		      
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
		      Iterator commitIterator = commitIds.iterator();
		      // creating indicators
		      ArrayList<Boolean> refactorTestFiles = new ArrayList();
		      ArrayList<Boolean> refactorProdFiles = new ArrayList();
		     
		      // ---- setting indicators to false
		      for (int counter = 0; counter < commitIds.size(); counter++) {
		    	  refactorTestFiles.add(false);
		    	  refactorProdFiles.add(false);
		      }
		      
		      System.out.println("*** Some statistics ***");
		      System.out.println("The number of commits: "+commits.size());
		      System.out.println("The number of test booleans: "+refactorTestFiles.size());
		      System.out.println("The number of prod booleans: "+refactorProdFiles.size());
		      System.out.println("The number of refactorings: "+refactorings.size());
		      
		        // detection of test vs. production commits
		        System.out.print("Detecting testing only commits... "); 
		        for (int counter = 0; counter < commitIds.size(); counter++) {
		            
		        	// testing with only 1000 instances
		        	//for (int counter2 = 0; counter2 < refactorings.size(); counter2++) {
		        	for (int counter2 = 0; counter2 < 1000; counter2++) {
		        		
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
			    
		        
		   } catch ( Exception e ) 
		    {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		   System.out.println("Operation done successfully");
		  }
}