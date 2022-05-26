package xyz.fumarase.killer.anlaiye.client;

import xyz.fumarase.killer.anlaiye.object.Address;
import xyz.fumarase.killer.anlaiye.object.Container;
import xyz.fumarase.killer.anlaiye.object.Order;
import xyz.fumarase.killer.anlaiye.object.Shop;
import xyz.fumarase.killer.anlaiye.object.base.AddressBase;
import xyz.fumarase.killer.anlaiye.object.base.OrderBase;

import java.util.List;

public class ExampleClient implements IClient {
    @Override
    public Shop getShop(int shopId) throws Exception {
        return null;
    }

    @Override
    public Container getContainer(int containerId) throws Exception {
        return null;
    }

    @Override
    public List<? extends AddressBase> getAddress() throws Exception {
        return null;
    }

    @Override
    public Long order(Order order) throws Exception {
        return null;
    }

    @Override
    public OrderBase precheck(Shop shop, List list, Address address) throws Exception {
        return null;
    }
}
