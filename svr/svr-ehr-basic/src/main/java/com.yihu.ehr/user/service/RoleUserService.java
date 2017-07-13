package com.yihu.ehr.user.service;

import com.yihu.ehr.apps.service.XUserAppRepository;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XRoleUserRepository;
import com.yihu.ehr.user.dao.XRolesRepository;
import com.yihu.ehr.user.entity.RoleUser;
import com.yihu.ehr.user.entity.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RoleUserService extends BaseJpaService<RoleUser,XRoleUserRepository> {
    @Autowired
    private XRoleUserRepository roleUserRepository;
    @Autowired
    private XRolesRepository rolesRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private XUserAppRepository userAppRepository;

    public Page<RoleUser> getRoleUserList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleUserRepository.findAll(pageable);
    }

    public RoleUser findRelation(String userId,long roleId){
        return roleUserRepository.findRelation(userId, roleId);
    }

    public boolean batchDeleteRoleUserRelation(String userId,String roleIds) throws Exception{
        List<RoleUser> roleUsers = this.search("userId=" + userId + ",roleId=" + roleIds);
        for(RoleUser roleUser : roleUsers){
            delete(roleUser.getId());
            userAppRepository.deleteByUserId(roleUser.getUserId());
        }
        return true;
    }

    public boolean batchCreateRoleUsersRelation(String userId,String roleIds){
        for(String roleId : roleIds.split(",")){
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(Long.parseLong(roleId));
            roleUser = save(roleUser);
            Roles roles = rolesRepository.findOne(roleUser.getRoleId());
            String user_id = roleUser.getUserId();
            //应用角色添加人员
            String sql = "SELECT DISTINCT ra.app_id,a.name app_name,u.id,u.real_name user_name,u.organization org_id,o.short_name org_name " +
                    "from roles ra " +
                    "LEFT JOIN apps a on ra.app_id = a.id " +
                    "LEFT JOIN role_user ru on ra.id = ru.role_id " +
                    "LEFT JOIN users u on u.id = '"+user_id+"'" +
                    "LEFT JOIN organizations o on u.organization = o.org_code " +
                    "WHERE ra.app_id = '"+roles.getAppId()+"'";
            String sql2 = "select id " +
                    "from user_app " +
                    "where ";
            String sqlUpd = "update user_app set status = 0 where ";
            String sqlInsert = "insert into user_app (app_id,app_name,user_id,user_name,org_id,org_name,status) values ";
            List<Map<String,Object>> listTemp = null;
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            for(Map<String,Object> map:list){
                String app_id = map.get("app_id").toString();
                String app_name = map.get("app_name").toString();
                String user_name = map.get("user_name").toString();
                String org_id = map.get("org_id")==null?"":map.get("org_id").toString();
                String org_name = map.get("org_name")==null?"":map.get("org_name").toString();

                String sqlCon = " user_id = '" +user_id +"' and app_id = '"+app_id+"'";
                listTemp = jdbcTemplate.queryForList(sql2+sqlCon);
                if(listTemp.size()>0){
                    //伪删除机制，所以新增时需判断数据是否存在，如果存在只需更新状态
                    jdbcTemplate.execute(sqlUpd+sqlCon);
                }else{
                    String insert = "('"+app_id+"','"+app_name+"','"+user_id+"','"+user_name+"','"+org_id+"','"+org_name+"',0)";
                    jdbcTemplate.execute(sqlInsert+insert);
                }
            }
        }
        return true;
    }

    public boolean batchUpdateRoleUsersRelation(String userId,String roleIds) throws Exception {
        List<RoleUser> roleUsers = search("userId=" + userId + ";roleId<>" + roleIds);
        for (RoleUser roleUser : roleUsers) {
            String userIds = roleUser.getUserId();
            //应用角色添加人员
            String sql = "SELECT ra.app_id " +
                    "from role_app_relation ra " +
                    "LEFT JOIN role_user ru on ra.role_id = ru.role_id " +
                    "WHERE ra.role_id = '"+roleUser.getRoleId()+"' and ru.user_id = '"+userIds+"'";
            String sqlUpd = "update user_app set status = 1 where ";
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            for(Map<String,Object> map:list){
                String app_id = map.get("app_id").toString();
                String sqlCon = " user_id = '" +userIds +"' and app_id = '"+app_id+"'";
                //伪删除机制，所以新增时需判断数据是否存在，如果存在只需更新状态
                jdbcTemplate.execute(sqlUpd+sqlCon);
            }
            delete(roleUser.getId());
        }
        List<RoleUser> roleUserList = search("userId=" + userId + ";roleId=" + roleIds);
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < roleUserList.size(); i++) {
            ids.add(roleUserList.get(i).getRoleId());
        }
        for (String roleId : roleIds.split(",")) {
            if (ids.contains(Long.parseLong(roleId))) {
                continue;
            }
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(Long.parseLong(roleId));
            roleUser = save(roleUser);
            Roles roles = rolesRepository.findOne(roleUser.getRoleId());
            String user_id = roleUser.getUserId();
            //应用角色添加人员
            String sql = "SELECT DISTINCT ra.app_id,a.name app_name,u.id,u.real_name user_name,u.organization org_id,o.short_name org_name " +
                    "from roles ra " +
                    "LEFT JOIN apps a on ra.app_id = a.id " +
                    "LEFT JOIN role_user ru on ra.id = ru.role_id " +
                    "LEFT JOIN users u on u.id = '"+user_id+"'" +
                    "LEFT JOIN organizations o on u.organization = o.org_code " +
                    "WHERE ra.app_id = '"+roles.getAppId()+"'";
            String sql2 = "select id " +
                    "from user_app " +
                    "where ";
            String sqlUpd = "update user_app set status = 0 where ";
            String sqlInsert = "insert into user_app (app_id,app_name,user_id,user_name,org_id,org_name,status) values ";
            List<Map<String,Object>> listTemp = null;
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            for(Map<String,Object> map:list){
                String app_id = map.get("app_id").toString();
                String app_name = map.get("app_name").toString();
                String user_name = map.get("user_name").toString();
                String org_id = map.get("org_id")==null?"":map.get("org_id").toString();
                String org_name = map.get("org_name")==null?"":map.get("org_name").toString();

                String sqlCon = " user_id = '" +user_id +"' and app_id = '"+app_id+"'";
                listTemp = jdbcTemplate.queryForList(sql2+sqlCon);
                if(listTemp.size()>0){
                    //伪删除机制，所以新增时需判断数据是否存在，如果存在只需更新状态
                    jdbcTemplate.execute(sqlUpd+sqlCon);
                }else{
                    String insert = "('"+app_id+"','"+app_name+"','"+user_id+"','"+user_name+"','"+org_id+"','"+org_name+"',0)";
                    jdbcTemplate.execute(sqlInsert+insert);
                }
            }
        }
        return true;
    }

    /**
     * 根据角色组id,人员Id删除角色组人员
     * @param roleUser
     */
    public void deleteRoleUser(RoleUser roleUser){
        Roles roles = rolesRepository.findOne(roleUser.getRoleId());
//        if("0".equals(roles.getType())){
            String userId = roleUser.getUserId();
            //应用角色添加人员
            String sql = "SELECT ra.app_id " +
                    "from role_app_relation ra " +
                    "LEFT JOIN role_user ru on ra.role_id = ru.role_id " +
                    "WHERE ra.role_id = '"+roleUser.getRoleId()+"' and ru.user_id = '"+userId+"'";
            String sqlUpd = "update user_app set status = 1 where ";
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            for(Map<String,Object> map:list){
                String app_id = map.get("app_id").toString();
                String sqlCon = " user_id = '" +userId +"' and app_id = '"+app_id+"'";
                //伪删除机制，所以新增时需判断数据是否存在，如果存在只需更新状态
                jdbcTemplate.execute(sqlUpd+sqlCon);
            }
//        }
        delete(roleUser);
    }

    /**
     * 为角色组配置人员，单个
     * @param roleUser
     * @return
     */
    public RoleUser createRoleUser(RoleUser roleUser){
        Roles roles = rolesRepository.findOne(roleUser.getRoleId());
//        if("0".equals(roles.getType())){
            String user_id = roleUser.getUserId();
            //应用角色添加人员
            String sql = "SELECT DISTINCT ra.app_id,a.name app_name,u.id,u.real_name user_name,u.organization org_id,o.short_name org_name " +
                    "from roles ra " +
                    "LEFT JOIN apps a on ra.app_id = a.id " +
                    "LEFT JOIN role_user ru on ra.id = ru.role_id " +
                    "LEFT JOIN users u on u.id = '"+user_id+"'" +
                    "LEFT JOIN organizations o on u.organization = o.org_code " +
                    "WHERE ra.app_id = '"+roles.getAppId()+"'";
            String sql2 = "select id " +
                    "from user_app " +
                    "where ";
            String sqlUpd = "update user_app set status = 0 where ";
            String sqlInsert = "insert into user_app (app_id,app_name,user_id,user_name,org_id,org_name,status) values ";
            List<Map<String,Object>> listTemp = null;
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            for(Map<String,Object> map:list){
                String app_id = map.get("app_id").toString();
                String app_name = map.get("app_name").toString();
                String user_name = map.get("user_name").toString();
                String org_id = map.get("org_id")==null?"":map.get("org_id").toString();
                String org_name = map.get("org_name")==null?"":map.get("org_name").toString();

                String sqlCon = " user_id = '" +user_id +"' and app_id = '"+app_id+"'";
                listTemp = jdbcTemplate.queryForList(sql2+sqlCon);
                if(listTemp.size()>0){
                    //伪删除机制，所以新增时需判断数据是否存在，如果存在只需更新状态
                    jdbcTemplate.execute(sqlUpd+sqlCon);
                }else{
                    String insert = "('"+app_id+"','"+app_name+"','"+user_id+"','"+user_name+"','"+org_id+"','"+org_name+"',0)";
                    jdbcTemplate.execute(sqlInsert+insert);
                }
            }
//        }

        return save(roleUser);
    }

    /**
     * 居民信息-角色授权-角色组保存
     * @param roleUserlist
     * @param userId
     */
    public String saveRoleUser(List<RoleUser> roleUserlist,String userId) {
        String id="";
        roleUserRepository.deleteByUserId(userId);
        userAppRepository.deleteByUserId(userId);
        if(roleUserlist.size()>0){
            for(RoleUser r:roleUserlist){
                id=String.valueOf(roleUserRepository.save(r).getId()) ;
                Roles roles = rolesRepository.findOne(r.getRoleId());
                String user_id = r.getUserId();
                //应用角色添加人员
                String sql = "SELECT DISTINCT ra.app_id,a.name app_name,u.id,u.real_name user_name,u.organization org_id,o.short_name org_name " +
                        "from roles ra " +
                        "LEFT JOIN apps a on ra.app_id = a.id " +
                        "LEFT JOIN role_user ru on ra.id = ru.role_id " +
                        "LEFT JOIN users u on u.id = '"+user_id+"'" +
                        "LEFT JOIN organizations o on u.organization = o.org_code " +
                        "WHERE ra.app_id = '"+roles.getAppId()+"'";
                /*String sql2 = "select id " +
                        "from user_app " +
                        "where ";*/
//                String sqlUpd = "update user_app set status = 0 where ";
                String sqlInsert = "insert into user_app (app_id,app_name,user_id,user_name,org_id,org_name,status) values ";
                List<Map<String,Object>> listTemp = null;
                List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
                for(Map<String,Object> map:list){
                    String app_id = map.get("app_id").toString();
                    String app_name = map.get("app_name").toString();
                    String user_name = map.get("user_name").toString();
                    String org_id = map.get("org_id")==null?"":map.get("org_id").toString();
                    String org_name = map.get("org_name")==null?"":map.get("org_name").toString();

//                    String sqlCon = " user_id = '" +user_id +"' and app_id = '"+app_id+"'";
//                    listTemp = jdbcTemplate.queryForList(sql2+sqlCon);
                    String insert = "('" + app_id + "','" + app_name + "','" + user_id + "','" + user_name + "','" + org_id + "','" + org_name + "',0)";
                    jdbcTemplate.execute(sqlInsert + insert);
                }
            }
        } else {
            id = "1";
        }
        return id;
    }
}
