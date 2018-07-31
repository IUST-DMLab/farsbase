package ir.ac.iust.dml.kg.resource.extractor.readers.test;

import ir.ac.iust.dml.kg.resource.extractor.IResourceExtractor;
import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.ResourceCache;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromKGStoreV1Service;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromKGStoreV2Service;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromTTLs;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromVirtuoso;
import ir.ac.iust.dml.kg.resource.extractor.tree.TreeResourceExtractor;
import org.junit.Test;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * test readers
 */
public class ReadersTest {
    @Test
    public void testKGStoreV1Service() throws Exception {
        final ResourceCache cache = new ResourceCache("D:\\Cache", true);
        try (IResourceReader reader = new ResourceReaderFromKGStoreV1Service("http://dmls.iust.ac.ir:8091/")) {
            cache.cache(reader, 1000000);
        }

    }

    @Test
    public void testKGStoreV2Service() throws Exception {
        final ResourceCache cache = new ResourceCache("/home/asgari/cache/ks", true);
        try (IResourceReader reader = new ResourceReaderFromKGStoreV2Service("http://dmls.iust.ac.ir:8091/")) {
            cache.cache(reader, 10000);
        }

    }

    @Test
    public void testVirtuosoReader() throws Exception {
        final ResourceCache cache = new ResourceCache("h:\\test2", true);
        try (IResourceReader reader = new ResourceReaderFromVirtuoso("194.225.227.161", "1111",
            "dba", "fkgVIRTUOSO2017", "http://majid.fkg.iust.ac.ir/")) {
            cache.cache(reader, 1000);
        }
    }

  @Test
  public void testTTLReader() throws Exception {
    final ResourceCache cache = new ResourceCache("D:\\test2", true);
    try (IResourceReader reader = new ResourceReaderFromTTLs("C:\\Users\\ASUS\\ttl_store",
        "http://majid.fkg.iust.ac.ir/")) {
      cache.cache(reader, 1000);
    }
  }

    @Test
    public void cacheTest() throws Exception {
        long t1 = System.currentTimeMillis();
        IResourceExtractor extractor = new TreeResourceExtractor();
        try (IResourceReader reader = new ResourceCache("/home/asgari/cache/ks", true)) {
            extractor.setup(reader, 1000);
        }
        System.out.println("" + (System.currentTimeMillis() - t1));
        extractor.search(" قانون اساسی ایران ماگدبورگ", true, false)
            .forEach(System.out::println);
        extractor.search(" قانون اساسی ایران ماگدبورگ", true, true)
            .forEach(System.out::println);
        extractor.search("فرزندان هاشمی رفسنجانی", true, true)
            .forEach(System.out::println);
    }

}
