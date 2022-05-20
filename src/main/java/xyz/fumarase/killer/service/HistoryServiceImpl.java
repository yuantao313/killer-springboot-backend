package xyz.fumarase.killer.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.model.HistoryModel;

import java.util.List;

/**
 * @author YuanTao
 */
@Service("HistoryService")
@NoArgsConstructor
public class HistoryServiceImpl implements IHistoryService {
    private HistoryMapper historyMapper;

    @Autowired
    public void setHistoryMapper(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    @Override
    public List<HistoryModel> getHistories(int page, int pageSize) {
        if(page<1||pageSize<1){
            return historyMapper.selectList(null);
        }
        QueryWrapper<HistoryModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit " + pageSize + " offset " + (page - 1) * pageSize);
        return historyMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteHistory(Integer id) {
        historyMapper.deleteById(id);
    }

}
