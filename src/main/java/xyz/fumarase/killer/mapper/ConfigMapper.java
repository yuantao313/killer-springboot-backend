package xyz.fumarase.killer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.model.ConfigModel;

/**
 * @author YuanTao
 */
@Component
@Mapper
public interface ConfigMapper extends BaseMapper<ConfigModel> {
}
