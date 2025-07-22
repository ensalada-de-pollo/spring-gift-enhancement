package gift.option.service;

import gift.common.exceptions.AlreadyExistsException;
import gift.common.exceptions.FailedToFindException;
import gift.option.domain.Option;
import gift.option.dto.OptionAddRequest;
import gift.option.dto.OptionResponse;
import gift.option.repository.OptionRepository;
import gift.product.domain.Product;
import gift.product.repository.ProductRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(
        OptionRepository optionRepository,
        ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public OptionResponse addOption(Long productId, OptionAddRequest optionAddRequest) {

        Optional<Option> option =
            optionRepository.findByNameAndProductId(optionAddRequest.name(), productId);

        if (option.isPresent()) {
            throw new AlreadyExistsException("이미 존재하는 옵션명입니다.");
        }

        Product product =
            productRepository.findById(productId)
                .orElseThrow(() -> new FailedToFindException("존재하지 않는 상품입니다."));

        return convertToDTO(
            optionRepository.save(
                new Option(
                    optionAddRequest.name(),
                    optionAddRequest.quantity(),
                    product
                )
            )
        );
    }

    private OptionResponse convertToDTO(Option option) {
        return new OptionResponse(
            option.getId(),
            option.getName(),
            option.getQuantity(),
            option.getProduct().getId()
        );
    }
}
