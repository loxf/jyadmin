package org.loxf.jyadmin.biz.thread;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JOBStart {
    private static Logger logger = LoggerFactory.getLogger(JOBStart.class);
    private List<JOB> jobList;
    private String[] enableJobs ;
    public JOBStart(List<JOB> jobList, String enableJobList){
        this.jobList = jobList;
        if(StringUtils.isNotBlank(enableJobList)&&!enableJobList.equalsIgnoreCase("all")){
            enableJobs = enableJobList.split(",");
        }
    }

    public void init(){
        logger.debug("JOB准备启动中...");
        start(Executors.newFixedThreadPool(10));
        logger.debug("JOB启动完成...");
    }

    public void start(ExecutorService executeService){
        if(jobList!=null && jobList.size()>0){
            for(JOB job : jobList){
                boolean doJob = false;
                if(enableJobs!=null){
                    for(String enableJob : enableJobs){
                        if(job.getClass().getSimpleName().equalsIgnoreCase(enableJob)){
                            doJob = true;
                            break;
                        }
                    }
                } else {
                    doJob = true;
                }
                if(doJob) {
                    job.setExecutor(executeService);
                    job.start();
                }
            }
        }
    }
}
