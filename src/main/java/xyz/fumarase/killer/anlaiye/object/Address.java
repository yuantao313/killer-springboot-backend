package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import xyz.fumarase.killer.anlaiye.crypto.Phone;
import xyz.fumarase.killer.anlaiye.object.base.AddressBase;

/**
 * @author YuanTao
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address extends AddressBase {
    @JsonProperty(value = "addressee_encryption", access = JsonProperty.Access.WRITE_ONLY)
    String addresseeEncryption;
    @JsonProperty(value = "mp_encryption")
    String mpEncryption;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String gender;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Long id;
    @JsonProperty(value="poi_address", access = JsonProperty.Access.WRITE_ONLY)
    String poiAddress;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String detail;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Double lat;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Double lon;

    @JsonValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long mpDecryption(){
        return Long.valueOf(Phone.decrypt(mpEncryption));
    }
}
