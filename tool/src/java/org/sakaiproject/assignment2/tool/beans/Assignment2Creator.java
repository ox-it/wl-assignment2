package org.sakaiproject.assignment2.tool.beans;

import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;
import org.sakaiproject.assignment2.logic.ExternalLogic;

import uk.org.ponder.messageutil.MessageLocator;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Assignment2Creator {

	//public static final String DEFAULT_TITLE = "";
	private ExternalLogic externalLogic;
	private MessageLocator messageLocator;
	
    public Assignment2 create() {
  
    	Assignment2 togo = new Assignment2();
    	togo.setTitle("");
    	togo.setContextId(externalLogic.getCurrentContextId());
    	togo.setCreator(externalLogic.getCurrentUserId());
    	togo.setRemoved(Boolean.FALSE);
    	togo.setHonorPledge(Boolean.FALSE);
    	togo.setRestrictedToGroups(Boolean.FALSE);
    	togo.setUngraded(Boolean.TRUE);
    	togo.setHasAnnouncement(Boolean.FALSE);
    	togo.setSubmissionType(AssignmentConstants.SUBMIT_INLINE_ONLY);
    	
    	//Setting up Dates
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 12);
    	cal.set(Calendar.MINUTE, 0);
    	Date openDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 7);
    	cal.set(Calendar.HOUR_OF_DAY, 17);
    	Date closeDate = cal.getTime();
    	
    	togo.setOpenTime(openDate);
    	togo.setDueDateForUngraded(closeDate);
    	togo.setAcceptUntilTime(closeDate);
    	return togo;
    }
    
    public Assignment2 createDuplicate(Assignment2 assignment) {
    	Assignment2 dup = new Assignment2();

    	String newTitle = messageLocator.getMessage("Assignment2Creator.duplicate.title", assignment.getTitle());
    	
    	dup.setGradableObjectId(assignment.getGradableObjectId());
    	dup.setContextId(assignment.getContextId());
    	dup.setTitle(newTitle);
    	dup.setDraft(Boolean.TRUE);
    	dup.setSortIndex(assignment.getSortIndex());
    	dup.setOpenTime(assignment.getOpenTime());
    	dup.setAcceptUntilTime(assignment.getAcceptUntilTime());
    	dup.setUngraded(assignment.isUngraded());
    	dup.setDueDateForUngraded(assignment.getDueDateForUngraded());
    	dup.setRestrictedToGroups(assignment.isRestrictedToGroups());
    	dup.setGroupSubmission(assignment.isGroupSubmission());
    	dup.setHonorPledge(assignment.isHonorPledge());
    	dup.setInstructions(assignment.getInstructions());
    	dup.setSubmissionType(assignment.getSubmissionType());
    	dup.setNotificationType(assignment.getNotificationType());
		dup.setHasAnnouncement(assignment.getHasAnnouncement());
		dup.setCalendarEventId(assignment.getCalendarEventId());
		dup.setAllowResubmitUntilDue(assignment.isAllowResubmitUntilDue());
		dup.setAllowReviewService(assignment.isAllowReviewService());
		dup.setAllowStudentViewReport(assignment.isAllowStudentViewReport());
		dup.setCreator(externalLogic.getCurrentUserId());
		dup.setCreateTime(new Date());
		dup.setRemoved(Boolean.FALSE);
		
    	return dup;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
	    this.externalLogic = externalLogic;
    }
    
    public void setMessageLocator (MessageLocator messageLocator) {
    	this.messageLocator = messageLocator;
    }
}