package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

import java.util.regex.Matcher;

@Transformer(value = "startOfTempRange", description = "تبدیل متن به حداقل دما")
public class StartTempratureTransformer implements ITransformer {

    @Override
    public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
        try {
          final Matcher matcher = TransformUtils.RANGE_PATTERN.matcher(value);
          if (matcher.find())
            return new TypedValue(ValueType.Float, Float.parseFloat(matcher.group(2)) + "", null);
          throw new TransformException("no range matched.");
        } catch (Throwable th) {
            throw new TransformException(th);
        }
    }
}
