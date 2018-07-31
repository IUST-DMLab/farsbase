package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1;

import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IForwardDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IPermissionDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.UrnMatching;
import ir.ac.iust.dml.kg.knowledge.proxy.web.logic.ForwardLogic;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.ForwardData;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.PermissionData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jws.WebService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Impl {@link IUserServices}
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.IForwardService")
public class ForwardServiceImpl implements IForwardService {
    @Autowired
    private ForwardLogic forwardLogic;
    @Autowired
    private IForwardDao forwardDao;
    @Autowired
    private IPermissionDao permissionDao;


    @Override
    public List<String> login() {
        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        final List<String> result = new ArrayList<>();
        authorities.forEach(a -> result.add(a.getAuthority()));
        return result;
    }

    @Override
    public Permission permission(@Valid PermissionData data) {
        final Permission permission = data.getIdentifier() == null ? new Permission() : permissionDao.read(new ObjectId(data.getIdentifier()));
        if (permission == null) return null;
        final Permission oldPermission = permissionDao.readByTitle(data.getTitle());
        if (oldPermission != null && !oldPermission.getIdentifier().equals(permission.getIdentifier()))
            throw new RuntimeException("Title can not be repeated");
        data.fill(permission);
        permissionDao.write(permission);
        return permission;
    }

    @Override
    public List<Permission> permissions() {
        return permissionDao.readAll();
    }


    @Override
    public List<Forward> forwards() {
        return forwardDao.readAll();
    }

    @Override
    public ForwardData edit(@Valid ForwardData data) {
        final Forward forward = data.getIdentifier() == null ? new Forward() : forwardDao.read(new ObjectId(data.getIdentifier()));
        if (forward == null)
            throw new RuntimeException("Not found");
        final Forward oldForward = forwardDao.readBySource(data.getSource());
        if (oldForward != null && !oldForward.getIdentifier().equals(forward.getIdentifier()))
            throw new RuntimeException("Title can not be repeated");
        data.fill(forward);
        forward.getPermissions().clear();
        data.getPermissions().forEach(dp -> {
            final Permission per = permissionDao.readByTitle(dp);
            if (per != null)
                forward.getPermissions().add(per);
        });
        assert data.getUrns().size() == forward.getUrns().size();
        for (int i = 0; i < data.getUrns().size(); i++) {
            final Set<String> permissions = data.getUrns().get(i).getPermissions();
            final UrnMatching urnMatching = forward.getUrns().get(i);
            permissions.forEach(dp -> {
                final Permission per = permissionDao.readByTitle(dp);
                if (per != null)
                    urnMatching.getPermissions().add(per);
            });
        }
        forwardDao.write(forward);
        forwardLogic.reload();
        return data.sync(forward);
    }
}
