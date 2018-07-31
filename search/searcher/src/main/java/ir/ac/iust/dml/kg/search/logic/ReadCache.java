package ir.ac.iust.dml.kg.search.logic;

import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.ResourceCache;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromVirtuoso;


/**
 * Created by ali on 04/05/17.
 */
public class ReadCache {
    public static void main(String[] args) throws Exception {
        if(args.length < 3) {
            System.out.println("parameters: CacheOutputFolder VirtuosoIP GraphURI");
            System.out.println("default params: cache 127.0.0.1 http://fkg.iust.ac.ir/new");
            System.exit(1);
        }
        String cacheOutputFolder = args[0];
        String virtuosoIP = args[1]; //"127.0.0.1";
        String graphURI = args[2]; // "http://fkg.iust.ac.ir/new";

        final ResourceCache cache = new ResourceCache(cacheOutputFolder, true);

        try (IResourceReader reader = new ResourceReaderFromVirtuoso(virtuosoIP, "1111",
                "user_e_SHOMA", "password_e_SHOMA", graphURI)) {
            cache.cache(reader, 10000);
        }
    }
}