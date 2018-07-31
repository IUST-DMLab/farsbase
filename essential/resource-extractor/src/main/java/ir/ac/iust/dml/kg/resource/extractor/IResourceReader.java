package ir.ac.iust.dml.kg.resource.extractor;

import java.io.IOException;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Reader interface for resource
 * implementors must open all needed connection in constructor
 * implementors must read list of entity on each call and return list of readed
 * implementors must return list with length of zero at end of read
 * implementors must close all connection in close
 */
public interface IResourceReader extends AutoCloseable {
    /**
     * Return list of entity
     *
     * @param pageSize is max entity that must read on each call
     * @return list of resources
     * @throws IOException
     */
    List<Resource> read(int pageSize) throws Exception;

    Boolean isFinished();
}
