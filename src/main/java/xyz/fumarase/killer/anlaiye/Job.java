package xyz.fumarase.killer.anlaiye;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.EmptyOrderException;
import xyz.fumarase.killer.anlaiye.client.exception.OrderTimeoutException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;

/**
 * @author YuanTao
 */
@Component
@Slf4j
public class Job extends QuartzJobBean {
    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    private HistoryMapper historyMapper;

    @Autowired
    public void setHistoryMapper(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int jobId = context.getJobDetail().getJobDataMap().getInt("jobId");
        log.info("任务{}开始执行", jobId);
        JobModel jobModel = manager.getJob(jobId);
        HistoryModel historyModel = new HistoryModel();
        historyModel.setJobId(jobModel.getId());
        historyModel.setStatus("RUNNING");
        /*todo 此功能需要重新实现
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if ("trigJob".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(true);
        } else if ("executeInternal".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(false);
        }*/
        historyMapper.insert(historyModel);
        UpdateWrapper<HistoryModel> uw = (new UpdateWrapper<>());
        uw.eq("id", historyModel.getId());
        User user = manager.getUser(jobModel.getSource());
        try {
            long orderId = user.setTimeout(jobModel.getTimeout())
                    .setShopId(jobModel.getShopId())
                    .avoid(jobModel.getBlackList())
                    .need(jobModel.getNeedList())
                    .setTarget(jobModel.getTarget())
                    .waitForShop()
                    .run();
            if (orderId > 0) {
                historyMapper.update(historyModel, uw.set("order_id", orderId));
                historyMapper.update(historyModel, uw.set("status", "SUCCESS"));
            } else {
                historyMapper.update(historyModel, uw.set("status", "UNKNOWN"));
            }
        } catch (TokenInvalidException e) {
            historyMapper.update(historyModel, uw.set("status", "INVALID TOKEN"));
        } catch (EmptyOrderException e) {
            historyMapper.update(historyModel, uw.set("status", "ORDER EMPTY"));
        } catch (OrderTimeoutException e) {
            historyMapper.update(historyModel, uw.set("status", "TIMEOUT"));
        } catch (ClientException e) {
            historyMapper.update(historyModel, uw.set("status", "UNKNOWN"));
        }
        //这里，也要考虑，使用enum
    }
}
