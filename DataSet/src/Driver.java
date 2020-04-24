import java.io.IOException;
import java.util.List;

public class Driver {

    public static void main(String[] args) throws IOException {
        String inputPath = "DataSet/doc/NYU";

        // simple bow
        // String trainOutputPath = "Niave_Bayes/data/bag.txt";

        // stop-words filtered
        String trainOutputPath = "Niave_Bayes/data/bag-filtered.txt";

        String devOutputPath = "Niave_Bayes/data/";

        // ReviewParser p = new ReviewParser();
        ReviewParser p = new ReviewParser(true);

        List<String> urls = p.loadUrls(inputPath);
        p.clearDevDataFileContent(devOutputPath);

        int progress = 1;
        int reviewCnt = 0;
        int devCnt = 0;
        int all = urls.size();
        for (String url : urls) {
            System.out.println("Processing " + progress + " of " + all + " urls.");
            List<Review> reviews = p.parse(url);
            reviewCnt += reviews.size();

            if (progress % 14 == 0) {
                p.writeDevData(reviews, devOutputPath, devCnt);
                devCnt += reviews.size();
            } else {
                p.putReviewsIntoTrainCorpus(reviews);
                p.writeTrainData(trainOutputPath);
            }
            progress++;
        }
        System.out.println("Process completed. " + reviewCnt + " reviews were parsed.");
    }
}
