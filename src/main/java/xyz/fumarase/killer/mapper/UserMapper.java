package xyz.fumarase.killer.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.model.UserModel;

/**
 * @author YuanTao
 */
@Mapper
@Component
public interface UserMapper extends BaseMapper<UserModel> {
}
