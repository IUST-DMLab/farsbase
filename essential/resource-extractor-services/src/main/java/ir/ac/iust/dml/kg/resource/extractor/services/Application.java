package ir.ac.iust.dml.kg.resource.extractor.services;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.ResourceCache;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromKGStoreV2Service;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromTTLs;
import ir.ac.iust.dml.kg.resource.extractor.readers.ResourceReaderFromVirtuoso;
import ir.ac.iust.dml.kg.resource.extractor.services.web.Jackson2ObjectMapperPrettier;
import ir.ac.iust.dml.kg.resource.extractor.services.web.filter.FilterRegistrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
//@ImportResource(value = {})
@EnableAutoConfiguration(exclude = {
    Jackson2ObjectMapperPrettier.class,
    FilterRegistrationConfiguration.class})
@ComponentScan
public class Application {

  public static void main(String[] args) throws Exception {
    Path cacheAddress = ConfigReader.INSTANCE.getPath("searcher.cache.dir", "cache");
    if (!Files.exists(cacheAddress)) {
      Files.createDirectories(cacheAddress);
      final ResourceCache cache = new ResourceCache(cacheAddress.toAbsolutePath().toString(),
          true);
      final ConfigReader cfg = ConfigReader.INSTANCE;
      if (args.length > 0 && args[0].equals("store")) {
        try (IResourceReader reader = new ResourceReaderFromKGStoreV2Service(
            ConfigReader.INSTANCE.getString("knowledge.store.base.url", "http://localhost:8091/"))) {
          cache.cache(reader, 10000);
        }
      } else if (args.length > 0 && args[0].equals("ttl")) {
        try (IResourceReader reader = new ResourceReaderFromTTLs(
            cfg.getPath("store.ttl.root", "~/ttl_store").toString(),
            cfg.getString("virtuoso.graph", URIs.INSTANCE.getDefaultContext()))) {
          cache.cache(reader, 10000);
        }
      } else {
        try (IResourceReader reader = new ResourceReaderFromVirtuoso(
            cfg.getString("virtuoso.host", "194.225.227.161"),
            cfg.getString("virtuoso.port", "1111"),
            cfg.getString("virtuoso.user", "dba"),
            cfg.getString("virtuoso.password", "fkgVIRTUOSO2017"),
            cfg.getString("virtuoso.graph", URIs.INSTANCE.getDefaultContext()))) {
          cache.cache(reader, 10000);
        }
      }
    }
    SpringApplication.run(Application.class, args);
  }

}
