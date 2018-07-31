package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;

@Transformer(value = "mile2km", description = "تبدیل مایل به کیلومتر")
public class MileToKmTransformer implements ITransformer {

    @Override
    public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
        try {
            return new TypedValue(ValueType.Float, milesTokm(Double.parseDouble(value)) + "", null);
        } catch (Throwable th) {
            throw new TransformException(th);
        }
    }

    private static double milesTokm(double distanceInMiles) {

        return distanceInMiles * 1.60934;

    }

    private static double kmTomiles(double distanceInKm) {

        return distanceInKm * 0.621371;

    }

}
