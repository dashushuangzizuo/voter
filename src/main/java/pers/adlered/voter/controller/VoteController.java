package pers.adlered.voter.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pers.adlered.voter.analyzer.Selection;
import pers.adlered.voter.analyzer.Serialize;
import pers.adlered.voter.dao.Vote;
import pers.adlered.voter.dao.VoteDetail;
import pers.adlered.voter.dao.VoteToken;
import pers.adlered.voter.limit.IpUtil;
import pers.adlered.voter.limit.LoadingCacheServiceImpl;
import pers.adlered.voter.mapper.VoteDetailMapper;
import pers.adlered.voter.mapper.VoteMapper;
import pers.adlered.voter.mapper.VoteTokenMapper;
import pers.adlered.voter.service.VoteService;
import pers.adlered.voter.tool.CommonUtils;
import pers.adlered.voter.tool.GetDate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
//import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.abs;

@Controller
public class VoteController {
    @Autowired
    VoteMapper voteMapper;
    @Autowired
    VoteTokenMapper voteTokenMapper;
    @Autowired
    VoteDetailMapper voteDetailMapper;
    @Resource
    LoadingCacheServiceImpl loadingCacheService;
    @Resource
    VoteService voteService;

    @RequestMapping("/vote/en/{VID}")
    public ModelAndView showVoteEN(@PathVariable Integer VID) {
        Vote vote = voteMapper.getVote(VID);
        ModelAndView modelAndView = new ModelAndView("/vote/index_en");
        modelAndView.addObject("VoteID", vote.getVID());
        modelAndView.addObject("Title", vote.getTitle());
        modelAndView.addObject("Describe", vote.getDescribes());
        modelAndView.addObject("Type", vote.getType());
        modelAndView.addObject("Limit", vote.getLimits());
        //Selection process
        List<Map<String, String>> selects = Selection.analyze(vote.getSelection());
        modelAndView.addObject("Selection", selects);
        modelAndView.addObject("YEAR", GetDate.year());
        return modelAndView;
    }

    @RequestMapping("/vote/cn/{VID}/{TOKEN}")
    public ModelAndView showVoteCN(@PathVariable Integer VID,@PathVariable String TOKEN) {
        // todo 身份验证
        boolean flag = voteService.isVoter(VID,TOKEN);
        if(!flag){
            return new ModelAndView("/index");
        }
        Vote vote = voteMapper.getVote(VID);
        ModelAndView modelAndView = new ModelAndView("/vote/index_cn");
        modelAndView.addObject("VoteID", vote.getVID());
        modelAndView.addObject("Title", vote.getTitle());
        modelAndView.addObject("Describe", vote.getDescribes());
        modelAndView.addObject("Type", vote.getType());
        modelAndView.addObject("Limit", vote.getLimits());
        modelAndView.addObject("Token",TOKEN);
        //Selection process
        List<Map<String, String>> selects = Selection.analyze(vote.getSelection());
        modelAndView.addObject("Selection", selects);
        modelAndView.addObject("YEAR", GetDate.year());
        return modelAndView;
    }

    @RequestMapping("/vote/adm/{VID}/{TOKEN}")
    public ModelAndView showVoteAdm(@PathVariable Integer VID,@PathVariable String TOKEN) {
        //todo 身份验证
        boolean flag = voteService.isAdm(VID,TOKEN);
        if(!flag){
            return new ModelAndView("/index");
        }

        int voteAll = 0;
        int voted = 0;
        int unVote = 0;
        String voteStatus="";//投票进行中 投票完成未过期  投票活动已过期
        String voteStartDate = "";
        Date dateStart = null;
        boolean voteSt = false;

        Vote vote = voteMapper.getVote(VID);
        if(vote!=null){
//            voteStartDate = vote.getVoteDate().toString();
            dateStart = vote.getVoteDate();
        }
        Date datenow = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(dateStart!=null){
            voteSt = GetDate.overOneDay(datenow,dateStart);
        }

        String date = format.format(datenow);
        List<VoteToken> list = voteTokenMapper.getVoteTokenList(VID);
        if(vote!=null){
            String selectionSerial = vote.getSelection();
            //Package and readout
            List<Map<String, String>> selects = Selection.analyze(selectionSerial);
            for(VoteToken vt : list){
                StringBuffer sb = new StringBuffer();
                String str[] = vt.getSelection().split(",");
                for (String s : str) {
                    for(int i = 0;i<selects.size();i++){
                        if(selects.get(i).get("num").equals(s)){
                            if(sb.length()>0){
                                sb.append(",");
                            }
                            sb.append(selects.get(i).get("selectionText"));
                        }
                    }
                }
                vt.setSelection(sb.toString());
                int len = vt.getMd5_v().length();
                if(len>16){
                    vt.setMd5_v("*"+vt.getMd5_v().substring(len-10,len));
                }
            }
        }

        List<VoteToken> tokenList = voteTokenMapper.getVoteTokenAll(VID);
        if(tokenList!=null){
            voteAll = tokenList.size();
        }
        for(int i = 0;i<tokenList.size();i++){
            if(tokenList.get(i).getToken().equals("")){
                voted++;
            }else{
                unVote++;
            }
        }
        if(voteSt){
            voteStatus = "（投票活动已过期）";
        }else if(voted == voteAll){
            voteStatus = "（投票完成未过期）";
        }else{
            voteStatus = "（投票进行中）";
        }
        ModelAndView modelAndView = new ModelAndView("/vote/index_adm");
        modelAndView.addObject("VoteID", vote.getVID());
        modelAndView.addObject("Title", vote.getTitle());
        modelAndView.addObject("Describe", vote.getDescribes());
        modelAndView.addObject("Type", vote.getType());
        modelAndView.addObject("Limit", vote.getLimits());
        modelAndView.addObject("VoteList", list);
        modelAndView.addObject("TokenList", tokenList);
        modelAndView.addObject("NowDate", date);// 当前时间

        modelAndView.addObject("VoteStatus", voteStatus);//投票状态  投票进行中 投票结束
        modelAndView.addObject("Collection", "总投票人数："+voteAll+" 已投票人数："+voted+" 未投票人数："+unVote);//汇总   总投票人+已投票人+未投票人
        //Selection process
        List<Map<String, String>> selects = Selection.analyze(vote.getSelection());
        modelAndView.addObject("Selection", selects);
        modelAndView.addObject("YEAR", GetDate.year());
        return modelAndView;
    }

    @RequestMapping("/vote/view/{VID}/{TOKEN}")
    public ModelAndView showVoteView(@PathVariable Integer VID,@PathVariable String TOKEN) {
        // todo  口令验证
        if(!voteService.isCode(VID,TOKEN)){
            return new ModelAndView("/index");
        }
        int voteAll = 0;
        int voted = 0;
        int unVote = 0;
        String voteStatus="";//投票进行中 投票完成未过期  投票活动已过期
        String voteStartDate = "";
        Date dateStart = null;
        boolean voteSt = false;
        Vote vote = voteMapper.getVote(VID);
        if(vote!=null){
//            voteStartDate = vote.getVoteDate().toString();
            dateStart = vote.getVoteDate();
        }
        Date datenow = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(dateStart!=null){
            voteSt = GetDate.overOneDay(datenow,dateStart);
        }

        String date = format.format(datenow);
        List<VoteToken> list = voteTokenMapper.getVoteTokenList(VID);
        if(vote!=null){
            String selectionSerial = vote.getSelection();
            //Package and readout
            List<Map<String, String>> selects = Selection.analyze(selectionSerial);
            for(VoteToken vt : list){
                StringBuffer sb = new StringBuffer();
                String str[] = vt.getSelection().split(",");
                for (String s : str) {
                    for(int i = 0;i<selects.size();i++){
                        if(selects.get(i).get("num").equals(s)){
                            if(sb.length()>0){
                                sb.append(",");
                            }
                            sb.append(selects.get(i).get("selectionText"));
                        }
                    }
                }
                vt.setSelection(sb.toString());
                int len = vt.getMd5_v().length();
                if(len>16){
                    vt.setMd5_v("*"+vt.getMd5_v().substring(len-10,len));
                }
            }
        }
        List<VoteToken> tokenList = voteTokenMapper.getVoteTokenAll(VID);
        if(tokenList!=null){
            voteAll = tokenList.size();
        }
        for(int i = 0;i<tokenList.size();i++){
            if(tokenList.get(i).getToken().equals("")){
                voted++;
            }else{
                unVote++;
            }
        }
        if(voteSt){
            voteStatus = "（投票活动已过期）";
        }else if(voted == voteAll){
            voteStatus = "（投票完成未过期）";
        }else{
            voteStatus = "（投票进行中）";
        }
        ModelAndView modelAndView = new ModelAndView("/vote/index_view");
        modelAndView.addObject("VoteID", vote.getVID());
        modelAndView.addObject("Title", vote.getTitle());
        modelAndView.addObject("Describe", vote.getDescribes());
        modelAndView.addObject("Type", vote.getType());
        modelAndView.addObject("Limit", vote.getLimits());
        modelAndView.addObject("VoteList", list);
        modelAndView.addObject("TokenList", tokenList);
        modelAndView.addObject("NowDate", date);// 当前时间

        modelAndView.addObject("VoteStatus", voteStatus);//投票状态  投票进行中 投票结束
        modelAndView.addObject("Collection", "总投票人数："+voteAll+" 已投票人数："+voted+" 未投票人数："+unVote);//汇总   总投票人+已投票人+未投票人
        //Selection process
        List<Map<String, String>> selects = Selection.analyze(vote.getSelection());
        modelAndView.addObject("Selection", selects);
        modelAndView.addObject("YEAR", GetDate.year());
        return modelAndView;
    }

    @RequestMapping("/getVoteList")
    @ResponseBody
    public List<VoteToken> getVoteList(Integer voteID,String token) {
        try {
            Vote v = voteMapper.getVote(voteID);
            List<String> vt = voteTokenMapper.queryVoteToken(voteID);
            List<VoteToken> list = voteTokenMapper.getVoteTokenList(voteID);
            if(token == null||token.trim() == ""){
                return new ArrayList<>();
            }
            String md5 = DigestUtils.md5DigestAsHex((token+"ccb2020").getBytes());
            if(v!=null){
                if(v.getPass().equals(md5)){
                    return list;
                }
                if(vt.contains(token)){
                    return list;
                }
                return new ArrayList<>();//口令无效
            }
            return new ArrayList<>();//查无此ID
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    @RequestMapping("/getVoteDetail")
    @ResponseBody
    public List<VoteDetail> getVoteDetail(Integer voteID){
        List<VoteDetail> list = voteDetailMapper.getVoteDetail(voteID);
        Vote vote = voteMapper.getVote(voteID);
        String selectionSerial = vote.getSelection();
        //Package and readout
        List<Map<String, String>> selects = Selection.analyze(selectionSerial);
        for(VoteDetail detail : list){
            String str = detail.getSno().toString();
                for(int i = 0;i<selects.size();i++){
                    if(selects.get(i).get("num").equals(str)){
                        detail.setSname(selects.get(i).get("selectionText"));
                    }
                }
        }
        return list;
    }

    @RequestMapping("/vote/end/{code}")
    public ModelAndView showVoteEnd(@PathVariable String code) {
//        VoteToken vt = voteTokenMapper.getVoteToken(token);
        // 根据token  VID selection VoteDate 生成md5_v
        ModelAndView modelAndView = new ModelAndView("/vote/index_end");
        modelAndView.addObject("YEAR", GetDate.year());
        modelAndView.addObject("Code",code);
//        modelAndView.addObject("VoteID", vote.getVID());
//        modelAndView.addObject("Title", vote.getTitle());
//        modelAndView.addObject("Describe", vote.getDescribe());
//        modelAndView.addObject("Type", vote.getType());
//        modelAndView.addObject("Limit", vote.getLimit());
//        //Selection process
//        List<Map<String, String>> selects = Selection.analyze(vote.getSelection());
//        modelAndView.addObject("Selection", selects);
//        modelAndView.addObject("YEAR", GetDate.year());
        return modelAndView;
    }

    @RequestMapping("/vote/token")
    @ResponseBody
    public ModelAndView showVoteToken(Integer VID,String token) {
//        VoteToken vt = voteTokenMapper.getVoteToken(token);
        // 根据token  VID selection VoteDate 生成md5_v
        List<VoteToken> list = voteTokenMapper.getVoteTokenAll(VID);
        ModelAndView modelAndView = new ModelAndView("/vote/index_token");
        modelAndView.addObject("TokenList", list);
        return modelAndView;
    }

//    @RequestMapping("/checkVoteID/{VID}")
//    @ResponseBody
//    public Integer checkVoteID(@PathVariable Integer VID) {
//        try {
//            voteMapper.checkVote(VID).getVID();
//            return 1;
//        } catch (Exception e) {
//            return 0;
//        }
//    }
    @RequestMapping("/checkVoteID")
    @ResponseBody
    public Integer checkVoteID(Integer voteID,String token) {
        try {
            if(token == null||token.trim().equals("")){
                return 3;//口令无效
            }
            Vote v = voteMapper.getVote(voteID);
            if(v==null){
                return 0;//查无此ID
            }
            if(voteService.isAdm(voteID,token)){//管理员
                return 2;//管理员
            }else if(voteService.isVoter(voteID,token)){//投票口令  （是否过期）
//                List<String> vt = voteTokenMapper.queryVoteToken(voteID);
                boolean status = voteService.checkDate(voteID);
                if(status){
                    return 5;//投票过期，无法投票
                }
                return 1;//投票人
            }else if(voteService.isCode(voteID,token)){//查看码
                boolean status = voteService.checkDate(voteID);
                boolean voteStatus = true;
                List<VoteToken> vtAll = voteTokenMapper.getVoteTokenAll(voteID);
                for(VoteToken t : vtAll){
                    if(!t.getToken().trim().equals("")){
                        voteStatus = false;
                    }
                }
                if(!status&&!voteStatus){
                    return 6;//投票未过期且投票未完成
                }
                //投票过期 或 投票完成未过期
                return 4;//查看码
            }else{
                return 3;//口令无效
            }


            //
//            List<String> vt = voteTokenMapper.queryVoteToken(voteID);
//            List<String> vtView = voteTokenMapper.queryVoteView(voteID);
//            List<VoteToken> vtAll = voteTokenMapper.getVoteTokenAll(voteID);
//            boolean voteStatus = true;
//            boolean status = voteService.checkDate(voteID);
//            for(VoteToken t : vtAll){
//                if(!t.getToken().trim().equals("")){
//                    voteStatus = false;
//                }
//            }
//            String md5 = DigestUtils.md5DigestAsHex((token+"ccb2020").getBytes());
//            if(vt.contains(token)){
//                if(status){
//                    return 5;//投票过期，无法投票
//                }
//                return 1;//投票人
//            }
//            if(v.getPass().equals(md5)){
//                return 2;//管理员
//            }
//            if((vtView.contains(token)&&status)||(vtView.contains(token)&&voteStatus)){//投票过期 或 投票完成未过期
//                return 4;//查看码
//            }
//            if(!status&&vtView.contains(token)&&!voteStatus){
//                return 6;//投票未过期且投票未完成
//            }
//            return 3;//口令无效
        } catch (Exception e) {
            return -1;
        }
    }

    @RequestMapping("/voterSubmit")
    @ResponseBody
    public Integer voterSubmit(String title, String describe, String selection, Integer type, Integer limit,String pass,Integer voternum,HttpServletRequest req) {
        String[] array = req.getParameterValues("textdesc[]");//选项简介
        //Verify
        if (title == "") {
            return -7426;
        }
        if (describe == "") {
            return -7426;
        }
        if (limit < -1 || limit == 0) {
            return -7426;
        }

        if (pass == "") {
            return -7426;
        }

        if (voternum < -1 || voternum == 0 || voternum > 100) {
            return -7426;
        }
        //Update
        try {
            Date datenow = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(datenow);
            String md5  = DigestUtils.md5DigestAsHex((pass+"ccb2020").getBytes());
            voteMapper.insertVote(title, describe, selection, type, limit,md5,date);
            Integer VID = voteMapper.queryVoteID(title, describe, selection, type, limit,md5);
            for( int i = 0;i<voternum;i++){
                UUID uuid = UUID.randomUUID();
                int code = uuid.hashCode();
                code = abs(code);
                voteMapper.insertToken(VID.toString(),CommonUtils.DeciamlToThirtySix(code));
            }
            for(int i = 0;i<array.length;i++){
                voteDetailMapper.insertVoteDetail(VID,array[i],i+1);
            }
            return VID;
        } catch (Exception E) {
            E.printStackTrace();
            return -1;
        }
    }

    @RequestMapping("/submitVote")
    @ResponseBody
    public String submitVote(HttpServletRequest request, Integer VID, String selected,String token) {
        // todo 判断token是否有效
        List<VoteToken> vt = voteService.getVoteToken(VID,token);
        if(vt.size()<=0){
            return "2";
        }

        String ipAddr = IpUtil.getIpAddr(request);
        String ipAndVID = ipAddr + ":" + VID;
        if(selected == null){
            selected = "";
        }
        try {
            RateLimiter limiter = loadingCacheService.getRateLimiter(ipAndVID);
            boolean localAccess = false;
            //Localhost is in the WhiteList
            if (ipAddr.equals("0:0:0:0:0:0:0:1")) {
                localAccess = true;
            }
//            if (limiter.tryAcquire() || localAccess) {
                //PASS
                //Get VID info
                Vote vote = voteMapper.getVote(VID);
                String selectionSerial = vote.getSelection();
                //Package and readout
                List<Map<String, String>> selects = Selection.analyze(selectionSerial);
                //Split selection
                String[] selectedList = selected.split(",");
                //Convert to Integer
                Integer[] selList = new Integer[selectedList.length];
                Integer i = 0;
                for (String sel : selectedList) {
                    selList[i] = Integer.parseInt(sel);
                    ++i;
                }
                //Write changes
                //Generate new List
                List<Map<String, String>> newList = new ArrayList<Map<String, String>>();
                //Change one by one
                for (int voteFor = 0; voteFor < selects.size(); ++voteFor) {
                    Map<String, String> stringMap = selects.get(voteFor);
                    boolean flag = false;
                    //Submit
                    for (Integer sel : selList) {
                        if (Integer.parseInt(stringMap.get("num")) == sel) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        //Change
                        Integer before = Integer.parseInt(stringMap.get("count"));
                        Integer after = before + 1;
                        stringMap.put("count", String.valueOf(after));
                    }
                    newList.add(stringMap);
                }
                //To serial
                String serial = Serialize.makeSerial(newList);
                //Update to database
                voteMapper.vote(serial, VID);//
                Date datenow = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = format.format(datenow);
                String md5v = DigestUtils.md5DigestAsHex((token+VID+selected+date).getBytes());
                String md5m = DigestUtils.md5DigestAsHex((vote.getPass()+selected+date).getBytes());
                String crc = DigestUtils.md5DigestAsHex((VID+selected+date+md5v+md5m).getBytes());
                voteTokenMapper.updateVoteToken(selected,VID,token,md5v,md5m,crc,date);
                return "1,"+md5v;
//            } else {
//                //DENIED
//                return "0";
//            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
