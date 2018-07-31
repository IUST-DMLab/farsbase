/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic;

import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.services.access.entities.*;
import ir.ac.iust.dml.kg.raw.services.access.repositories.ArticleRepository;
import ir.ac.iust.dml.kg.raw.services.access.repositories.DependencyPatternRepository;
import ir.ac.iust.dml.kg.raw.services.access.repositories.OccurrenceRepository;
import ir.ac.iust.dml.kg.raw.services.logic.data.SentenceSelection;
import ir.ac.iust.dml.kg.raw.services.logic.data.TextRepositoryFile;
import ir.ac.iust.dml.kg.raw.services.web.rest.data.RepositoryStats;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextRepositoryLogic {

  private final Path mainPath = ConfigReader.INSTANCE.getPath("raw.repository", "~/raw/repository");
  @Autowired
  ArticleRepository articleRepository;
  @Autowired
  DependencyPatternRepository dependencyPatternRepository;
  @Autowired
  OccurrenceRepository occurrenceRepository;
  @Autowired
  UserLogic userLogic;

  private Path getPath(String path) {
    if (path == null || path.isEmpty() || path.contains("..") || path.startsWith("/")) return mainPath;
    return mainPath.resolve(path);
  }

  public List<TextRepositoryFile> ls(String path) throws IOException {
    return Files.list(getPath(path))
        .filter(file -> Files.isDirectory(file) || file.getFileName().toString().endsWith(".json"))
        .map(file -> new TextRepositoryFile(Files.isDirectory(file), file.getFileName().toString()))
        .sorted(Comparator.comparing(TextRepositoryFile::getName))
        .collect(Collectors.toList());
  }

  public List<List<ResolvedEntityToken>> get(String path) {
    final Path p = getPath(path);
    if (Files.isDirectory(p)) return null;
    return EnhancedEntityExtractor.importFromFile(p);
  }

  public boolean mark(String username, String path) {
    final Path p = getPath(path);
    if (Files.isDirectory(p)) return false;
    Article article = articleRepository.findByPath(path);
    if (article != null) return false;
    final List<List<ResolvedEntityToken>> content = EnhancedEntityExtractor.importFromFile(p);
    if (content == null) return false;
    final User user = userLogic.getUserOrCreate(username);
    if (user == null) return false;
    article = new Article();
    article.setPath(path);
    article.setTitle(path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path);
    article.setNumberOfSentences(content.size());
    article.setNumberOfRelations(0);
    article.setPercentOfRelations(0f);
    article.setApproved(false);
    article.setSelectedByUser(user);
    for (List<ResolvedEntityToken> sentence : content) {
      final ArticleSentence articleSentence = new ArticleSentence();
      articleSentence.setNumberOfRelations(0);
      articleSentence.setSentence(join(sentence));
      articleSentence.setTokens(sentence);
      article.getSentences().add(articleSentence);
    }
    try {
      articleRepository.save(article);
    } catch (Throwable e) {
      return false;
    }
    return true;
  }

  public Page<Article> searchArticles(int page, int pageSize, String path, String title,
                                      Integer minPercentOfRelations, Boolean approved,
                                      String selectedByUsername) {
    User selectedByUser = null;
    if (selectedByUsername != null) selectedByUser = userLogic.getUser(selectedByUsername);
    return articleRepository.search(page, pageSize, path, title, minPercentOfRelations, approved, selectedByUser);
  }

  public Article saveArticle(String username, Article article) {
    if (article.getIdentifier() == null) return null;
    final User user = userLogic.getUserOrCreate(username);
    if (user == null) return null;
    final Article articleInDB = articleRepository.findOne(new ObjectId(article.getIdentifier()));
    if (articleInDB == null) return null;
    articleInDB.setTitle(article.getTitle());
    articleInDB.setApproved(article.isApproved());
    int numberOfRelations = 0;
    List<ArticleSentence> sentences = article.getSentences();
    for (int i = 0; i < sentences.size(); i++) {
      final ArticleSentence sentence = sentences.get(i);
      if (sentence.getNumberOfRelations() < 0) continue;
      final ArticleSentence sentenceInDb = articleInDB.getSentences().get(i);
      sentenceInDb.setNumberOfRelations(sentence.getNumberOfRelations());
      numberOfRelations += sentence.getNumberOfRelations();
//      articleInDB.getSentences().get()
    }
    articleInDB.setNumberOfRelations(numberOfRelations);
    articleInDB.setPercentOfRelations((float) numberOfRelations / article.getNumberOfSentences());

    articleRepository.save(articleInDB);
    return articleInDB;
  }

  private String buildTreeHash(List<ResolvedEntityToken> sentence) {
    StringBuilder hashBuilder = new StringBuilder();
    for (ResolvedEntityToken token : sentence) {
      hashBuilder.append('[')
          .append(token.getPos())
          .append(',').append(token.getDep().getHead())
          .append(',').append(token.getDep().getRelation())
          .append(']');
    }
    return hashBuilder.toString();
  }

  private String join(List<ResolvedEntityToken> token) {
    return String.join(" ", token.stream().map(ResolvedEntityToken::getWord).collect(Collectors.toList()));
  }

  public DependencyPattern selectForDependencyRelation(String username, SentenceSelection selection) {
    if (selection.getTokens() == null) return null;
    final User user = userLogic.getUserOrCreate(username);
    if (user == null) return null;
    String hash = buildTreeHash(selection.getTokens());

    DependencyPattern pattern = dependencyPatternRepository.findByPattern(hash);
    if (pattern != null) {
      pattern.setSelectedByUser(user);
      dependencyPatternRepository.save(pattern);
      return pattern;
    }

    pattern = new DependencyPattern();
    pattern.setCount(1);
    pattern.getSamples().add(join(selection.getTokens()));
    pattern.setSelectedByUser(user);
    pattern.setPattern(hash);
    pattern.setSentenceLength(selection.getTokens().size());
    if (selection.getObject() != null && selection.getSubject() != null && selection.getPredicate() != null) {
      RelationDefinition definition = new RelationDefinition();
      definition.setAccuracy(1);
      definition.setSubject(selection.getSubject());
      definition.setObject(selection.getObject());
      definition.setPredicate(selection.getPredicate());
      pattern.getRelations().add(definition);
    }
    dependencyPatternRepository.save(pattern);
    return pattern;
  }

  public RepositoryStats stats() {
    final List<Article> articles = articleRepository.findAll();
    final RepositoryStats stats = new RepositoryStats();
    articles.forEach(article -> {
      if (article.isApproved()) {
        final RepositoryStats.SourceStats ss = article.getPath().contains("news")
            ? stats.getNews() : stats.getWikipedia();
        ss.setNumberOfArticles(ss.getNumberOfArticles() + 1);
        ss.setNumberOfSentences(ss.getNumberOfSentences() + article.getNumberOfSentences());
        ss.setNumberOfRelations(ss.getNumberOfRelations() + article.getNumberOfRelations());
      }
    });
    return stats;
  }

  private class Replacement {
    List<Integer> indexes;
    String replaceWith;

    Replacement(List<Integer> indexes, String replaceWith) {
      this.indexes = indexes;
      this.replaceWith = replaceWith;
    }
  }

  private String replaceByIndex(List<ResolvedEntityToken> tokens, Replacement... replacements) {
    final StringBuilder builder = new StringBuilder();
    boolean[] seen = new boolean[replacements.length];
    for (int i = 0; i < tokens.size(); i++) {
      final ResolvedEntityToken token = tokens.get(i);
      boolean replaced = false;
      for (int j = 0; j < replacements.length; j++) {
        Replacement r = replacements[j];
        if (r.indexes.contains(i)) {
          if (!seen[j] && !replaced) {
            builder.append(r.replaceWith).append(' ');
            seen[j] = true;
          }
          if (seen[j]) replaced = true;
        }
      }
      if (!replaced) builder.append(token.getWord()).append(' ');
    }
    builder.setLength(builder.length() - 1);
    return builder.toString();
  }

  private String getString(List<ResolvedEntityToken> tokens, List<Integer> indexes) {
    StringBuilder builder = new StringBuilder();
    for (int index : indexes) builder.append(tokens.get(index).getWord()).append(' ');
    builder.setLength(builder.length() - 1);
    return builder.toString();
  }

  public Occurrence selectForOccurrence(String username, SentenceSelection selection) {
    if ((selection.getTokens() == null || selection.getTokens().isEmpty())
        || selection.getSubject() == null || selection.getSubject().isEmpty()
        || selection.getObject() == null || selection.getObject().isEmpty()
        || ((selection.getPredicate() == null || selection.getPredicate().isEmpty())
        && selection.getManualPredicate() == null))
      return null;
    final User user = userLogic.getUserOrCreate(username);
    if (user == null) return null;
    final String normalized = join(selection.getTokens());
    final String generalized = replaceByIndex(selection.getTokens(),
        new Replacement(selection.getSubject(), "$SUBJ"),
        new Replacement(selection.getObject(), "$OBJ"));
    Occurrence occurrence = occurrenceRepository.getByNormalized(normalized);
    if (occurrence != null && occurrence.getGeneralizedSentence().equals(generalized)) {
      occurrence.setOccurrence(occurrence.getOccurrence() + 1);
      occurrence.setSelectedByUser(user);
      occurrence.setApproved(true);
      occurrenceRepository.save(occurrence);
      return occurrence;
    }
    occurrence = new Occurrence();
    occurrence.setOccurrence(1);
    occurrence.setApproved(true);
    occurrence.setAssignee(user);
    occurrence.setSelectedByUser(user);
    occurrence.setNormalized(normalized);
    occurrence.setPosTags(selection.getTokens().stream().map(ResolvedEntityToken::getPos).collect(Collectors.toList()));
    occurrence.setRaw(occurrence.getNormalized());
    occurrence.setWords(selection.getTokens().stream().map(ResolvedEntityToken::getWord).collect(Collectors.toList()));
    occurrence.setDepTreeHash(buildTreeHash(selection.getTokens()));
    occurrence.setGeneralizedSentence(generalized);
    if (selection.getManualPredicate() != null && !selection.getManualPredicate().isEmpty())
      occurrence.setPredicate(selection.getManualPredicate());
    else occurrence.setPredicate(getString(selection.getTokens(), selection.getPredicate()));
    occurrence.setObject(getString(selection.getTokens(), selection.getObject()));
    occurrence.setSubject(getString(selection.getTokens(), selection.getSubject()));
    occurrenceRepository.save(occurrence);
    return occurrence;
  }

}
