package gift.wishlist.service;

import gift.common.exceptions.FailedToDeleteException;
import gift.common.exceptions.WishAlreadyExistsException;
import gift.wishlist.domain.Wishlist;
import gift.wishlist.dto.WishAddRequest;
import gift.wishlist.dto.WishResponse;
import gift.wishlist.repository.WishlistRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional
    public WishResponse addWish(WishAddRequest wishAddRequest, Long memberId) {
        Optional<Wishlist> wishlist =
            wishlistRepository.findByMemberIdAndProductId(
                memberId,
                wishAddRequest.productId()
            );

        if (wishlist.isPresent()) {
            throw new WishAlreadyExistsException("이미 위시리스트에 추가된 상품입니다.");
        }

        return convertToDTO(
            wishlistRepository.save(
                new Wishlist(
                    wishAddRequest.productId(),
                    memberId,
                    wishAddRequest.quantity()
                )
            )
        );
    }

    @Transactional(readOnly = true)
    public List<WishResponse> getWishes(Long memberId) {
        return wishlistRepository.findByMemberId(memberId)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Transactional
    public void delete(Long wishId, Long memberId) {
        Long id = wishlistRepository.findMemberIdByWishId(wishId);

        if (!id.equals(memberId)) {
            throw new FailedToDeleteException("삭제 권한이 없습니다.");
        }

        wishlistRepository.delete(wishId, memberId);
    }

    private WishResponse convertToDTO(Wishlist wishlist) {
        return new WishResponse(
            wishlist.getId(),
            wishlist.getProductId(),
            wishlist.getMemberId(),
            wishlist.getQuantity()
        );
    }
}
