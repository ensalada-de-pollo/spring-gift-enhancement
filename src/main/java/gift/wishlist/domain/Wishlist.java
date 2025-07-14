package gift.wishlist.domain;

public class Wishlist {

    private Long id;
    private Long productId;
    private Long memberId;
    private Integer quantity;

    public Wishlist(Long id, Long productId, Long memberId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.memberId = memberId;
        this.quantity = quantity;
    }

    public Wishlist(Long productId, Long memberId, Integer quantity) {
        this.productId = productId;
        this.memberId = memberId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Wishlist setId(Long id) {
        return new Wishlist(id, productId, memberId, quantity);
    }
}
