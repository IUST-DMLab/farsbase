package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

@Transformer(value = "long", description = "تبدیل به عدد بلند")
public class LongTransformer implements ITransformer {

  @Override
  public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
    try {
      return new TypedValue(ValueType.Long, Long.parseLong(value) + "", null);
    } catch (Throwable th) {
      throw new TransformException(th);
    }
  }
}
