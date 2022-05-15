package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import org.quartz.*;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.model.JobModel;

import java.util.HashMap;

@Data
public class BaseManager {
    protected HashMap<Long, User> users;
    protected final static JsonMapper jsonMapper = new JsonMapper();

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public void runJob(JobModel job) {
        // todo 解耦
        users.get(job.getSource()).setShop(job.getShopId())
                .setTarget(job.getTarget())
                .avoid(job.getBlackList())
                .need(job.getNeedList())
                .waitForShop()
                .run();
    }
}
