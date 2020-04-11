import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReviewParser {
    String agent =
            "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    Map<String, Integer> positive = new HashMap<>();
    Map<String, Integer> negative = new HashMap<>();

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

            reviews.add(
                    new Review(comment.get(0).text(), rating.get(0).text(), helpful.get(0).text()));
        }

        return reviews;
    }

    public void putReviewsIntoBagOfWords(List<Review> reviews) {
        for (Review review : reviews) {
            String[] words = review.comment.split("[()/\\s]");

            if (review.cls == Cls.POSITIVE) {
                for (String word : words) {
                    if (word.matches("[.,!?\"]+")) {
                        continue;
                    }
                    positive.merge(word.toLowerCase().replaceAll("[.,!?\"()/]", ""), 1,
                            (a, b) -> a + b);
                }
            } else {
                for (String word : words) {
                    if (word.matches("[.,!?\"]+")) {
                        continue;
                    }
                    negative.merge(word.toLowerCase().replaceAll("[.,!?\"()/]", ""), 1,
                            (a, b) -> a + b);
                }
            }
        }
    }

    public void showBags() {

        for (Map.Entry<String, Integer> entry : positive.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue() + "\tP");
        }

        for (Map.Entry<String, Integer> entry : negative.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue() + "\tN");
        }
    }

    public void writeBags(String path) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path));) {
            for (Map.Entry<String, Integer> entry : positive.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue() + "\tP");
            }
            for (Map.Entry<String, Integer> entry : negative.entrySet()) {
                writer.println(entry.getKey() + "\t" + entry.getValue() + "\tN");
            }

        }
    }
}
