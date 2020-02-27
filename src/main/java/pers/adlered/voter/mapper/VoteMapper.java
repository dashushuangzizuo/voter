package pers.adlered.voter.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import pers.adlered.voter.dao.Vote;

@Mapper
@Service
public interface VoteMapper {
    @Select("SELECT * FROM Voter_Vote where VID = #{VID}")
    Vote getVote(@Param("VID") Integer VID);

    @Select("SELECT VID FROM Voter_Vote where VID = #{VID}")
    Vote checkVote(@Param("VID") Integer VID);

    @Update("UPDATE Voter_Vote SET Selection = #{Selection} WHERE VID = #{VID}")
    int vote(@Param("Selection") String selection, @Param("VID") Integer VID);

    @Insert("INSERT INTO Voter_Vote" +
            " (Title, Describes, Selection, Type, Limits,Pass,VoteDate) VALUES" +
            " (#{title}, #{describe}, #{selection}, #{type}, #{limit}, #{pass},#{voteDate})")
    void insertVote(@Param("title") String title, @Param("describe") String describe, @Param("selection") String selection, @Param("type") Integer type, @Param("limit") Integer limit,@Param("pass") String pass,@Param("voteDate") String voteDate);

    @Select("SELECT VID FROM Voter_Vote WHERE" +
            " Title = #{title} AND Describes = #{describe} AND Selection = #{selection} AND Type = #{type} AND Limits = #{limit} AND Pass = #{pass}" +
            " ORDER BY VID DESC LIMIT 1")
    Integer queryVoteID(@Param("title") String title, @Param("describe") String describe, @Param("selection") String selection, @Param("type") Integer type, @Param("limit") Integer limit,@Param("pass") String pass);

    @Insert("INSERT INTO Voter_Vote_Token" +
            " (VID, Token) VALUES" +
            " (#{vid}, #{token})")
    void insertToken(@Param("vid") String vid, @Param("token") String token);
}
