import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReviewParser {
    String agent =
            "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    String keyFile = "standard-boolean.txt";
    String devCorpus = "testWords-boolean.txt";

    Map<String, Integer> trainPos = new HashMap<>();
    Map<String, Integer> trainNeg = new HashMap<>();
    Set<String> stopWords = new HashSet<>();

    boolean booleanNaiveBayes;

    public ReviewParser(boolean filterStopWords, boolean booleanNaiveBayes)
            throws FileNotFoundException, IOException {
        if (filterStopWords) {
            try (BufferedReader br =
                    new BufferedReader(new FileReader("Niave_Bayes/data/stopwords.txt"));) {
                String line;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line);
                }
            }
            for (String s : this.stopWords) {
                System.out.println(s);
            }
        }
        this.booleanNaiveBayes = booleanNaiveBayes;
    }

    public List<String> loadUrls(String path) throws IOException {
        List<String> urls = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path));) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ShowRatings")) {
                    String resource =
                            line.substring(line.indexOf("ShowRatings"), line.indexOf("&"));
                    String domain = "https://www.ratemyprofessors.com/";
                    urls.add(domain + resource);
                }
            }
        }
        return urls;
    }

    public List<Review> parse(String url) throws IOException {
        List<Review> reviews = new LinkedList<>();

        Connection.Response resp = Jsoup.connect(url).userAgent(agent).method(Method.GET).execute();
        Document doc = resp.parse();

        Elements parsedReviews = doc.select("div.Rating__StyledRating-sc-1rhvpxz-0");
        for (Element e : parsedReviews) {
            Elements comment = e.select("div.Comments__StyledComments-dzzyvm-0");
            Elements rating = e.select("div.RatingValues__RatingValue-sc-6dc747-3");
            Elements helpful = e.select("div.RatingFooter__HelpTotal-ciwspm-2");

            // skip ineffective reviews
            if (comment.get(0).text().equalsIgnoreCase("")) {
                continue;
            }

            reviews.add(
                    new Review(comment.get(0).text(), rating.get(0).text(), helpful.get(0).text()));
        }

        return reviews;
    }

    public void putReviewsIntoTrainCorpus(List<Review> reviews)
            throws FileNotFoundException, IOException {

        for (Review review : reviews) {
            Set<String> booleanNaiveBayesVocabulary = new HashSet<>();
            String[] words = review.comment.split("[()/\\s]");

            if (review.cls == Cls.P) {
                for (String word : words) {
                    String token = word.replaceAll("[.,!?\"()]", "").toLowerCase();
                    // filter stopwords and perform boolean Naive Bayes vocabulary
                    if (token.equals("") || stopWords.contains(token)
                            || (booleanNaiveBayes && booleanNaiveBayesVocabulary.contains(token))) {
                        continue;
                    }
                    trainPos.merge(token, 1, (a, b) -> a + b);
                    booleanNaiveBayesVocabulary.add(token);
                }
            } else {
                for (String word : words) {
                    String token = word.replaceAll("[.,!?\"()]", "").toLowerCase();
                    // filter stopwords and perform boolean Naive Bayes vocabulary
                    if (token.equals("") || stopWords.contains(token)
                            || (booleanNaiveBayes && booleanNaiveBayesVocabulary.contains(token))) {
                        continue;
                    }
                    trainNeg.merge(token, 1, (a, b) -> a + b);
                    booleanNaiveBayesVocabulary.add(token);
                }
            }
        }
    }

    public void showBags() {
        for (Map.Entry<String, Integer> entry : trainPos.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue() + "\tP");
        }

        for (Map.Entry<String, Integer> entry : trainNeg.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue() + "\tN");
        }
    }

    public void writeTrainData(String path) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path));) {
            for (Map.Entry<String, Integer> entry : trainPos.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue() + "\tP");
            }
            for (Map.Entry<String, Integer> entry : trainNeg.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue() + "\tN");
            }
        }
    }

    public void clearDevDataFileContent(String devOutputPath) throws IOException {
        try (PrintWriter keyFileWriter = new PrintWriter(new FileWriter(devOutputPath + keyFile));
                PrintWriter testWordsWriter =
                        new PrintWriter(new FileWriter(devOutputPath + devCorpus));) {
            keyFileWriter.write("");
            testWordsWriter.write("");
        }
    }

    public void writeDevData(List<Review> reviews, String devOutputPath, int baseIndex) {
        try (PrintWriter keyFileWriter =
                new PrintWriter(new FileWriter(devOutputPath + keyFile, true));
                PrintWriter testWordsWriter =
                        new PrintWriter(new FileWriter(devOutputPath + devCorpus, true));) {

            for (Review review : reviews) {
                Set<String> booleanNaiveBayesVocabulary = new HashSet<>();

                // Write standard.txt, one review class per line
                keyFileWriter.println(baseIndex++ + "\t" + review.cls + "\t"
                        + review.comment.replaceAll("[\n]+", " "));

                // Write testWords.txt, one token per line
                String[] words = review.comment.split("[()/\\s]");

                for (String word : words) {
                    String token = word.replaceAll("[.,!?\"()]", "").toLowerCase();
                    // filter stopwords and perform boolean Naive Bayes vocabulary
                    if (token.equals("") || stopWords.contains(token)
                            || (booleanNaiveBayes && booleanNaiveBayesVocabulary.contains(token))) {
                        continue;
                    }
                    testWordsWriter.println(token);
                    booleanNaiveBayesVocabulary.add(token);
                }
                testWordsWriter.println("-----");
            }
        } catch (IOException ignore) {
        }
    }
}
