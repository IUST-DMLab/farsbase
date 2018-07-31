package ir.ac.iust.dml.kg.knowledge.proxy.access.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Dao of permissions
 */
public interface IPermissionDao {
    void write(Permission... permissions);

    void delete(Permission... permissions);

    Permission read(ObjectId id);

    Permission readByTitle(String title);

    List<Permission> readAll();

    PagingList<Permission> search(String title, int page, int pageSize);
}
