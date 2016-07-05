package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.XDictClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.model.MedicationStat;
import com.yihu.ehr.profile.model.Template;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author hzp
 * @created 2016.07.02 13:49
 * 用药信息服务(待完善)
 */
@Transactional
@Service
public class MedicationStatService extends BaseJpaService<Template, XTemplateRepository> {



    /**
     * 获取用药清单（直接查mysql）
     */
    public List<MedicationStat> getMedicationStat(String demographicId) throws  Exception{
        String sql = "select m.*,d.code,d.unit,d.specifications \n" +
                "from (select drug_id,drug_name,\n" +
                "  sum(case  \n" +
                "      when medication_time > :3MonthTime then drug_num\n" +
                "      ELSE 0\n" +
                "      end) as month3,\n" +
                "  sum(case  \n" +
                "    when medication_time > :6MonthTime then drug_num\n" +
                "    ELSE 0\n" +
                "    end) as month6,\n" +
                "  max(medication_time) as last_time \n" +
                "  from RS_Medication_List\n" +
                "  where demographic_id=:demographicId\n" +
                "  group by drug_id,drug_name) m\n" +
                "left join drug_dict d on d.id = m.drug_id";

        Session session = currentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("demographicId", demographicId);
        query.setParameter("3MonthTime", "2016-04-02 17:26:16");
        query.setParameter("6MonthTime", "2016-01-02 17:26:16");
        query.addEntity(MedicationStat.class);
        List<MedicationStat> list = query.list();

        return list;

    }

}