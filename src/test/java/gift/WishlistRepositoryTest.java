package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.member.domain.Member;
import gift.member.domain.enums.UserRole;
import gift.member.repository.MemberRepository;
import gift.product.domain.Product;
import gift.product.repository.ProductRepository;
import gift.wishlist.domain.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Product product;
    private Member member;

    @BeforeEach
    void setUp() {
        product = productRepository.save(new Product(
            "상품1",
            1234L,
            "testurl"
        ));

        member = memberRepository.save(new Member(
            "asdf@gmail.com",
            "password",
            UserRole.NORMAL
        ));
    }

    @Test
    void save() {
        Wishlist wish = new Wishlist(product, member);

        Wishlist savedWish = wishlistRepository.save(wish);

        assertAll(
            () -> assertThat(savedWish.getId()).isNotNull(),
            () -> assertThat(savedWish.getProduct()).isEqualTo(wish.getProduct()),
            () -> assertThat(savedWish.getMember()).isEqualTo(wish.getMember())
        );
    }

    @Test
    void findByMemberIdAndProductId() {
        Wishlist wish = new Wishlist(product, member);
        wishlistRepository.save(wish);

        Wishlist savedWish =
            wishlistRepository.findByMemberIdAndProductId(1L, 1L).get();

        assertAll(
            () -> assertThat(savedWish.getId()).isNotNull(),
            () -> assertThat(savedWish.getProduct()).isEqualTo(wish.getProduct()),
            () -> assertThat(savedWish.getMember()).isEqualTo(wish.getMember())
        );

    }

    @Test
    void findByMemberId() {
        Wishlist wish = new Wishlist(product, member);
        wishlistRepository.save(wish);

        List<Wishlist> savedWish =
            wishlistRepository.findByMemberId(wish.getMember().getId());

        assertAll(
            () -> assertThat(savedWish.size()).isEqualTo(1),
            () -> assertThat(savedWish.getFirst().getId()).isNotNull(),
            () -> assertThat(savedWish.getFirst().getProduct()).isEqualTo(wish.getProduct()),
            () -> assertThat(savedWish.getFirst().getMember()).isEqualTo(wish.getMember())
        );
    }

    @Test
    void getMemberIdById() {
        Wishlist wish = new Wishlist(product, member);
        wishlistRepository.save(wish);

        Long id = wishlistRepository.getMemberIdById(1L);

        assertThat(id).isEqualTo(wish.getMember().getId());
    }

    @Test
    void deleteByIdAndMemberId() {
        Wishlist wish = new Wishlist(product, member);
        Wishlist savedWish = wishlistRepository.save(wish);
        assertThat(savedWish.getId()).isNotNull();

        wishlistRepository.deleteByIdAndMemberId(
            1L,
            wish.getMember().getId()
        );

        Optional<Wishlist> deletedWish = wishlistRepository.findById(1);

        assertThat(deletedWish).isEmpty();
    }

    @Test
    void deleteByProductId() {
        Wishlist wish = new Wishlist(product, member);
        Wishlist savedWish = wishlistRepository.save(wish);
        assertThat(savedWish.getId()).isNotNull();

        wishlistRepository.deleteByProductId(wish.getProduct().getId());

        Optional<Wishlist> deletedWish = wishlistRepository.findById(1);

        assertThat(deletedWish).isEmpty();
    }
}