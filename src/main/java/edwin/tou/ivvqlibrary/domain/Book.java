package edwin.tou.ivvqlibrary.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Generated(reason = "POJO")
@JsonInclude(value = Include.NON_NULL)
public class Book {

    private String title;
    private String subtitle;
    private String isbn13;
    private String price;
    private String image;
    private String url;

    private String authors;

    private String publisher;

    private String pages;

    private String year;

    private String rating;

    private String desc;

    private boolean borrowed = false;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String error;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return (
            "Book{" +
            "title='" +
            title +
            '\'' +
            ", subtitle='" +
            subtitle +
            '\'' +
            ", isbn13='" +
            isbn13 +
            '\'' +
            ", price='" +
            price +
            '\'' +
            ", image='" +
            image +
            '\'' +
            ", url='" +
            url +
            '\'' +
            ", authors='" +
            authors +
            '\'' +
            ", publisher='" +
            publisher +
            '\'' +
            ", pages='" +
            pages +
            '\'' +
            ", year='" +
            year +
            '\'' +
            ", rating='" +
            rating +
            '\'' +
            ", desc='" +
            desc +
            '\'' +
            ", error='" +
            error +
            '\'' +
            '}'
        );
    }
}
