package gift.option.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record OptionAddRequest(
    @NotBlank(message = "옵션명을 입력해주세요.")
    @Pattern(
        regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\(\\)\\[\\]\\+\\-\\&/_ ]*$",
        message = "상품 이름은 한글, 영어, 숫자, 특수문자(( ), [ ], +, -, &, /, _) 외 다른 문자가 들어갈 수 없습니다."
    )
    @Length(
        max = 50,
        message = "등록 가능한 옵션명의 최대 길이는 50자 입니다."
    )
    String name,

    @NotNull(message = "수량을 입력해주세요.")
    @Min(value = 1, message = "최솟값은 1 입니다.")
    @Max(value = 100_000_000, message = "최댓값은 100,000,000 입니다.")
    Long quantity) {

}
