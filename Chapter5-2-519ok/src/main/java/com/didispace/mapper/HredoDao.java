/**
 * @(#)CustomerDao.java 1.0 2014-7-26
 * @Description: 
 * 
 * Modification History:
 * Date:        2014-7-26
 * Author:      issuser
 * Version:     g4s V1.0.0
 * Description: (Initialize)
 * Reviewer:    
 * Review Date: 
 */
package com.didispace.mapper;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.didispace.web.EventProcess;
 

 
@Repository("HredoDao")
public class HredoDao implements HredoMapper {
    private static final String NAME_SPACE = "com.didispace.web.Manager";
    
    @Resource
    private SqlSession sqlSession;


    @Override
    public List<EventProcess> queryList(Map<String, ?> params) {
        List<EventProcess> list = null;
       
        list = sqlSession.selectList(NAME_SPACE.concat(".eventp_pagination_select"), params);
        return list;
    }

 

    @Override
    public int queryListCount(Map<String, ?> params) {
    	
    	String x= params.get("taskName").toString();
    	
        return (Integer) sqlSession.selectOne(NAME_SPACE.concat(".eventptotal_select"), params);
    }

    
}
