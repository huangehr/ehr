package com.yihu.ehr.dfs.fastdfs.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dfs.fastdfs.entity.FastDFS;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Repository
public class FastDFSDao {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    public ObjectNode upload(InputStream in, String fileExtension, String description) throws Exception {
        return fastDFSUtil.upload(in, fileExtension, description);
    }

    public void delete(String groupName, String remoteFileName) throws Exception {
        fastDFSUtil.delete(groupName, remoteFileName);
    }

    public void modify(){

    }

    public byte[] download(String groupName, String remoteFileName) throws Exception {
        return fastDFSUtil.download(groupName, remoteFileName);
    }
}
