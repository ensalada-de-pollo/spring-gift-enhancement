package gift.product.dto.request;

import org.springframework.data.domain.Sort;

public record PageFindRequest(int page, int size, Sort.Direction direction, String criteria) {

}
