package xyz.fumarase.killer.anlaiye.client;

import xyz.fumarase.killer.anlaiye.object.*;
import xyz.fumarase.killer.anlaiye.object.base.AddressBase;
import xyz.fumarase.killer.anlaiye.object.base.OrderBase;

import java.util.List;

/**
 * @author YuanTao
 */
public interface IClient<O extends OrderBase> {
    /**
     * @param shopId 商家ID
     * @return 返回商家实例
     */
    Shop getShop(int shopId) throws Exception;

    /**
     * @param containerId 容器ID
     * @return 返回容器实例
     */
    Container getContainer(int containerId) throws Exception;

    /**
     * @return 返回地址列表
     */
    List<? extends AddressBase> getAddress() throws Exception;

    /**
     * @return 返回预检信息
     */
    O precheck(Shop shop, List<OrderGood> orderGoods, Address address) throws Exception;

    /**
     * @return 返回订单编号或者在失败时返回-1L
     */
    Long order(Order order) throws Exception;
}
