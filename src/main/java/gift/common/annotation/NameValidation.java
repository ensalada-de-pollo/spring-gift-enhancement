package gift.common.annotation;

import gift.common.annotation.validator.NameValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidator.class)
public @interface NameValidation {

    String message() default "유효하지 않은 상품명입니다.";

    Class[] groups() default {};

    Class[] payload() default {};
}
