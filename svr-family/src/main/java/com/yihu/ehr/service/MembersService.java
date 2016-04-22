package com.yihu.ehr.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户管理接口实现类.
 *
 * @author Lyr
 * @version 1.0
 * @updated 18-4月-2016 10:30:00
 */

@Service
@Transactional
public class MembersService extends BaseJpaService<Members, XMembersRepository> {

    @Autowired
    private XMembersRepository membersRep;

    /*
     * 获取家庭成员列表
     *
     * @param sorts 排序字段
     * @param page 页码
     * @param size 每页大小
     * @return Page<Members> 家庭成员列表
     */
    public Page<Members> getMembers(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return membersRep.findAll(pageable);
    }

    /*
     * 根据家庭成员ID获取成员
     *
     * @param id 家庭成员ID
     * @Families 家庭成员
     */
    public Members getMembers(String familyId,String idCardNo)
    {
        Members members = membersRep.findByFamilyIdAndIdCardNo(familyId,idCardNo);

        return members;
    }

    /*
     * 根据家庭ID获取家庭成员
     *
     * @param familyId 家庭ID
     * @return Page<Members> 家庭成员列表
     */
    public List<Members> getMembersByFamilyId(String familyId)
    {
        List<Members> members = membersRep.findByFamilyId(familyId);

        return members;
    }

    /*
     * 创建家庭成员
     *
     * @param families 家庭成员
     * @return Families 家庭成员
     */
    public Members createMembers(Members members)
    {
        members.setCreateDate(new Date());
        membersRep.save(members);

        return members;
    }

    /*
     * 更新家庭成员
     *
     * @param families 家庭成员
     */
    public void updateMembers(Members members)
    {
        membersRep.save(members);
    }

    /*
     * 删除家庭成员
     *
     * @param id 家庭成员ID
     */
    public void deleteMembers(String familyId,String idCardNo)
    {
        membersRep.deleteByFamilyIdAndIdCardNo(familyId,idCardNo);
    }

    /*
     * 根据家庭ID删除家庭成员
     *
     * @param familyId 家庭ID
     */
    public void deleteMembersByFamilyId(String familyId)
    {
        membersRep.deleteByFamilyId(familyId);
    }
}