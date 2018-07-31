/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.virtuoso.connector;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleObject;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTripleType;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import virtuoso.rdf4j.driver.VirtuosoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VirtuosoConnector {

  private static ValueFactory factory = SimpleValueFactory.getInstance();
  private RepositoryConnection con;
  private final VirtuosoRepository repository;

  public VirtuosoConnector() {
    this("http://fkg.iust.ac.ir/");
  }

  public VirtuosoConnector(String graphName) {
    this(graphName, ConfigReader.INSTANCE.getString("virtuoso.address",
        "localhost:1111"),
        ConfigReader.INSTANCE.getString("virtuoso.user", "dba"),
        ConfigReader.INSTANCE.getString("virtuoso.password", "admin"));
  }

  public VirtuosoConnector(String graphName, String serverAddress, String username, String password) {
    repository = new VirtuosoRepository("jdbc:virtuoso://" + serverAddress + "/autoReconnect=true/charset=UTF-8",
        username, password, graphName);
    repository.setQueryTimeout(300000);
    con = repository.getConnection();
  }

  public boolean clear() {
    try {
      con.clear();
      return true;
    } catch (Throwable th) {
      handle(th);
      return false;
    }
  }

  public boolean removeResource(String subject, String predicate, String object) {
    try {
      con.remove(factory.createIRI(subject), factory.createIRI(predicate), factory.createIRI(object));
      return true;
    } catch (Throwable th) {
      handle(th);
      return false;
    }
  }

  private void handle(Throwable th) {
    th.printStackTrace();
    try {
      con.close();
      con = repository.getConnection();
    } catch (Throwable ignored) {
    }
  }

  public boolean removeLiteral(String subject, String predicate, Object object) {
    try {
      con.remove(factory.createIRI(subject), factory.createIRI(predicate), createLiteral(object));
      return true;
    } catch (Throwable th) {
      handle(th);
      return false;
    }
  }

  public boolean addResource(String subject, String predicate, String object) {
    try {
      con.add(factory.createIRI(subject), factory.createIRI(predicate), factory.createIRI(object));
      return true;
    } catch (Throwable th) {
      handle(th);
      return false;
    }
  }

  public boolean addLiteral(String subject, String predicate, Object object) {
    try {
      con.add(factory.createIRI(subject), factory.createIRI(predicate), createLiteral(object));
      return true;
    } catch (Throwable th) {
      handle(th);
      return false;
    }
  }

  public List<VirtuosoTriple> getTriplesOfSubject(String subject) {
    String queryString =
        "SELECT ?p ?o\n" +
            "WHERE {\n" +
            "<" + subject + "> ?p ?o .\n" +
            "}";
    try {
      final List<VirtuosoTriple> converted = new ArrayList<>();
      TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      final TupleQueryResult results = query.evaluate();
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final String p = d.getBinding("p").getValue().stringValue();
        final Value o = d.getBinding("o").getValue();
        converted.add(new VirtuosoTriple(subject, p, convertObject(o)));
      }
      return converted;
    } catch (Throwable th) {
      handle(th);
      return null;
    }
  }

  public List<VirtuosoTriple> getTriples(String subject, String predicate) {
    try {
      String queryString =
          "SELECT ?o\n" +
              "WHERE {\n" +
              "<" + subject + "> <" + predicate + "> ?o .\n" +
              "}";
      final List<VirtuosoTriple> converted = new ArrayList<>();
      TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      final TupleQueryResult results = query.evaluate();
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final Value o = d.getBinding("o").getValue();
        converted.add(new VirtuosoTriple(subject, predicate, convertObject(o)));
      }
      return converted;
    } catch (Throwable th) {
      handle(th);
      return null;
    }
  }

  public List<VirtuosoTriple> getTriplesOfObject(String predicate, String object) {
    return getTriplesOfObject(predicate, object, 0, -1);
  }

  public List<VirtuosoTriple> getTriplesOfObject(String predicate, String object, int page, int pageSize) {
    String queryString =
        "SELECT ?s\n" +
            "WHERE {\n" +
            "?s <" + predicate + "> <" + object + "> .\n" +
            "}" + ((page >= 0 && pageSize >= 0) ? " OFFSET " + (page * pageSize) + " LIMIT " + pageSize : "");
    try {
      final List<VirtuosoTriple> converted = new ArrayList<>();
      TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      final TupleQueryResult results = query.evaluate();
      while (results.hasNext()) {
        final BindingSet d = results.next();
        final String subject = d.getBinding("s").getValue().stringValue();
        converted.add(new VirtuosoTriple(subject, predicate, convertObject(object)));
      }
      return converted;
    } catch (Throwable th) {
      handle(th);
      return null;
    }
  }

  public TupleQueryResult query(String queryString) {
    try {
      TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
      return query.evaluate();
    } catch (Throwable th) {
      handle(th);
      return null;
    }
  }

  public static VirtuosoTripleObject convertObject(String o) {
    if (o.startsWith("http://"))
      return new VirtuosoTripleObject(VirtuosoTripleType.Resource, o, null);
    return new VirtuosoTripleObject(VirtuosoTripleType.String, o, null);
  }

  public static Value createLiteral(Object object) {
    if (object instanceof Boolean) return factory.createLiteral((Boolean) object);
    if (object instanceof Byte) return factory.createLiteral((Byte) object);
    if (object instanceof Short) return factory.createLiteral((Short) object);
    if (object instanceof Integer) return factory.createLiteral((Integer) object);
    if (object instanceof Long) return factory.createLiteral((Long) object);
    if (object instanceof Float) return factory.createLiteral((Float) object);
    if (object instanceof Double) return factory.createLiteral((Double) object);
    if (object instanceof Date) return factory.createLiteral((Date) object);
    if (object instanceof String) return factory.createLiteral((String) object);
    return null;
  }

  public static VirtuosoTripleObject convertObject(Value o) {
    if (o instanceof Resource)
      return new VirtuosoTripleObject(VirtuosoTripleType.Resource, o.toString(), null);
    else if (o instanceof Literal) {
      final Literal l = (Literal) o;
      final VirtuosoTripleType type;
      final Object value;
      final String dataType;
      if (l.getDatatype() == null) {
        dataType = null;
        type = VirtuosoTripleType.String;
        value = l.stringValue();
      } else {
        dataType = l.getDatatype().toString();
        if (dataType.endsWith("long")) {
          type = VirtuosoTripleType.Long;
          value = l.longValue();
        } else if (dataType.endsWith("int")) {
          type = VirtuosoTripleType.Int;
          value = l.intValue();
        } else if (dataType.endsWith("short")) {
          type = VirtuosoTripleType.Short;
          value = l.intValue();
        } else if (dataType.endsWith("double")) {
          type = VirtuosoTripleType.Double;
          value = l.doubleValue();
        } else if (dataType.endsWith("boolean")) {
          type = VirtuosoTripleType.Boolean;
          value = l.doubleValue();
        } else if (dataType.endsWith("byte")) {
          type = VirtuosoTripleType.Byte;
          value = l.byteValue();
        } else if (dataType.endsWith("float")) {
          type = VirtuosoTripleType.Float;
          value = l.floatValue();
        } else if (dataType.endsWith("dateTime")) {
          type = VirtuosoTripleType.DateTime;
          value = l.calendarValue();
        } else {
          type = VirtuosoTripleType.String;
          value = l.stringValue();
        }
      }
      return new VirtuosoTripleObject(type, dataType, value,
          l.getLanguage().isPresent() ? l.getLanguage().get() : "en");
    }
    return null;
  }

  public void close() {
    con.close();
  }
}
