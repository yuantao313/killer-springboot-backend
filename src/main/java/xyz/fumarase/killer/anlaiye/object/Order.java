package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author YuanTao
 */
@Data
public class Order {
    private String target;
    private String packPrice;
    private String reductionAmount;
    private List<OrderGood> goods;
    @JsonProperty("delivery_date")
    private String deliveryDate;
    @JsonProperty("delivery_time")
    private String deliveryTime;
    private String comment;
    private String source;
    @JsonProperty("supplier_id")
    private Integer supplierId;
    private String consignee;
    @JsonProperty("consignee_tel_encryption")
    private String consigneeTelEncryption;
    private Integer gender;
    @JsonProperty("address_id")
    private Long addressId;
    @JsonProperty("address_detail")
    private String addressDetail;
    @JsonProperty("address_summary")
    private String addressSummary;
    @JsonProperty("address_lat")
    private Double addressLat;
    @JsonProperty("address_lng")
    private Double addressLng;
    private Integer orderType;
    @JsonProperty("selfTakeAddr")
    private String selfTakeAddr;
    public Order(Shop shop, List<OrderGood> goods, Address address, String deliveryDate, String deliveryTime) {
        this.target="order_center";
        this.packPrice = "0.00";
        this.reductionAmount = "0.00";
        this.goods = goods;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.comment = "";
        this.source = "";
        this.supplierId = shop.getShopId();
        if(shop.isSelfTake()){
            this.orderType = 1;
            this.selfTakeAddr = shop.getSelfTakeAddress();
        }else{
            this.consignee = address.getAddresseeEncryption();
            this.consigneeTelEncryption = address.getMpEncryption();
            this.gender="女男".indexOf(address.getGender());
            this.addressId = address.getId();
            this.addressDetail = address.getDetail();
            this.addressSummary = address.getPoiAddress();
            this.addressLat = address.getLat();
            this.addressLng = address.getLon();
            this.orderType = 0;
        }
    }


}
