package ir.ac.iust.dml.kg.knowledge.proxy.web.logic;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IForwardDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logic to forward
 * have map source->destination for fast forward
 */
@Service
public class ForwardLogic {
    private Map<String, Forward> forwardMap;
    private final IForwardDao forwardDao;

    @Autowired
    public ForwardLogic(IForwardDao forwardDao) {
        this.forwardDao = forwardDao;
    }

    @PostConstruct
    public void reload() {
        final Map<String, Forward> forwardMap = new ConcurrentHashMap<>();
        forwardDao.readAll().forEach(f -> forwardMap.put(f.getSource(), f));
        this.forwardMap = forwardMap;
    }

    public Forward get(String source) {
        return forwardMap.get(source);
    }

}
