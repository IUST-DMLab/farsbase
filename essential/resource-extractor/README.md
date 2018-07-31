Extract all resource from a text

# How to use
```java
class Test {
    void sample() {
        IResourceExtractor extractor = new TreeResourceExtractor();
                try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://localhost:8091/")) {
                    extractor.setup(reader, 1000);
                }
                extractor.search(" قانون اساسی ایران ماگدبورگ", true).forEach(System.out::println);
    }
}
```

* if `remove subset` is true, remove subset of entities else not
* for example `farhad rahbar` with removeSubset has 1 entities
* for example `farhad rahbar` with !removeSubset has 2 entities


## Entity readers
read entity from file or rest or ...

* `ResourceReaderFromKGStoreV1Service` read entity from rest KGStore Service `/rs/v1/triples/search`

```java
class Test {
    void sample() {
        try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://194.225.227.161:8091/")) {
            extractor.setup(reader, 1000);
        }
    }
}
```

* `ResourceReaderFromVirtuoso` read entity from virtuoso, 

```java
class Test {
    void sample() {
         final ResourceCache cache = new ResourceCache("h:\\test");
                try (IResourceReader reader = new ResourceReaderFromVirtuoso("194.225.227.161" , "1111",
                        "dba", "dba", "http://localhost:8890/knowledgeGraphV2")) {
                    cache.cache(reader, 100000);
                }
    }
}
```
## Cache entity readers
For cache any reader use this `ResourceCache`
* if useFSTCache is true read and write cache faster

```java
class Test {
    void sample() {
        final ResourceCache cache = new ResourceCache("dir/to/be/saved", true);
        try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://194.225.227.161:8091/")) {
            cache.cache(reader, 10000);
        }
    }
}
```

And you can use `ResourceCache` like other reader

```java
class Test {
    void sample() {
        try (IResourceReader reader = new ResourceCache("dir/to/be/saved", true)) {
            extractor.setup(reader, 1000);
        }
    }
}
```

# Convert label
If you want to change label must pass `ILabelConverter` to setup method of resource extractor
for example for remove parentheses use this sample

```java
class Test {
    void sample() {
        IResourceExtractor extractor = new TreeResourceExtractor();
        try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://localhost:8091/")) {
            extractor.setup(reader, label -> {
                final HashSet<String> newLabels = new HashSet<>();
                newLabels.add(label);
                newLabels.add(label.replaceAll("\\(.*\\)", "").trim());
                return newLabels;
            }, 1000);
        }
    }
}
```