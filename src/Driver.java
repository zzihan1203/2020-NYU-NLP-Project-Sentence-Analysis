import java.io.IOException;
import java.util.List;

public class Driver {

    public static void main(String[] args) throws IOException {
        String inputPath = "doc/NYU";
        String outputPath = "data/bag.txt";

        ReviewParser p = new ReviewParser();
        List<String> urls = p.loadUrls(inputPath);

        int progress = 1;
        int all = urls.size();
        for (String url : urls) {
            System.out.println("Processing " + progress++ + " of " + all + " urls.");
            List<Review> reviews = p.parse(url);
            p.putReviewsIntoBagOfWords(reviews);
            p.writeBags(outputPath);
        }

    }
}
