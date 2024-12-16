package org.bhn.resource.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

//import javax.validation.Constraint;
//import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername
{
    String message() default "User id already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}