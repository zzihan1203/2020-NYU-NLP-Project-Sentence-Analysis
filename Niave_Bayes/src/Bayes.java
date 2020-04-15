import java.io.*;
import java.util.*;

public class Bayes {
  private  static Map<String, Integer> PosDic = new HashMap<>();
  private  static Map<String, Integer> NegDic = new HashMap<>();
  private static HashSet<String> dict = new HashSet<>();
  private static long countN = 0;
  private static long countP = 0;


  public static void main(String[] args) throws Exception{
    if (args.length != 2) {
      System.err.println("This needs two arguments: bag.txt testWords.txt");
      System.exit(1);
    }
    trainModel(args[0]);  // word - P/N - totalCount
    Map<Integer, String> results = classifyModel(args[1]);  // sentences
    String outputFilePath = "results.txt";
    writeIntoFile(results, outputFilePath);
  }

  private static void writeIntoFile(Map<Integer, String> results, String path) {
    if(results.size() > 0){
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
        for (Map.Entry<Integer, String> e : results.entrySet()) {
          String info = e.getKey() + "\t" + e.getValue()+ "\n";
          writer.write(info);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void trainModel(String path) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      Boolean endReading = false;
      while(!endReading){
        String line = reader.readLine();

        if(line == null|| line.equals("")){   // end of one sentence
//          line = reader.readLine();
//          if(line == null || line == ""){   // still empty: end reading
//            endReading = true;
//          }
          endReading = true;
        }
        if(endReading) break;

        String[] keyFields = line.split("\t");

        if(keyFields.length != 3) throw new IllegalArgumentException("Training Data format is not valid. and the length is: "+keyFields.length);
        String word = keyFields[0];
        int times = Integer.parseInt(keyFields[1]);
        String tag = keyFields[2];

        if(!dict.contains(word)) dict.add(word);

        if(tag.equals("P")){
          //if(PosDic.containsKey(word)) throw new IllegalArgumentException("word is duplicated: "+line+ " and word is:"+word);
          if(PosDic.containsKey(word)) PosDic.put(word, PosDic.get(word)+times);
          else PosDic.put(word, times);
          countP++;
        }
        else if(tag.equals("N")){
          //if(NegDic.containsKey(word)) throw new IllegalArgumentException("word is duplicated: "+word);
          if(NegDic.containsKey(word)) NegDic.put(word, NegDic.get(word)+times);
          else NegDic.put(word, times);
          countN++;
        }
        else throw new IllegalArgumentException("Class "+tag +"is not valid for word:"+word);
      }
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  private static Map<Integer, String> classifyModel(String path) throws IOException {
    int sentenceIndex = 0;
    Map<Integer, String> tagResults = new HashMap<>();
    List<String> words = new ArrayList<>();
    int wordIndex = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      Boolean endReading = false;
      while (!endReading) {
        String line = reader.readLine();
        //if (line.equals(null) || line.equals("")) { // end of one sentence
        if(line.length() == 5 && line.charAt(0) == '-' && line.charAt(1) == '-' && line.charAt(2) == '-' && line.charAt(3) == '-' && line.charAt(4) == '-') {
          String res = getTagOfSentence(words);
//          System.out.println("end of last sentence from this word: "+words.get(words.size()-1));
          tagResults.put(sentenceIndex, res);
//          System.out.println("write sentence with index: "+sentenceIndex);
          
          // Read NextLine
          words.clear();
          line = reader.readLine();
          wordIndex++;
//          System.out.println("hit splitter on line: "+wordIndex+" and now sentence# is: "+sentenceIndex);
          sentenceIndex++;
          
          if (line == null || line == "") { // still empty: end reading
            endReading = true;
          }
        }
        if (endReading) {
//          System.out.println("total sentence number is: "+ sentenceIndex);
          break;
        }

        // add words into word
        words.add(line);
        wordIndex++;
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return tagResults;
  }

  private static String getTagOfSentence(List<String> words){
    String res = "N";
    Double PProb = 1.0, NProb = 1.0;
    int V = dict.size();
    for(int i = 0; i < words.size(); i++){  // Debug:
      String w = words.get(i);
      if(!dict.contains(w)) {
//        System.out.println("skip the word: "+w);
        continue; // skip the unknown word
      }
//      else System.out.println(w);
      int CN = 0, CP = 0;
      if(PosDic.containsKey(w)) CP = PosDic.get(w);
      if(NegDic.containsKey(w)) CN = NegDic.get(w);
      double curPProb = (CP+1.0)/(countP+V);
      double curNProb = (CN+1.0)/(countN+V);
      PProb *= curPProb;
      NProb *= curNProb;
//      System.out.println("word: "+w+" CP: "+CP+" CN: "+CN+" curPProb: "+curPProb+" curNProb: "+curNProb + "PProb: "+PProb+" NProb: "+NProb); // Debug
    }
    if(PProb >= NProb) return "P";
    return res;
  }
}
