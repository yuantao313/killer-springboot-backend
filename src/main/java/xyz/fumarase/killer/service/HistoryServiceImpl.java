package xyz.fumarase.killer.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.model.HistoryModel;

import java.text.SimpleDateFormat;
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
    public List<HistoryModel> getHistories() {
        return historyMapper.selectList(null);
    }

    @Override
    public void checkHistory(Integer id) {
        HistoryModel historyModel = historyMapper.selectById(id);
        historyModel.setChecked(true);
        historyMapper.updateById(historyModel);
    }
}
