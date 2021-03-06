package com.htzw.study.controller;

import com.htzw.study.entities.WorkLog;
import com.htzw.study.service.WorkLogService;
import com.htzw.study.utils.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Description:    工作日报控制层
* @Author:         glj
* @CreateDate:     2018/10/20
* @Version:        1.0
*/
@RestController
@RequestMapping(value = "/work")
@Api(value = "工作日报操作接口",tags = {"Work Log Controller"})
public class WorkLogController {
    @Autowired
    private WorkLogService workLogService;

    @ApiOperation(value = "获取所有的工作日报信息",notes = "获取所有的工作日报信息")
    @RequestMapping(value = "list",method = RequestMethod.GET)
    public Map<String,Object> getList(){
        Map<String,Object> map = new HashMap<>(2);
        List<WorkLog> res = workLogService.list();
        map.put("workLogList",res);
        return map;
    }

    @ApiOperation(value = "获取工作日报信息",notes = "根据用户编号和周数来获取相应的工作日报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "week", value = "周数",required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户编号",required = true, paramType = "query",dataType = "int")
    })
    @RequestMapping(value = "getWorkByWeek",method = RequestMethod.GET)
    public Map<String,Object> getWorkByWeek(String week, Integer userId){
        Map<String,Object> map = new HashMap<>(2);
        List<WorkLog> res = workLogService.queryWorkLogByWeek(week, userId);
        map.put("workLogList",res);
        return map;
    }

    @ApiOperation(value = "获取符合条件的工作日报信息",notes = "根据用户编号和周数来获取相应的工作日报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value = "开始时间",required = true,paramType = "query",dataType ="String"),
            @ApiImplicitParam(name = "endDate",value = "结束时间",required = true,paramType = "query",dataType ="String"),
            @ApiImplicitParam(name = "userId",value = "用户编号",required = true,paramType = "query",dataType ="int")
    })
    @RequestMapping(value = "getWorkByTimes",method = RequestMethod.GET)
    public Map<String,Object> getWorkByTimes(String startDate, String endDate, Integer userId){
        Map<String,Object> map = new HashMap<>(2);
        List<WorkLog> res = workLogService.queryWorkLogByTimes(startDate, endDate, userId);
        map.put("workLogList",res);
        return map;
    }

    @ApiOperation(value = "删除工作日报信息",notes = "根据编号删除相应的工作日报信息")
    @ApiImplicitParam(name = "workLogId",value = "工作日报编号",required = true,paramType = "header",dataType = "int")
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public Map<String,Object> deleteWorkLog (@RequestHeader Integer workLogId){
        Map<String,Object> map = new HashMap<>(2);
        WorkLog  workLog = workLogService.queryWorkLogById(workLogId);
        if (workLog == null){
            map.put("success",false);
        }
        workLog.setStatus(0);
        workLog.setUpdateDate(TimeUtils.dateToString(new Date()));
        map.put("success",workLogService.modifyWorkLog (workLog));
        return map;
    }

    @ApiOperation(value = "添加工作日报信息",notes = "添加工作日报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value = "开始时间",required = true,dataType ="String"),
            @ApiImplicitParam(name = "endDate",value = "开始时间",required = true,dataType ="String"),
            @ApiImplicitParam(name = "workSummary",value = "工作总结、经验教训",required = true,dataType ="String"),
            @ApiImplicitParam(name = "workContent",value = "工作内容",required = true,dataType ="String"),
            @ApiImplicitParam(name = "projectId",value = "项目编号",required = true,dataType ="int"),
            @ApiImplicitParam(name = "userId",value = "用户编号",required = true,dataType ="int"),
            @ApiImplicitParam(name = "workSummary",value = "工作总结",required = true,dataType ="String")
    })
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public Map<String,Object> addWorkLog ( String startDate, String endDate, String workContent, Integer projectId, Integer userId, String workSummary){
        Map<String,Object> map = new HashMap<>(2);
        if (projectId == null || userId ==null){
            map.put("success",false);
            return map;
        }
        WorkLog workLog = new WorkLog(workContent,startDate,endDate,projectId,userId,workSummary);
        workLog.setStatus(1);
       String[] times = startDate.split("-");
        if (times.length == 3){
            workLog.setYear(times[0]);
            workLog.setMonth(times[1]);
        }
        workLog.setUpdateDate(TimeUtils.dateToString(new Date()));
        map.put("success",workLogService.addWorkLog (workLog));
        return map;
    }

    @ApiOperation(value = "更新工作日报信息",notes = "更新工作日报对象信息")
    @ApiImplicitParam(name = "workLog",value = "工作日报对象",required = true,paramType = "body",dataType = "WorkLog")
    @RequestMapping(value = "modify",method = RequestMethod.POST)
    public Map<String,Object> modifyWorkLog (@RequestBody WorkLog  workLog){
        Map<String,Object> map = new HashMap<>(2);
        WorkLog  currentWorkLog  = workLogService.queryWorkLogById(workLog.getWorkId());
        if (currentWorkLog  == null){
            map.put("success",false);
            return map;
        }
        //TODO 待确定界面后完善
        map.put("success",workLogService.modifyWorkLog (currentWorkLog ));
        return map;
    }

    @ApiOperation(value = "根据编号获取对应的工作日报信息",notes = "根据工作日报编号获取工作日报对象信息")
    @ApiImplicitParam(name = "workLogId",value = "工作日报编号",required = true,paramType = "query",dataType = "int")
    @RequestMapping(value = "getWorkLogById",method = RequestMethod.GET)
    public Map<String,Object> getWorkLogById(Integer workLogId){
        Map<String,Object> map = new HashMap<>(2);
        map.put("workLog",workLogService.queryWorkLogById(workLogId));
        return map;
    }
}
