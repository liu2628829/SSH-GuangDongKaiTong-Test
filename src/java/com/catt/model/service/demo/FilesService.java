package com.catt.model.service.demo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/22 0022.
 */
public interface FilesService {
    public List getFilesList(Map map);

    public int addFiles(Map map);

    public int editFiles(Map map);

    public int deleteFiles(String filesId);
}
