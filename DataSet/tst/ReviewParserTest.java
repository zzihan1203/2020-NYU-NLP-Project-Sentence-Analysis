import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class ReviewParserTest {
    String url = "https://www.ratemyprofessors.com/ShowRatings.jsp?tid=577620";
    ReviewParser p = new ReviewParser();
    List<Review> reviews;

    @Test
    public void testParseUrl_getCorrectReviewDetails() throws IOException {
        // Given
        String expectComment =
                "Only took this class because I heard that if you told him youre in love with him that hell give you an A, he also passed out one time when he came to class out of breath, showed up in roller skates too. Heard he got caught having sex in the paleontology section of the library too";
        Double expectRating = 1.0;
        Integer expecthelpful = 0;

        reviews = p.parse(url);
        Review firstReview = reviews.get(0);

        // When
        String actualComment = firstReview.comment;
        Double actualRating = firstReview.rating;
        Integer actualhelpful = firstReview.helpful;

        // Then
        assertEquals(expectComment, actualComment);
        assertEquals(expectRating, actualRating);
        assertEquals(expecthelpful, actualhelpful);
        assertEquals(Cls.N, firstReview.cls);
    }

    @Test
    public void testWriteDevData_getCorrectKeyFileAndTestWords() throws IOException {
        // Given
        reviews = p.parse(url);
        p.writeDevData(reviews, "temp/", 0);

        // When

        // Then
    }
}
