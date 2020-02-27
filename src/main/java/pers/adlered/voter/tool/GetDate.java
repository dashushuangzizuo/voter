package pers.adlered.voter.tool;

import pers.adlered.voter.dao.Vote;
import pers.adlered.voter.service.VoteService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {
    public static Integer year() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();
        return Integer.parseInt(simpleDateFormat.format(date));
    }

    /**
     * 判断两个时间是否相隔一天
     */
    public static boolean overOneDay(Date date1,Date date2){
        long  between = date1.getTime() - date2.getTime();
        if(between > (24* 3600000)){
            return true;
        }
        return false;
    }
}
