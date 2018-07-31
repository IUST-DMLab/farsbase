package ir.ac.iust.dml.kg.knowledge.core.transforms.test;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

/**
 *
 */
@Transformer(value = "pricetest", description = "تبدیل تومان به ریال")
public class PriceTransformer implements ITransformer {
    @Override
    public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
        final String[] args = value.split("\\s+");
        final Long v;
        try {
            v = Long.parseLong(args[0]);

        } catch (Throwable th) {
            throw new TransformException(th);
        }
        if (args.length == 1)
            return new TypedValue(ValueType.Long, value, lang);
        else if (args.length != 2)
            throw new TransformException("Must be in format # (تومان|ریال)");
        switch (args[1]) {
            case "تومان":
                return new TypedValue(ValueType.Long, String.valueOf(v * 10), lang);
            case "ریال":
                return new TypedValue(ValueType.Long, String.valueOf(v), lang);
            case "دلار":
                return new TypedValue(ValueType.Long, String.valueOf(v) + "$", lang);
            default:
                throw new TransformException("Unknown unit price");
        }
    }
}
