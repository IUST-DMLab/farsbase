/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.PathWalker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scans CRAWLER directory. Crawler module (written in Python) writes files of each rss feed in a
 * file with name `news.json`.
 * This scanner find these news.json files, renaming the to another name like news.json..consuming.EPOCH_TIME,
 * extracts all triples from raw text modules and writes them to a new folder located in OUTPUT folder.
 * That folder contains a `triples.json` file and a `info.json` file.
 * Updater module, will used `info.json` and write new triples to Knowledge Store.
 */
@Service
public class DirectoryScanner {

  private final List<RawTripleExtractor> extractors;
  private Logger LOGGER = Logger.getLogger(this.getClass());
  private Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final static Type EXPORT_FEEDS_TYPE =
      new TypeToken<Map<String, FeedItem>>() {
      }.getType();

  @SuppressWarnings("FieldCanBeLocal")
  private final String semaphore = "Semaphore";
  private boolean working = false;
  private boolean scanning = false;
  private Path scanPath = ConfigReader.INSTANCE.getPath("raw.extractor.crawler.folder", "~/pkg/raw/crawler");
  private Path outputPath = ConfigReader.INSTANCE.getPath("raw.extractor.output.folder", "~/pkg/raw/output/");

  @Autowired
  public DirectoryScanner(List<RawTripleExtractor> extractors) {
    this.extractors = extractors;
  }

  public void enableScanning() {
    scanning = true;
  }

  @Scheduled(fixedRate = 5000)
  private void scan() {
    // returns if scanning is not enabled
    if (!scanning) return;
    // returns when another scanning is preforming and not ended in previous scan
    synchronized (semaphore) {
      if (working) return;
      working = true;
    }

    LOGGER.info("scanning to find new files at " + scanPath.toAbsolutePath());
    final Long extractionStart = System.currentTimeMillis();
    final List<Path> files = PathWalker.INSTANCE.getPath(scanPath, "news.json");
    final List<RawTriple> allTriples = new ArrayList<>();
    for (Path file : files) {

      LOGGER.info("new file has been found: " + file.toAbsolutePath());
      final Path consumingPath = file.getParent().resolve(file.getFileName() +
          ".consuming." + System.currentTimeMillis());
      try {
        Files.move(file, consumingPath);
      } catch (IOException e) {
        LOGGER.error("error in marking feed as consuming", e);
        continue;
      }
      try (Reader reader = new InputStreamReader(new FileInputStream(consumingPath.toFile()), "UTF-8")) {
        final Map<String, FeedItem> feeds = gson.fromJson(reader, EXPORT_FEEDS_TYPE);
        feeds.values().forEach(feed -> {
          try {
            for (RawTripleExtractor rawTripleExtractor : extractors) {
              final List<RawTriple> triples = rawTripleExtractor.extract(feed.getSource(), feed.getCrawlDate(), feed.getText());
              if (triples != null) allTriples.addAll(triples);
            }
          } catch (Throwable throwable) {
            LOGGER.error("error in relation extraction", throwable);
          }
        });
      } catch (Throwable e) {
        LOGGER.error("error in feed scanning", e);
      }
      // it's not a necessary phase. we can ignore the movement.
      final Path consumedPath = file.getParent().resolve(file.getFileName() +
          ".consumed." + System.currentTimeMillis());
      try {
        Files.move(consumingPath, consumedPath);
      } catch (IOException e) {
        LOGGER.error("error in marking feed as consumed", e);
      }
    }
    if (!allTriples.isEmpty()) {
      try {
        final Path newFolder = outputPath.resolve(String.valueOf(System.currentTimeMillis()));
        Files.createDirectories(newFolder);
        ExtractorUtils.writeTriples(newFolder.resolve("triples.json"), allTriples);
        ExtractorUtils.markExtraction(newFolder, extractionStart);
      } catch (Throwable e) {
        LOGGER.error("error in exporting feed to raw triples.", e);
      }
    }

    synchronized (semaphore) {
      working = false;
    }
  }
}
