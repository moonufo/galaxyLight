package com.didispace.domain;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventProcessMapper {
  	@Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and etype =1 and  sid=0  " )
	EventProcess findEvntDone(@Param("bid") Integer bid ) ;

	//业务处理成功完成
	@Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and etype =2 or etype =6 and  sid=1  " )
	EventProcess findEvntSecond(@Param("bid") Integer bid);

	@Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and etype =3 and  sid=1  " )
	EventProcess findEvntFour(@Param("bid") Integer bid);
	
	@Select("SELECT * FROM hredo  WHERE bid =#{bid}    " )
	Hredo findEvntFive(@Param("bid") Integer bid);

	@Select("SELECT * FROM eventprocess  WHERE etype =1 and sid=0 and edate<#{edaten} and edate>=#{edates}")
	List<EventProcess> findEvntThree(@Param("edates") String edates, @Param("edaten") String edaten);

	@Select("SELECT * FROM eventprocess WHERE  bid =#{bid} and   sid=0 and etype=1")
	EventProcess findEpById(@Param("bid") Integer bid);
	@Select("SELECT * FROM eventprocess WHERE  bid =#{bid} and   sid=0  ")
	EventProcess findEp0ById(@Param("bid") Integer bid);

	@Select("SELECT * FROM eventprocess WHERE  bid =#{bid} and   sid=1   ")
	EventProcess findEpById2(@Param("bid") Integer bid);

	@Results({ @Result(property = "service", column = "service"), @Result(property = "etype", column = "etype") })
	@Select("SELECT * FROM eventprocess  WHERE bid =#{bid} and (sid<>0 or sid<>1)")
	List<EventProcess> findAll(@Param("bid") Integer bid);

	@Select("SELECT * FROM hredo  WHERE bid =#{bid}")
	Hredo findhredoId(@Param("bid") Integer bid);
	@Select("SELECT * FROM hredo  WHERE etype =2 and edate<#{edaten} and edate>=#{edates}")
	List<Hredo> findhredo(@Param("edates") String edates, @Param("edaten") String edaten);
	
	@Select("SELECT * FROM hredo  WHERE etype =1 limit #{start},  #{pagesize} order by edate desc")
	List<Hredo> findhrDoneAll (@Param("start") Integer start,@Param("pagesize") Integer pagesize);

	
	@Select("SELECT * FROM hredo  WHERE etype =2 limit #{start},  #{pagesize} order by edate desc")
	List<Hredo> findhredoAll( @Param("start") Integer start,@Param("pagesize") Integer pagesize);

	
	@Insert("INSERT INTO eventprocesslog(serviceNumber,etypec, pathParemater,eresponse,uri,method,bapp, bid,sid,service,etype,paremater,edate) "
			+ "VALUES(#{serviceNumber},#{etypec}, #{pathParemater},#{eresponse},#{uri},#{method}, #{bapp}, #{bid}, #{sid}, #{service},#{etype}, #{paremater}, #{edate})")
	int insertLog(@Param("serviceNumber") Integer serviceNumber, @Param("etypec") Integer etypec,
			@Param("pathParemater") String pathParemater, @Param("eresponse") String eresponse,
			@Param("uri") String uri, @Param("method") Integer method, @Param("bapp") String bapp,
			@Param("bid") Integer bid, @Param("sid") Integer sid, @Param("service") String service,
			@Param("etype") Integer etype, @Param("paremater") String paremater, @Param("edate") String edate);

	@Insert("INSERT INTO eventprocess(serviceNumber,etypec, pathParemater,eresponse,uri,method,bapp, bid,sid,service,etype,paremater,edate) "
			+ "VALUES(#{serviceNumber},#{etypec}, #{pathParemater},#{eresponse},#{uri},#{method}, #{bapp}, #{bid}, #{sid}, #{service},#{etype}, #{paremater}, #{edate})")
	int insert(@Param("serviceNumber") Integer serviceNumber, @Param("etypec") Integer etypec,
			@Param("pathParemater") String pathParemater, @Param("eresponse") String eresponse,
			@Param("uri") String uri, @Param("method") Integer method, @Param("bapp") String bapp,
			@Param("bid") Integer bid, @Param("sid") Integer sid, @Param("service") String service,
			@Param("etype") Integer etype, @Param("paremater") String paremater, @Param("edate") String edate);

	@Update("UPDATE hredo  SET etype=1   WHERE bid=#{bid} ")
	int updatehrdo(@Param("bid") Integer bid );

	
	@Insert("INSERT INTO hredo(  bid,etype,edate)" + "VALUES(  #{bid}, #{etype}, #{edate})")
	int insertRdo(@Param("bid") Integer bid, @Param("etype") Integer etype, @Param("edate") String edate);
	@Update("UPDATE eventprocess SET etype=#{etype} ,edate=#{edate} WHERE bid =#{bid} and sid=1")
	void updatebapp(@Param("etype") Integer etype, @Param("edate") String edate, @Param("bid") Integer bid);
	@Select("SELECT * FROM eventprocesslog  WHERE bid =#{bid}")
	List<EventProcess>  findEvntLog(@Param("bid") Integer bid);

	@Update("UPDATE eventprocess SET etype=#{etype} ,edate=#{edate} WHERE sid=#{sid}")
	void update(@Param("etype") Integer etype, @Param("edate") String edate, @Param("sid") Integer sid);

	@Update("UPDATE eventprocess SET etypec=#{etypec} ,edate=#{edate} WHERE sid=#{sid}")
	void updateComp(@Param("etypec") Integer etypec, @Param("edate") String edate, @Param("sid") Integer sid);

	// @Update("UPDATE EventProcess SET age=#{age} WHERE name=#{name}")
	// void update(EventProcess EventProcess);
	//
	// @Delete("DELETE FROM EventProcess WHERE id =#{id}")
	// void delete(Long id);
	//
	// @Insert("INSERT INTO EventProcess(name, age) VALUES(#{name}, #{age})")
	// int insertByEventProcess(EventProcess EventProcess);
	//
	// @Insert("INSERT INTO EventProcess(name, age)
	// VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER})")
	// int insertByMap(Map<String, Object> map);

}