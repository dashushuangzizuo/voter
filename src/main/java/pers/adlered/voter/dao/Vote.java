package pers.adlered.voter.dao;

import java.util.Date;

public class Vote {
    private Integer VID;
    private String Title;
    private String Describes;
    private String Selection;
    private Integer Type;
    private Integer Limits;
    private String Pass;
    private Date VoteDate;

    public Integer getVID() {
        return VID;
    }

    public void setVID(Integer VID) {
        this.VID = VID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSelection() {
        return Selection;
    }

    public void setSelection(String selection) {
        Selection = selection;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        Pass = pass;
    }

    public String getDescribes() {
        return Describes;
    }

    public void setDescribes(String describes) {
        Describes = describes;
    }

    public Integer getLimits() {
        return Limits;
    }

    public void setLimits(Integer limits) {
        Limits = limits;
    }

    public Date getVoteDate() {
        return VoteDate;
    }

    public void setVoteDate(Date voteDate) {
        VoteDate = voteDate;
    }
}
