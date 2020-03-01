package pers.adlered.voter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import pers.adlered.voter.dao.Vote;
import pers.adlered.voter.dao.VoteToken;
import pers.adlered.voter.mapper.VoteMapper;
import pers.adlered.voter.mapper.VoteTokenMapper;
import pers.adlered.voter.tool.GetDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class VoteService {
    @Autowired
    VoteMapper voteMapper;
    @Autowired
    VoteTokenMapper voteTokenMapper;

    public Vote getVote(Integer VID){
        Vote vote = voteMapper.getVote(VID);
        return vote;
    }

    public List<VoteToken> getVoteToken(Integer VID,String Token){
        List<VoteToken> list = voteTokenMapper.getVoteTokenListByPass(VID,Token);
        return list;
    }

    public boolean checkDate(Integer VID){
        Date dateStart = null;
        boolean voteSt = false;
        Vote vote = getVote(VID);
        if(vote!=null){
            dateStart = vote.getVoteDate();
        }
        Date datenow = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(dateStart!=null){
            voteSt = GetDate.overOneDay(datenow,dateStart);
        }

        return voteSt;
    }


    public boolean isAdm(Integer VID,String TOKEN){
        Vote vote = getVote(VID);
        if(vote.getPass().equals(DigestUtils.md5DigestAsHex((TOKEN+"ccb2020").getBytes()))){
            return true;
        }
        return false;
    }

    public boolean isVoter(Integer VID,String TOKEN){
        List<VoteToken> voteTokenList = getVoteToken(VID,TOKEN);
        if(voteTokenList.size()>0){
            return true;
        }
        return false;
    }
}
