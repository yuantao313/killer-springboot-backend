package xyz.fumarase.killer.anlaiye;

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
import xyz.fumarase.killer.anlaiye.object.*;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.fumarase.killer.model.JobMode.ONCE;
import static xyz.fumarase.killer.model.JobMode.UNTIL_SUCCESS;

/**
 * @author YuanTao
 */
@Component
@Slf4j
public class Job extends QuartzJobBean {
    private Manager manager;

    private Long startTimeStamp;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    private void wait(Shop shop, Integer timeout) throws OrderTimeoutException {
        while (!shop.isOpen()) {
            log.info("店铺未营业，1秒后重试");
            if (System.currentTimeMillis() - startTimeStamp >= timeout * 1000) {
                throw new OrderTimeoutException();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("店铺营业");
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        startTimeStamp = System.currentTimeMillis();
        int jobId = context.getJobDetail().getJobDataMap().getInt("jobId");
        log.info("任务{}开始执行", jobId);
        JobModel jobModel = manager.getJob(jobId);
        if (jobModel == null || !jobModel.getEnable()) {
            log.info("任务{}已经被禁用", jobId);
            return;
        }
        HistoryModel historyModel = new HistoryModel();
        historyModel.setJobId(jobModel.getId());
        historyModel.setStatus("RUNNING");
        manager.addHistory(historyModel);
        User user = manager.getUser(jobModel.getSource());
        Shop shop = manager.getShop(jobModel.getShopId());
        try {
            HashMap<Long, OrderGood> orderGoodsMap = shop.order(jobModel.getBlackList(), jobModel.getNeedList());
            List<OrderGood> orderGoods = new ArrayList<>(orderGoodsMap.values());
            Precheck precheck = user.precheck(shop, orderGoods);
            if (precheck.getInvalidGoodId().size() > 0) {
                for (Long id : precheck.getInvalidGoodId()) {
                    orderGoodsMap.remove(id);
                }
            }
            orderGoods = new ArrayList<>(orderGoodsMap.values());
            Order order = OrderBuilder.newOrder()
                    .setAddress(user.getAddress(jobModel.getTarget()))
                    .setShop(shop)
                    .setOrderGoods(orderGoods)
                    .setDeliveryDate(precheck.getDeliveryDate())
                    .setDeliveryTime(precheck.getDeliveryTime())
                    .build();
            wait(shop, jobModel.getTimeout());
            Long orderId;
            do {
                orderId = user.order(order);
                if (orderId == -1L) {
                    log.info("抢购失败,1s后重试");
                } else {
                    log.info("订单号：" + orderId);
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (System.currentTimeMillis() - startTimeStamp < jobModel.getTimeout() * 1000);
            if (orderId > 0) {
                historyModel.setOrderId(orderId);
                int successNum = 0;
                for (OrderGood orderGood : orderGoods) {
                    successNum += orderGood.getNumber();
                }
                for (Integer needNum : jobModel.getNeedList().values()) {
                    successNum -= needNum;
                }
                if (successNum < 0) {
                    historyModel.setStatus("PARTIALLY");
                } else {
                    historyModel.setStatus("SUCCESS");
                }
                if (jobModel.getMode() == ONCE.getCode()) {
                    jobModel.setEnable(false);
                }

                if (jobModel.getMode() == UNTIL_SUCCESS.getCode()) {
                    jobModel.setEnable(false);
                }
            } else {
                throw new OrderTimeoutException();
            }
        } catch (TokenInvalidException e) {
            log.info("用户{}的token失效", jobModel.getSource());
            historyModel.setStatus("TOKEN INVALID");
        } catch (EmptyOrderException e) {
            log.info("订单为空");
            historyModel.setStatus("EMPTY ORDER");
        } catch (OrderTimeoutException e) {
            log.info("订单超时");
            historyModel.setStatus("TIMEOUT");
        } catch (ClientException e) {
            log.info("未知错误");
            historyModel.setStatus("UNKNOWN");
        }
        //status转为枚举
        manager.updateHistory(historyModel);
    }
}
