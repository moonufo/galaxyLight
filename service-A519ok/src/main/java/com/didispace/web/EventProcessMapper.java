package com.didispace.web;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventProcessMapper {

	 @Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and (etype =2  ) and  sid=0")
	 
	 //or etype =3
	    EventProcess  findEvntSecond( @Param("bid") Integer bid);
	   
	   @Select("SELECT * FROM eventprocess  WHERE etype =1 and sid=0 and edate<#{edaten} and edate>=#{edates}")
	    List<EventProcess> findEvntThree( @Param("edates") String edates,@Param("edaten") String edaten);
	   
	
    @Select("SELECT * FROM eventprocess WHERE  bid =#{bid} and   sid=0 and etype=1")
    EventProcess findEpById(@Param("bid") Integer bid);

    @Results({
            @Result(property = "service", column = "service"),
            @Result(property = "etype", column = "etype")
    })
    @Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and sid<>0")
    List<EventProcess> findAll(@Param("bid") Integer bid);
    
  //  @Select("SELECT * FROM hredo  WHERE etype =2 and edate<#{edaten} and edate>=#{edates}")
   // List<Hredo> findhredo( @Param("edates") String edates,@Param("edaten") String edaten);
   
    @Insert("INSERT INTO eventprocesslog(serviceNumber,etypec, pathParemater,eresponse,uri,method,bapp, bid,sid,service,etype,paremater,edate) "
    		+ "VALUES(#{serviceNumber},#{etypec}, #{pathParemater},#{eresponse},#{uri},#{method}, #{bapp}, #{bid}, #{sid}, #{service},#{etype}, #{paremater}, #{edate})")
    int insertLog(@Param("serviceNumber") Integer serviceNumber, @Param("etypec") Integer etypec, @Param("pathParemater") String pathParemater,
    		@Param("eresponse") String eresponse,@Param("uri") String uri,@Param("method") Integer method,    		
    		@Param("bapp") String bapp, @Param("bid") Integer bid,
    		@Param("sid") Integer sid,@Param("service") String service,@Param("etype") Integer etype,
    		@Param("paremater") String paremater		    		,@Param("edate") String edate);
   
    
    @Insert("INSERT INTO eventprocess(serviceNumber,etypec, pathParemater,eresponse,uri,method,bapp, bid,sid,service,etype,paremater,edate) "
    		+ "VALUES(#{serviceNumber},#{etypec}, #{pathParemater},#{eresponse},#{uri},#{method}, #{bapp}, #{bid}, #{sid}, #{service},#{etype}, #{paremater}, #{edate})")
    int insert(@Param("serviceNumber") Integer serviceNumber, @Param("etypec") Integer etypec, @Param("pathParemater") String pathParemater,
    		@Param("eresponse") String eresponse,@Param("uri") String uri,@Param("method") Integer method,    		
    		@Param("bapp") String bapp, @Param("bid") Integer bid,
    		@Param("sid") Integer sid,@Param("service") String service,@Param("etype") Integer etype,
    		@Param("paremater") String paremater		    		,@Param("edate") String edate);
    
    @Insert("INSERT INTO hredo(  bid,etype,edate)"
    		+ "VALUES(  #{bid}, #{etype}, #{edate})")    
    int insertRdo(  @Param("bid") Integer bid,
    		 @Param("etype") Integer etype   		,@Param("edate") String edate);

    @Update("UPDATE eventprocess SET etype=#{etype} ,edate=#{edate} WHERE bid =#{bid} and etype=3")
	void updatebapp(@Param("etype") Integer etype, @Param("edate") String edate, @Param("bid") Integer bid);

	@Update("UPDATE eventprocess SET etype=#{etype} ,edate=#{edate} WHERE sid=#{sid}")
	void update(@Param("etype") Integer etype, @Param("edate") String edate, @Param("sid") Integer sid);

	@Update("UPDATE eventprocess SET etypec=#{etypec} ,edate=#{edate} WHERE sid=#{sid}")
	void updateComp(@Param("etypec") Integer etypec, @Param("edate") String edate, @Param("sid") Integer sid);


}