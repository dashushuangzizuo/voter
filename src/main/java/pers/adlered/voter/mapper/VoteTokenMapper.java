package pers.adlered.voter.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import pers.adlered.voter.dao.Vote;
import pers.adlered.voter.dao.VoteToken;

import java.sql.Date;
import java.util.List;

@Mapper
@Service
public interface VoteTokenMapper {

    @Select("SELECT Token FROM Voter_Vote_Token WHERE" +
            " VID = #{vid}")
    List<String> queryVoteToken( @Param("vid") Integer vid);

    @Select("SELECT md5_v FROM Voter_Vote_Token WHERE" +
            " VID = #{vid}")
    List<String> queryVoteView( @Param("vid") Integer vid);

    @Select("SELECT * FROM Voter_Vote_Token WHERE" +
            " token = #{token}")
    VoteToken getVoteToken( @Param("token") String token);

    @Update("UPDATE Voter_Vote_Token SET Selection = #{Selection},token = '',md5_v=#{md5v},md5_m=#{md5m},crc=#{crc},VoteDate = #{voteDate} WHERE VID = #{VID} AND token = #{token}")
    int updateVoteToken(@Param("Selection") String selection, @Param("VID") Integer VID, @Param("token") String token, @Param("md5v") String md5v,@Param("md5m") String md5m,@Param("crc") String crc, @Param("voteDate") String voteDate);

    @Select("SELECT * FROM Voter_Vote_Token WHERE" +
            " VID = #{vid} AND token = '' order by  VoteDate desc")
    List<VoteToken> getVoteTokenList( @Param("vid") Integer vid);

    @Select("SELECT * FROM Voter_Vote_Token WHERE" +
            " VID = #{vid}")
    List<VoteToken> getVoteTokenAll( @Param("vid") Integer vid);

    @Select("SELECT * FROM Voter_Vote_Token WHERE" +
            " VID = #{vid} AND Token = #{TOKEN} order by  VoteDate desc")
    List<VoteToken> getVoteTokenListByPass( @Param("vid") Integer vid, @Param("TOKEN") String TOKEN);

    @Select("SELECT * FROM Voter_Vote_Token WHERE" +
            " VID = #{vid} AND md5_v = #{Code} order by  VoteDate desc")
    List<VoteToken> getVoteTokenListByCode( @Param("vid") Integer vid, @Param("Code") String code);

}
