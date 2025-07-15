package gift.product.service;

import gift.product.domain.Product;
import gift.product.dto.request.ProductSaveRequest;
import gift.product.dto.request.ProductUpdateRequest;
import gift.product.dto.response.ProductResponse;
import gift.product.repository.ProductRepository;
import gift.wishlist.repository.WishlistRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public ProductService(
        ProductRepository productRepository,
        WishlistRepository wishlistRepository
    ) {
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional
    public ProductResponse save(ProductSaveRequest productSaveRequest) {
        return convertToDTO(
            productRepository.save(
                new Product(
                    productSaveRequest.name(),
                    productSaveRequest.price(),
                    productSaveRequest.imageURL()
                )
            )
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return convertToDTO(
            productRepository.findById(id)
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest productUpdateRequest) {
        Product product = new Product(
            productUpdateRequest.name(),
            productUpdateRequest.price(),
            productUpdateRequest.imageURL()
        );

        productRepository.update(id, product);

        return convertToDTO(
            productRepository.findById(id)
        );
    }

    @Transactional
    public void delete(Long id) {
        wishlistRepository.deleteByProductId(id);
        productRepository.delete(id);
    }

    private ProductResponse convertToDTO(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getImageURL()
        );
    }
}
