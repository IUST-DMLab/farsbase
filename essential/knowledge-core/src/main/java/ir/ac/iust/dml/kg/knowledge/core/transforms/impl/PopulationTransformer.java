package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

@Transformer(value = "population", description = "تبدیل متن به جمعیت")
public class PopulationTransformer implements ITransformer {

    @Override
    public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
        try {
            value = value.replace("نفر", "");
            value = value.replaceAll("\\(.+\\)", "");
          value = TransformUtils.convertToEnglishDigits(value);

            return new TypedValue(ValueType.Double, Math.round(Double.parseDouble(value)) + "", null);
        } catch (Throwable th) {
            throw new TransformException(th);
        }
    }
}
