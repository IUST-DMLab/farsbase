package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

import java.net.URL;

@Transformer(value = "url", description = "تبدیل به لینک")
public class UrlTransformer implements ITransformer {

  @Override
  public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
    try {
      return new TypedValue(ValueType.Resource, new URL(value) + "", null);
    } catch (Throwable th) {
      throw new TransformException(th);
    }
  }
}
