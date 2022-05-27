package xyz.fumarase.killer.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Container {
    String containerName;
    int containerId;
    List<HashMap<String,Object>> shops;
}
