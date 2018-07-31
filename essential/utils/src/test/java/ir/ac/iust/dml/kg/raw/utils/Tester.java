package ir.ac.iust.dml.kg.raw.utils;

import ir.ac.iust.dml.kg.raw.utils.dump.owl.OwlDumpReader;
import ir.ac.iust.dml.kg.raw.utils.dump.owl.Triple;
import ir.ac.iust.dml.kg.raw.utils.dump.triple.TripleData;
import ir.ac.iust.dml.kg.raw.utils.dump.triple.TripleJsonFileReader;
import ir.ac.iust.dml.kg.raw.utils.dump.wiki.WikiArticle;
import ir.ac.iust.dml.kg.raw.utils.dump.wiki.WikiDumpReader;
import ir.ac.iust.dml.kg.raw.utils.dump.wiki.WikiDumpWriter;
import kotlin.text.Regex;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Tester {

  @Test
  public void pathWalker() throws IOException {
    final Path p = Paths.get(System.getProperty("user.home"), ".pkg", "utils-test");
    Files.createDirectories(p);
    if (!Files.exists(p.resolve("1.txt"))) Files.createFile(p.resolve("1.txt"));
    if (!Files.exists(p.resolve("2.txt"))) Files.createFile(p.resolve("2.txt"));
    Files.createDirectories(p.resolve("folder"));
    if (!Files.exists(p.resolve("folder").resolve("3.txt"))) Files.createFile(p.resolve("folder").resolve("3.txt"));

    List<Path> pathList = PathWalker.INSTANCE.getPath(p);
    assert pathList.size() == 3;

    pathList = PathWalker.INSTANCE.getPath(p, new Regex("\\d+.txt"));
    assert pathList.size() == 3;

    pathList = PathWalker.INSTANCE.getPath(p, new Regex("\\d-a.txt"));
    assert pathList.size() == 0;

    Files.delete(p.resolve("folder").resolve("3.txt"));
    Files.delete(p.resolve("folder"));
    Files.delete(p.resolve("2.txt"));
    Files.delete(p.resolve("1.txt"));
    Files.delete(p);
  }

  @Test
  public void languageChecker() {
    assert LanguageChecker.INSTANCE.isEnglish("I'm Majid");
    assert !LanguageChecker.INSTANCE.isEnglish("من مجید یا Majid هستم.");
    assert !LanguageChecker.INSTANCE.isEnglish("من مجید هستم.");
  }

  @Test
  public void path() {
    Path path = ConfigReader.INSTANCE.getPath("~/pkg");
    assert path.startsWith(System.getProperty("user.home"));
    if (File.separatorChar == '/') {
      System.out.println("I am in linux-like OS");
      path = ConfigReader.INSTANCE.getPath("/test");
      assert path.toAbsolutePath().toString().equals("/test");
    } else {
      System.out.println("I am in windows-like OS");
      path = ConfigReader.INSTANCE.getPath("C:\\test");
      assert path.toAbsolutePath().toString().equals("C:\\test");
      path = ConfigReader.INSTANCE.getPath("C://test");
      assert path.toAbsolutePath().toString().equals("C:\\test");
    }
  }

  @Test
  public void configReader() {
    final String dummyTest = ConfigReader.INSTANCE.getString("dummy.test", "Dummy Test");
    assert dummyTest.equals("Dummy Test");
  }

  public void howToUse() {
    // It's not a test. Just learn how to use libraries.

    // try on resource code style
    try (OwlDumpReader reader = new OwlDumpReader(
            ConfigReader.INSTANCE.getPath("test.dump.file", "~/pkg/test/dump.ttl"))) {
      while (reader.hasNext()) {
        final List<Triple> t = reader.next();
        System.out.println(t);
      }
    }

    /*
     * reading triples extracted from wiki by extractors team (python).
     * extraction phase creates a folder which contains some json files with this structure:
     * {
     *    "object": "fa.wikipedia.org/wiki/گرگان",
     *    "predicate": "محل تولد",
     *    "source": "fa.wikipedia.org/wiki/رامتین_خداپناهی",
     *    "subject": "fa.wikipedia.org/wiki/رامتین_خداپناهی",
     *    "template_name": "جعبه",
     *    "type": "بازیگر",
     *    "version": "15954145"
     * }
     */
    final Path tripleFolder = ConfigReader.INSTANCE.getPath("test.triple.folder", "~/pkg/test/triples");
    final List<Path> files = PathWalker.INSTANCE.getPath(tripleFolder);
    for (Path p : files)
      try (TripleJsonFileReader reader = new TripleJsonFileReader(p)) {
        while (reader.hasNext()) {
          final TripleData t = reader.next();
          System.out.println(t);
        }
      }

    // reading a wiki dump file and selecting articles which starts with شیخ
    final List<WikiArticle> sheykhArticles = new ArrayList<>();
    try (WikiDumpReader reader = new WikiDumpReader(
            ConfigReader.INSTANCE.getPath("test.wiki.dump.file", "~/pkg/test/wiki-dump.xml"))) {
      while (reader.hasNext()) {
        final WikiArticle t = reader.next();
        if (t.getTitle() != null && t.getTitle().startsWith("شیخ")) sheykhArticles.add(t);
        System.out.println(t);
      }
    }

    // writing all شیخ articles to a standard wiki dump file
    try (WikiDumpWriter writer = new WikiDumpWriter(
            ConfigReader.INSTANCE.getPath("test.wiki.write.dump.file", "~/pkg/test/sheykh-dump.xml"))) {
      for (WikiArticle a : sheykhArticles)
        writer.write(a);
    }
  }
}
