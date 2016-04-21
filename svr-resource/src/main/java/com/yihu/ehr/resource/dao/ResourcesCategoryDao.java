package com.yihu.ehr.resource.dao;


import com.yihu.ehr.dbhelper.common.DBList;
import com.yihu.ehr.resource.common.DataGridResult;
import com.yihu.ehr.resource.model.RsCategory;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by hzp on 2016/4/19.
 */
@Service("resourcesCategoryDao")
public class ResourcesCategoryDao extends BaseDao {

    /**
     * 新增资源分类
     */
    public Boolean addResourcesCategory(RsCategory obj) throws Exception{
        String sql = "insert into RS_CATEGORY(ID,NAME,PID,DESCRIPTION) values (?,?,?,?)";

        Boolean re = this.getDB().execute(sql,obj.getId(),obj.getName(),obj.getPid(),obj.getDescription());
        if(!re && this.getDB().errorMessage.length()>0)
        {
            throw new Exception(this.getDB().errorMessage);
        }
        return re;
    }

    /**
     * 修改资源分类
     */
    public Boolean editResourcesCategory(RsCategory obj) throws Exception{
        String sql = "update RS_CATEGORY set NAME=?,PID=?,DESCRIPTION=? where ID=?";

        Boolean re = this.getDB().execute(sql,obj.getName(),obj.getPid(),obj.getDescription(),obj.getId());
        if(!re && this.getDB().errorMessage.length()>0)
        {
            throw new Exception(this.getDB().errorMessage);
        }
        return re;
    }

    /**
     * 删除资源分类
     */
    public Boolean deleteResourcesCategory(String id) throws Exception{
        String sql = "delete from RS_CATEGORY where ID=?";

        Boolean re = this.getDB().execute(sql,id);
        if(!re && this.getDB().errorMessage.length()>0)
        {
            throw new Exception(this.getDB().errorMessage);
        }
        return re;
    }

    /**
     * 获取子分类
     */
    public List<RsCategory> getChildrenCategory(String pid) throws Exception
    {
        String sql = "select * from RS_CATEGORY where pid=?";
        return this.getDB().query(RsCategory.class,sql,pid);
    }

    /**
     * 获取资源分类
     */
    public DataGridResult queryResourcesCategory(String id,String name,String pid) throws Exception {
        String sql = "select * from RS_CATEGORY where 1=1";

        if(id!=null && id.length()>0)
        {
            sql += " and id='"+id+"'";
        }
        if(name!=null && name.length()>0)
        {
            sql += " and name='%"+name+"%'";
        }
        if(pid!=null && pid.length()>0)
        {
            sql += " and pid='"+pid+"'";
        }
        DBList list = this.getQE().queryBySql(sql);
        return DataGridResult.fromDBList(list);
    }

}
