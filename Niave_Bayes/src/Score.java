import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Score {
  public static void main(String[] args) throws Exception{
    if (args.length != 2) {
      System.err.println("This needs two arguments: results.txt standard.txt");
      System.exit(1);
    }
    String responseFile = args[0];
    String keyFile = args[1];
    int correct = 0;
    int incorrect = 0;
    int TP = 0, TN = 0, FP = 0, FN = 0;
    try (BufferedReader r1 = new BufferedReader(new FileReader(responseFile))) {
    	try (BufferedReader r2 = new BufferedReader(new FileReader(keyFile))) {
    		while(true) {
    			String responseLine = r1.readLine();
        		String keyLine = r2.readLine();
        		if (responseLine == null || responseLine == "" || keyLine == null || keyLine == "") break;
        	    if (keyLine.equals("")) {
        	      if (responseLine.equals("")) {
        	        continue;
        	      } else {
        	        System.err.println ("sentence break expected at line");
        	        System.exit(1);
        	      }
        	    }
        	    String[] keyFields = keyLine.split("\t");
        	    if (keyFields.length != 3) {
        	      System.err.println ("format error in key at line :" + keyLine);
        	      System.exit(1);
        	    }
        	    String keyToken = keyFields[0];
        	    String keyPos = keyFields[1];
        	    String[] responseFields = responseLine.split("\t");
        	    if (responseFields.length != 2) {
        	      System.err.println ("format error in response at line :" + responseLine);
        	      System.exit(1);
        	    }
        	    String responseToken = responseFields[0];
        	    String responsePos = responseFields[1];
        	    if (!responseToken.equals(keyToken)) {
        	      System.err.println ("token mismatch at line ");
        	      System.exit(1);
        	    }
        	    if(responsePos.equals("T")) continue;
        	    else {
        	    	if (responsePos.equals(keyPos)) {
                      if(responsePos.equals("P")) TP ++;
                      else TN++;
              	      correct = correct + 1;
              	    } else {
                      if(responsePos.equals("P")) FP++;
                      else FN++;
              	      incorrect = incorrect + 1;
//              	      System.out.println(responseToken+" "+keyToken +": "+keyFields[2] + " "+responsePos + " is wrong "+keyPos);
              	    }
        	    }
        	    
    		}    		
        }catch(Exception e){
            e.printStackTrace();
        }
    }catch(Exception e){
        e.printStackTrace();
    }
    System.out.println (correct + " out of " + (correct + incorrect) + " sentences is correctly classified.");
    float accuracy = (float) 100.0 * correct / (correct + incorrect);
    System.out.printf ("accuracy: %8.2f\n", accuracy);

    float precisionP = (float) 100.0 * TP / (TP + FN);
    System.out.printf ("precisionT: %8.2f\n", precisionP);
    
    float precisionN = (float) 100.0 * TN / (TN + FP);
    System.out.printf ("precisionF: %8.2f\n", precisionN);

    float recallP = (float) 100.0 * TP / (TP + FP);
    System.out.printf ("recallT: %8.2f\n", recallP);
    
    float recallN = (float) 100.0 * TN / (TN + FN);
    System.out.printf ("recallF: %8.2f\n", recallN);

  }
}