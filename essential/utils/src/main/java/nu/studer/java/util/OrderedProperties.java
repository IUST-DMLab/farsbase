/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package nu.studer.java.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class OrderedProperties extends Properties {
  @Override
  public synchronized Enumeration<Object> keys() {
    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
  }
}