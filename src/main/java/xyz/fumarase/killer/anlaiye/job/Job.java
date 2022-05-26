package xyz.fumarase.killer.anlaiye.job;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.job.exception.OrderTimeoutException;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.job.exception.EmptyOrderException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.*;
import xyz.fumarase.killer.model.UserModel;
import xyz.fumarase.killer.reporter.Reporter;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;

import java.util.*;

import static xyz.fumarase.killer.constrant.HistoryStatusEnum.*;
import static xyz.fumarase.killer.constrant.JobModeEnum.ONCE;
import static xyz.fumarase.killer.constrant.JobModeEnum.UNTIL_SUCCESS;

/**
 * @author YuanTao
 */
@Component
@Slf4j
public class Job extends QuartzJobBean {


    private Long startTimeStamp;

    private Client client;
    private Reporter reporter;

    @Autowired
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @SneakyThrows(InterruptedException.class)
    private void wait(Shop shop, Integer timeout) throws OrderTimeoutException {
        while (!shop.isOpen()) {
            log.info("店铺未营业，0.5秒后重试");
            if (System.currentTimeMillis() - startTimeStamp >= timeout * 1000) {
                throw new OrderTimeoutException();
            }
            Thread.sleep(500);
        }
        log.info("店铺营业");
    }

    private boolean isFullySuccess(List<OrderGood> orderGoods, HashMap<String, Integer> needList) {
        int successNum = 0;
        for (Integer needNum : needList.values()) {
            successNum += needNum;
        }
        for (OrderGood orderGood : orderGoods) {
            successNum -= orderGood.getNumber();
        }
        return successNum <= 0;
    }

    @Override
    @SneakyThrows(InterruptedException.class)
    public void executeInternal(JobExecutionContext context) {
        startTimeStamp = System.currentTimeMillis();
        int jobId = context.getJobDetail().getJobDataMap().getInt("jobId");
        log.info("任务{}开始执行", jobId);
        reporter.report("任务" + jobId + "开始执行");
        JobModel jobModel = manager.getJob(jobId);
        HistoryModel historyModel = new HistoryModel();
        historyModel.setJobId(jobModel.getId());
        historyModel.setStatus(RUNNING);
        Calendar c = Calendar.getInstance();
        if (jobModel.getHour() == c.get(Calendar.HOUR_OF_DAY) && jobModel.getMinute() == c.get(Calendar.MINUTE)) {
            //todo 这样做不准确但没办法
            historyModel.setIsManual(false);
        } else {
            historyModel.setIsManual(true);
        }
        manager.addHistory(historyModel);
        UserModel user = manager.getUser(jobModel.getSource());
        Client client = new Client().setToken(user.getToken(), user.getLoginToken());
        Shop shop = manager.getShop(jobModel.getShopId());
        try {
            List<OrderGood> orderGoods = shop.order(jobModel.getBlackList(), jobModel.getNeedList());
            if (orderGoods.isEmpty()) {
                throw new EmptyOrderException();
            }
            Order order = client.precheck(shop, orderGoods, user.getAddress(jobModel.getTarget()));
            wait(shop, jobModel.getTimeout());
            Long orderId;
            do {
                orderId = client.order(order);
                if (orderId == -1L) {
                    log.info("抢购失败,1s后重试");
                } else {
                    log.info("订单号：" + orderId);
                    reporter.report("订单号：" + orderId);
                    break;
                }
                Thread.sleep(1000);
            } while (System.currentTimeMillis() - startTimeStamp < jobModel.getTimeout() * 1000);
            if (jobModel.getMode() == ONCE) {
                log.info("任务被设定为一次性任务，将任务状态暂停");
                manager.pauseJob(jobId);
            }
            if (orderId > 0) {
                historyModel.setOrderId(orderId);
                historyModel.setStatus(isFullySuccess(orderGoods, jobModel.getNeedList()) ? SUCCESS : PARTIALLY);
                if (jobModel.getMode() == UNTIL_SUCCESS) {
                    log.info("任务被设定为直到成功任务，并且本次执行成功，将任务状态暂停");
                    manager.pauseJob(jobId);
                }
            } else {
                throw new OrderTimeoutException();
            }
        } catch (TokenInvalidException e) {
            log.info("用户{}的token失效", jobModel.getSource());
            historyModel.setStatus(TOKEN_INVALID);
        } catch (EmptyOrderException e) {
            log.info("订单为空");
            historyModel.setStatus(EMPTY_ORDER);
        } catch (OrderTimeoutException e) {
            log.info("订单超时");
            historyModel.setStatus(TIMEOUT);
        } catch (ClientException e) {
            log.info("未知错误");
            historyModel.setStatus(UNKNOWN);
        }
        manager.updateHistory(historyModel);
    }
}
