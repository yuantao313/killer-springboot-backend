package xyz.fumarase.killer.service;

import xyz.fumarase.killer.model.HistoryModel;

import java.util.List;

/**
 * @author YuanTao
 */
public interface IHistoryService {

    List<HistoryModel> getHistories(int page, int pageSize);

    void deleteHistory(Integer id);
}
