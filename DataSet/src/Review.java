public class Review {
    String comment;
    Double rating;
    Integer helpful;
    Cls cls;

    public Review(String parsedComment, String parsedRating, String parsedhelpful) {
        this.comment = parsedComment;
        this.rating = Double.parseDouble(parsedRating);
        this.helpful = Integer.parseInt(parsedhelpful);
        this.cls = (this.rating >= 4) ? Cls.P : Cls.N;
    }

    @Override
    public String toString() {
        return cls.toString() + "(" + rating + "): " + comment + " - " + helpful;
    }
}

