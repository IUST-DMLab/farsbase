package ir.ac.iust.dml.kg.knowledge.store.services.v1.validation;

import ir.ac.iust.dml.kg.knowledge.store.services.v1.data.TypedValueData;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Validate TypedValueData
 */
@Deprecated
public class TypedValueValidator implements ConstraintValidator<ValidTypedValue, TypedValueData> {
    private URLValidator urlValidator = new URLValidator();

    public TypedValueValidator() {
//        this.urlValidator.initialize(new URL());
    }

    @Override
    public void initialize(ValidTypedValue validTypedValue) {

    }

    @Override
    public boolean isValid(TypedValueData data, ConstraintValidatorContext context) {
        if (data.getType() == null) return false;
        if (data.getValue() == null || data.getValue().length() == 0)
            return true;
        try {
            Object result = data.getType().parse(data.getValue());
        } catch (Throwable e) {
            return false;
        }
        return true;
    }
}