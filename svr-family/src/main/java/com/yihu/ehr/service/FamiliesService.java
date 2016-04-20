package com.yihu.ehr.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * 用户管理接口实现类.
 *
 * @author Lyr
 * @version 1.0
 * @updated 18-4月-2016 10:30:00
 */

@Service
@Transactional
public class FamiliesService extends BaseJpaService<Families, XFamiliesRepository> {

    @Autowired
    private XFamiliesRepository familiesRep;

    @Autowired
    private XMembersRepository membersRep;

    /*
     * 获取家庭列表
     *
     * @param sorts 排序字段
     * @param page 页码
     * @param size 每页大小
     * @return Page<Families> 家庭列表
     */
    public Page<Families> getFamilies(String sorts,int page,int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return familiesRep.findAll(pageable);
    }

    /*
     * 根据家庭ID获取家庭
     *
     * @param id 家庭ID
     * @Families 家庭
     */
    public Families getFamiliesById(String id)
    {
        Families families = familiesRep.findOne(id);

        return families;
    }

    /*
     * 创建家庭
     *
     * @param families 家庭
     * @return Families 家庭
     */
    public Families createFamilies(Families families)
    {
        families.setCreateDate(new Date());
        familiesRep.save(families);

        return families;
    }

    /*
     * 更新家庭
     *
     * @param families 家庭
     */
    public void updateFamilies(Families families)
    {
        familiesRep.save(families);
    }

    /*
     * 删除家庭
     *
     * @param id 家庭ID
     */
    public void deleteFamilies(String id)
    {
        membersRep.deleteByFamilyId(id);
        familiesRep.delete(id);
    }
}