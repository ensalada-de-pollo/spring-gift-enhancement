package gift.product.domain;

public class Product {

    private Long id;
    private String name;
    private Long price;
    private String imageURL;

    public Product(String name, Long price, String imageURL) {
        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
    }

    public Product(Long id, String name, Long price, String imageURL) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Product setId(Long id) {
        return new Product(id, name, price, imageURL);
    }
}
