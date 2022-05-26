package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import xyz.fumarase.killer.anlaiye.object.base.OrderBase;


import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order extends OrderBase {
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
    @JsonProperty("pickUpAddr")
    private String pickUpAddr;

    public Order(Shop shop, List<OrderGood> goods, Address address) {
        this.target = "order_center";
        this.packPrice = "0.00";
        this.reductionAmount = "0.00";
        this.goods = goods;
        this.comment = "";
        this.source = "";
        this.supplierId = shop.getShopId();
        this.consigneeTelEncryption = address.getMpEncryption();
        if (shop.isSelfTake()) {
            this.orderType = 1;
            this.pickUpAddr = shop.getSelfTakeAddress();
            this.gender = null;
        } else {
            this.consignee = address.getAddresseeEncryption();
            this.gender = "女男".indexOf(address.getGender());
            this.addressId = address.getId();
            this.addressDetail = address.getDetail();
            this.addressSummary = address.getPoiAddress();
            this.addressLat = address.getLat();
            this.addressLng = address.getLon();
            this.orderType = 0;
        }
        this.deliveryDate = (new SimpleDateFormat("yyyyMMdd")).format(System.currentTimeMillis());
        this.deliveryTime = "0";
    }
}
