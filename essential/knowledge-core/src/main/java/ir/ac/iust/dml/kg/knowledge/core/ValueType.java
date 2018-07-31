package ir.ac.iust.dml.kg.knowledge.core;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Types of value that we handle
 */
public enum ValueType {
    Resource, String, Boolean, Byte, Short, Integer, Long, Double, Float, Date;

    public Object parse(String value) {
        try {
            switch (this) {
                case Resource:
                    return new java.net.URL(value);
                case String:
                    return value;
                case Boolean:
                    return java.lang.Boolean.parseBoolean(value);
                case Byte:
                    return java.lang.Byte.parseByte(value);
                case Short:
                    return java.lang.Short.parseShort(value);
                case Integer:
                    return java.lang.Integer.parseInt(value);
                case Long:
                    return java.lang.Long.parseLong(value);
                case Double:
                    return java.lang.Double.parseDouble(value);
                case Float:
                    return java.lang.Float.parseFloat(value);
                case Date:
                  return new java.util.Date(java.lang.Long.parseLong(value));
            }
        } catch (Throwable throwable) {
            return new RuntimeException("Can not parse", throwable);
        }
        throw new RuntimeException("Unknown Type");
    }
}
