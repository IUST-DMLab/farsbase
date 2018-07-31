package ir.ac.iust.dml.kg.search.services.Types;

import java.util.List;

/**
 * Created by ali on 6/22/17.
 */
public class APIPropertyGroupWithTitle extends APIPropertyGroup {
    private String groupName;

    public APIPropertyGroupWithTitle(int order, List<APIPropertySingle> result, double confidence, String groupName) {
        super(order,result,confidence);
        this.groupName = groupName;
    }

    public String getgroupName() {
        return groupName;
    }
}
