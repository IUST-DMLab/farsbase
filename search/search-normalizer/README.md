# Persian Character normalizer
## simple usage
normalize by all available normalization options
```java
class Test {
    void sample() {
        PersianCharNormalizer normalizer = new PersianCharNormalizer();
        String result = normalizer.normalize("text");
    }
}
```
## advanced usage
we can define normalization options using `List<PersianCharNormalizer.Option>` 
```java
class Test {
    void sample() {
        List<PersianCharNormalizer.Option> options=new ArrayList<>();
        options.add(PersianCharNormalizer.Option.NORMAL_YEH);
        options.add(PersianCharNormalizer.Option.NORMAL_KAF);
        options.add(PersianCharNormalizer.Option.NORMAL_ALEF);
        PersianCharNormalizer instance = new PersianCharNormalizer(options);
        String result = normalizer.normalize("text");
    }
}
```
## available normalization options
| `PersianCharNormalizer.Option` | description |
| --- | --- |
|`NORMAL_YEH` | convert various type of arabic Yeh to persian Yeh ('ي','ے','ۓ','ى','ئ','ې') => 'ی'|
|`NORMAL_KAF` |convert arabic Kaf to persian Kaf ('ك') => 'ک'
|`NORMAL_HE` |convert various type of He to persian He ('ۀ','ہ','ۂ','ھ') => 'ه'
|`NORMAL_ALEF` |convert Alef with Madda and Alef with Hamza to normal Alef ('آ','أ') => 'ا'
|`NORMAL_WAW` |convert Waw with Hamza to normal Waw ('ؤ','ٶ') => 'و'
|`NORMAL_NUMBERS` |convert arabic and persian numbers to english numbers ('١','٢',...) => '1','2',...
