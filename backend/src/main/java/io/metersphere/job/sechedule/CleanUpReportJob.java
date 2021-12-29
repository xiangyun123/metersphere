package io.metersphere.job.sechedule;

import io.metersphere.base.domain.Project;
import io.metersphere.commons.constants.ScheduleGroup;
import io.metersphere.commons.utils.CommonBeanFactory;
import io.metersphere.service.ProjectService;
import io.metersphere.utils.LoggerUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author lyh
 */
public class CleanUpReportJob extends MsScheduleJob {

    private final ProjectService projectService;
    private static final String UNIT_DAY = "D";
    private static final String UNIT_MONTH = "M";
    private static final String UNIT_YEAR = "Y";
    LocalDate localDate;

    public CleanUpReportJob() {
        projectService = CommonBeanFactory.getBean(ProjectService.class);
        localDate = LocalDate.now();
    }

    @Override
    void businessExecute(JobExecutionContext context) {
        Project project = projectService.getProjectById(resourceId);
        Boolean cleanTrackReport = project.getCleanTrackReport();
        Boolean cleanApiReport = project.getCleanApiReport();
        Boolean cleanLoadReport = project.getCleanLoadReport();
        if (BooleanUtils.isTrue(cleanTrackReport)) {
            this.cleanUpTrackReport(project.getCleanTrackReportExpr());
        }
        if (BooleanUtils.isTrue(cleanApiReport)) {
            this.cleanUpApiReport(project.getCleanApiReportExpr());
        }
        if (BooleanUtils.isTrue(cleanLoadReport)) {
            this.cleanUpLoadReport(project.getCleanLoadReportExpr());
        }
    }

    public static JobKey getJobKey(String projectId) {
        return new JobKey(projectId, ScheduleGroup.CLEAN_UP_REPORT.name());
    }

    public static TriggerKey getTriggerKey(String projectId) {
        return new TriggerKey(projectId, ScheduleGroup.CLEAN_UP_REPORT.name());
    }

    private void cleanUpTrackReport(String expr) {
        long time = getCleanDate(expr);
        projectService.cleanUpTrackReport(time);
    }

    private void cleanUpApiReport(String expr) {
        long time = getCleanDate(expr);
        projectService.cleanUpApiReport(time);
    }

    private void cleanUpLoadReport(String expr) {
        long time = getCleanDate(expr);
        projectService.cleanUpLoadReport(time);
    }

    private long getCleanDate(String expr) {
        LocalDate date = null;
        long timeMills = 0;
        if (StringUtils.isNotBlank(expr)) {
            try {
                String unit = expr.substring(expr.length() - 1);
                int quantity = Integer.parseInt(expr.substring(0, expr.length() - 1));
                if (StringUtils.equals(unit, UNIT_DAY)) {
                    date = localDate.minusDays(quantity);
                } else if (StringUtils.equals(unit, UNIT_MONTH)) {
                    date = localDate.minusMonths(quantity);
                } else if (StringUtils.equals(unit, UNIT_YEAR)) {
                    date = localDate.minusYears(quantity);
                }
            } catch (Exception e) {
                LoggerUtil.error(e);
                LoggerUtil.error("clean up job. get clean date error. project : " + resourceId);
            }
        }
        if (date != null) {
            timeMills = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        return timeMills;
    }
}
