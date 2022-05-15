package xyz.fumarase.killer.anlaiye.object;

import xyz.fumarase.killer.model.Model;

/**
 * @author YuanTao
 */
public interface IToModel<T extends Model> {
    public T toModel();
}
