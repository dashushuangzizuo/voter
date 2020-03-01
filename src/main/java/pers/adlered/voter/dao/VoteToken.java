package pers.adlered.voter.dao;

import java.sql.Date;

public class VoteToken {
    private Integer ID;
    private Integer VID;
    private String Token;
    private String Selection;
    private String md5_v;
    private String md5_m;
    private String crc;
//    private Date VoteDate;
    private String VoteDate;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getVID() {
        return VID;
    }

    public void setVID(Integer VID) {
        this.VID = VID;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getSelection() {
        return Selection;
    }

    public void setSelection(String selection) {
        Selection = selection;
    }

    public String getMd5_v() {
        return md5_v;
    }

    public void setMd5_v(String md5_v) {
        this.md5_v = md5_v;
    }

    public String getMd5_m() {
        return md5_m;
    }

    public void setMd5_m(String md5_m) {
        this.md5_m = md5_m;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

//    public Date getVoteDate() {
//        return VoteDate;
//    }
//
//    public void setVoteDate(Date voteDate) {
//        VoteDate = voteDate;
//    }


    public String getVoteDate() {
        return VoteDate;
    }

    public void setVoteDate(String voteDate) {
        VoteDate = voteDate;
    }
}
