package com.yihu.ehr.std.model;

import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Service;

/**
 * Created by AndyCai on 2015/12/17.
 */
@Service(Services.DispatchLogManager)
public class DispatchLogManager extends SQLGeneralDAO {

    public boolean insertDispatchLog(DispatchLog info)
    {
        boolean boolResult = true;

        try{
            saveEntity(info);
        }
        catch (Exception ex)
        {
            boolResult=false;
            LogService.getLogger(DispatchLogManager.class).error(ex.getMessage());
        }

        return boolResult;
    }
}
