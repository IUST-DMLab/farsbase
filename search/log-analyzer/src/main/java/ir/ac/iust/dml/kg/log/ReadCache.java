package ir.ac.iust.dml.kg.log;

import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.ResourceCache;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromVirtuoso;


/**
 * Created by ali on 04/05/17.
 */
public class ReadCache {
    public static void main(String[] args) throws Exception {
        final ResourceCache cache = new ResourceCache(args[0], true);
        try (IResourceReader reader = new ResourceReaderFromVirtuoso("194.225.227.161", "1111",
                "dba", "fkgVIRTUOSO2017", "http://fkg.iust.ac.ir/new")) {
            cache.cache(reader, 10000);
        }
    }
}