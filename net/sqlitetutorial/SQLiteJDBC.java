package net.sqlitetutorial;
import java.util.*;
import java.sql.*;

// Function to remove duplicates from an ArrayList 



public class SQLiteJDBC {
	
	
	 public static void main( String args[] ) {

		 ArrayList<String> commits = new ArrayList();
		 ArrayList<Refactoring> temp = new ArrayList();
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
		      c = DriverManager.getConnection("jdbc:sqlite:E:/Data/eclipse-workspace/AnalysisRefDB/db/TOSEM.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");

		      stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery( "SELECT * FROM miner_refactoring;" );
		      
     
		      while ( rs.next() ) {
		    	 
		    	  Refactoring r = new Refactoring();
		    	  
		         r.CommitId = rs.getString("CommitId");
		         r.RefactoringType = rs.getString("RefactoringType");
		         r.FilePath = rs.getString("FilePath");
		         r.Class = rs.getString("Class");
		         r.RefactoringDetail = rs.getString("RefactoringDetail");
		         r.Message = rs.getString("Message");
		         
		         temp.add(r);
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
		          
		          for (int counter2 = 0; counter2 < temp.size(); counter2++) {
		        	  
		        	  if (temp.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter)))
		        		  all++;
		        	  if ( (temp.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter))) 
		        			  && (temp.get(counter2).Message.contains(refactoringTypes.get(counter)))
		        			  )
		        	  {
		        		  match++;
		        	  }
		        	  
		        	  if ( (temp.get(counter2).RefactoringDetail.contains(refactoringTypes.get(counter))) 
		        			  && (temp.get(counter2).Message.contains("refact"))
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
		      
		      // Calculating test vs production commits
		      
		      for (int counter2 = 0; counter2 < temp.size(); counter2++) {
		    	  commits.add(temp.get(counter2).CommitId);
		      }
		      
		      
		      
		      
		   } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		   }
		   System.out.println("Operation done successfully");
		  }
}