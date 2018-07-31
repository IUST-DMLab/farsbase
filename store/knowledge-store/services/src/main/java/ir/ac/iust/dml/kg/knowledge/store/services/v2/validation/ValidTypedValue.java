package ir.ac.iust.dml.kg.knowledge.store.services.v2.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@Constraint(validatedBy = {TypedValueValidator.class})
//This constraint annotation can be used only on fields and method parameters.
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ValidTypedValue {
    //The message to return when the instance of MyAddress fails the validation.
    String message() default "Invalid Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


