package org.sakaiproject.assignment2.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

import org.sakaiproject.assignment2.tool.beans.AssignmentSubmissionBean;
import org.sakaiproject.assignment2.tool.params.AssignmentAddViewParams;
import org.sakaiproject.assignment2.tool.params.AssignmentGradeAssignmentViewParams;
import org.sakaiproject.assignment2.tool.params.AssignmentListSortViewParams;
import org.sakaiproject.assignment2.tool.params.SimpleAssignmentViewParams;
import org.sakaiproject.assignment2.tool.params.SortPagerViewParams;
import org.sakaiproject.assignment2.tool.params.AssignmentGradeViewParams;
import org.sakaiproject.assignment2.tool.producers.NavBarRenderer;
import org.sakaiproject.assignment2.tool.producers.PagerRenderer;
import org.sakaiproject.assignment2.tool.producers.AssignmentGradeProducer;
import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIComponent;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import java.util.Map;
import java.util.HashMap;

public class AssignmentViewSubmissionsProducer implements ViewComponentProducer, ViewParamsReporter {

    public static final String VIEW_ID = "assignment_view-submissions";
    public String getViewID() {
        return VIEW_ID;
    }
    
  //sorting strings
    public static final String DEFAULT_SORT_DIR = AssignmentLogic.SORT_DIR_ASC;
    public static final String DEFAULT_OPPOSITE_SORT_DIR = AssignmentLogic.SORT_DIR_DESC;
    public static final String DEFAULT_SORT_BY = AssignmentSubmissionLogic.SORT_BY_NAME;
    
    private String current_sort_by = DEFAULT_SORT_BY;
    private String current_sort_dir = DEFAULT_SORT_DIR;
    private String opposite_sort_dir = DEFAULT_OPPOSITE_SORT_DIR;
    
    //images
    public static final String BULLET_UP_IMG_SRC = "/sakai-assignment2-tool/content/images/bullet_arrow_up.png";
    public static final String BULLET_DOWN_IMG_SRC = "/sakai-assignment2-tool/content/images/bullet_arrow_down.png";

    private NavBarRenderer navBarRenderer;
    private PagerRenderer pagerRenderer;
    private MessageLocator messageLocator;
    private AssignmentLogic assignmentLogic;
    private AssignmentSubmissionLogic submissionLogic;
    private TargettedMessageList messages;
    private ExternalLogic externalLogic;
    private Locale locale;
    private SortHeaderRenderer sortHeaderRenderer;
    private AttachmentListRenderer attachmentListRenderer;
    private AssignmentSubmissionBean submissionBean;
    
    private Long assignmentId;
    
    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    	AssignmentGradeAssignmentViewParams params = (AssignmentGradeAssignmentViewParams) viewparams;
    	//make sure that we have an AssignmentID to work with
    	if (params.assignmentId == null){
    		//ERROR SHOULD BE SET, OTHERWISE TAKE BACK TO ASSIGNMENT_LIST
    		messages.addMessage(new TargettedMessage("GeneralActionError"));
    		return;
    	}
    	assignmentId = params.assignmentId;
    	Assignment2 assignment = assignmentLogic.getAssignmentByIdWithAssociatedData(assignmentId);
  
    	//use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    	
    	//get parameters
    	if (params.sort_by == null) params.sort_by = DEFAULT_SORT_BY;
    	if (params.sort_dir == null) params.sort_dir = DEFAULT_SORT_DIR;
    	current_sort_by = params.sort_by;
    	current_sort_dir = params.sort_dir;
    	opposite_sort_dir = (AssignmentLogic.SORT_DIR_ASC.equals(current_sort_dir) 
    			? AssignmentLogic.SORT_DIR_DESC : AssignmentLogic.SORT_DIR_ASC);
    	
    	List<AssignmentSubmission> submissions = submissionLogic.getViewableSubmissionsForAssignmentId(assignmentId);
    	
    	//get paging data
    	int total_count = submissions != null ? submissions.size() : 0;
    	
        UIMessage.make(tofill, "page-title", "assignment2.assignment_grade-assignment.title");
        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        pagerRenderer.makePager(tofill, "pagerDiv:", VIEW_ID, viewparams, total_count);
        UIMessage.make(tofill, "heading", "assignment2.assignment_grade-assignment.heading", new Object[] { assignment.getTitle() });

        UIForm assign_form = UIForm.make(tofill, "assign_form");
        UIMessage.make(assign_form, "assign_grade", "assignment2.assignment_grade-assignment.assign_grade");
        UIInput.make(assign_form, "assign_grade_input", "");
        UICommand.make(assign_form, "assign_grade_submit", "");

        
        //Do Student Table
        sortHeaderRenderer.makeSortingLink(tofill, "tableheader.student", viewparams, 
        		AssignmentSubmissionLogic.SORT_BY_NAME, "assignment2.assignment_grade-assignment.tableheader.student");
        sortHeaderRenderer.makeSortingLink(tofill, "tableheader.submitted", viewparams, 
        		AssignmentSubmissionLogic.SORT_BY_SUBMIT_DATE, "assignment2.assignment_grade-assignment.tableheader.submitted");
        sortHeaderRenderer.makeSortingLink(tofill, "tableheader.status", viewparams, 
        		AssignmentSubmissionLogic.SORT_BY_STATUS, "assignment2.assignment_grade-assignment.tableheader.status");
        sortHeaderRenderer.makeSortingLink(tofill, "tableheader.grade", viewparams, 
        		AssignmentSubmissionLogic.SORT_BY_GRADE, "assignment2.assignment_grade-assignment.tableheader.grade");
        sortHeaderRenderer.makeSortingLink(tofill, "tableheader.released", viewparams, 
	      		AssignmentSubmissionLogic.SORT_BY_RELEASED, "assignment2.assignment_grade-assignment.tableheader.released");
                
        //Do Table Data
        submissionBean.filterPopulateAndSortSubmissionList(submissions, params.current_start, params.current_count, 
        		current_sort_by, current_sort_dir.equals(AssignmentLogic.SORT_DIR_ASC));
        
        for (AssignmentSubmission as : submissions){
        	UIBranchContainer row = UIBranchContainer.make(tofill, "row:");
        	
        	UIOutput.make(row, "row_name", externalLogic.getUserDisplayName(as.getUserId()));
        	UIInternalLink.make(row, "row_grade_link", 
        			new AssignmentGradeViewParams(AssignmentGradeProducer.VIEW_ID, as.getAssignment().getAssignmentId(), as.getUserId()));
        	
        	if (as.getCurrentSubmissionVersion() != null && as.getCurrentSubmissionVersion().getSubmittedTime() != null){
        		UIOutput.make(row, "row_submitted", df.format(as.getCurrentSubmissionVersion().getSubmittedTime()));
        	} else {
        		UIOutput.make(row, "row_submitted", "");
        	}
        	
        	UIOutput.make(row, "row_status", as.getSubmissionStatus());
        	
        	UIOutput.make(row, "row_grade", as.getGradebookGrade());
        	if (assignment.isUngraded()){
        		if (as.getCurrentSubmissionVersion() != null)  {
        			Date releasedTime = as.getCurrentSubmissionVersion().getReleasedTimeForUngraded();
        			if (releasedTime != null && releasedTime.before(new Date())) {
        				UIOutput.make(row, "row_released");
        			}
        		}
        	} else {
        		if (as.isGradebookGradeReleased()) {
        			UIOutput.make(row, "row_released");
        		}
        	}
        }
        

        //Assignment Details
        UIMessage.make(tofill, "assignment_details", "assignment2.assignment_grade-assignment.assignment_details");
        UIInternalLink.make(tofill, "assignment_details_edit", new AssignmentAddViewParams(AssignmentAddProducer.VIEW_ID, assignment.getAssignmentId()));
        UIMessage.make(tofill, "assignment_details.title_header", "assignment2.assignment_grade-assignment.assignment_details.title");
        UIOutput.make(tofill, "assignment_details.title", assignment.getTitle());
        UIMessage.make(tofill, "assignment_details.created_by_header", "assignment2.assignment_grade-assignment.assignment_details.created_by");
        UIOutput.make(tofill, "assignment_details.created_by", externalLogic.getUserDisplayName(assignment.getCreator()));
        UIMessage.make(tofill, "assignment_details.modified_header", "assignment2.assignment_grade-assignment.assignment_details.modified");
        UIOutput.make(tofill, "assignment_details.modified", (assignment.getModifiedTime() != null ? df.format(assignment.getModifiedTime()) : ""));
        UIMessage.make(tofill, "assignment_details.open_header", "assignment2.assignment_grade-assignment.assignment_details.open");
        UIOutput.make(tofill, "assignment_details.open", df.format(assignment.getOpenTime()));
        UIMessage.make(tofill, "assignment_details.due_header", "assignment2.assignment_grade-assignment.assignment_details.due");
        if (assignment.isUngraded()){
        	UIOutput.make(tofill, "assignment_details.due", 
        			(assignment.getDueDateForUngraded() != null ? df.format(assignment.getDueDateForUngraded()) : ""));
        } else {
        	UIOutput.make(tofill, "assignment_details.due", 
        			(assignment.getDueDate() != null ? df.format(assignment.getDueDate()) : ""));
        }
        UIMessage.make(tofill, "assignment_details.accept_until_header", "assignment2.assignment_grade-assignment.assignment_details.accept_until");
        UIOutput.make(tofill, "assignment_details.accept_until", df.format(assignment.getAcceptUntilTime()));
        UIMessage.make(tofill, "assignment_details.submissions_header", "assignment2.assignment_grade-assignment.assignment_details.submissions");
        UIMessage.make(tofill, "assignment_details.submissions", "assignment2.submission_type." + String.valueOf(assignment.getSubmissionType()));
        //UIMessage.make(tofill, "assignment_details.scale_header", "assignment2.assignment_grade-assignment.assignment_details.scale");
        //UIOutput.make(tofill, "assignment_details.scale", "Points (max100.0)");
        UIMessage.make(tofill, "assignment_details.honor_header", "assignment2.assignment_grade-assignment.assignment_details.honor");
        UIMessage.make(tofill, "assignment_details.honor", (assignment.isHonorPledge() ? "assignment2.yes" : "assignment2.no"));
        
        UIMessage.make(tofill, "assignment_details.instructions_header", "assignment2.assignment_grade-assignment.assignment_details.instructions");
        UIVerbatim.make(tofill, "assignment_details.instructions", assignment.getInstructions());
        UIMessage.make(tofill, "assignment_details.attachments_header", "assignment2.assignment_grade-assignment.assignment_details.attachments");
        
        //Init JS
        String frameId = org.sakaiproject.util.Web.escapeJavascript("Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId());
        UIVerbatim.make(tofill, "iframeId_init", "var iframeId = \"" + frameId + "\";");
    	
        attachmentListRenderer.makeAttachmentFromAssignmentAttachmentSet(tofill, "attachment_list:", params.viewID, 
        		assignment.getAttachmentSet(), Boolean.FALSE);
        
        
    }
    
    public ViewParameters getViewParameters(){
    	return new AssignmentGradeAssignmentViewParams();
    }

    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
    
    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }
    
    public void setPagerRenderer(PagerRenderer pagerRenderer){
    	this.pagerRenderer = pagerRenderer;
    }
    
    public void setAssignmentLogic(AssignmentLogic assignmentLogic) {
    	this.assignmentLogic = assignmentLogic;
    }
    
    public void setAssignmentSubmissionLogic(AssignmentSubmissionLogic submissionLogic) {
    	this.submissionLogic = submissionLogic;
    }
    
    public void setMessages(TargettedMessageList messages) {
    	this.messages = messages;
    }
    
    public void setExternalLogic(ExternalLogic externalLogic) {
    	this.externalLogic = externalLogic;
    }
    
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
    
    public void setSortHeaderRenderer(SortHeaderRenderer sortHeaderRenderer) {
    	this.sortHeaderRenderer = sortHeaderRenderer;
    }
    
	public void setAttachmentListRenderer(AttachmentListRenderer attachmentListRenderer){
		this.attachmentListRenderer = attachmentListRenderer;
	}
	
	public void setAssignmentSubmissionBean(AssignmentSubmissionBean submissionBean){
		this.submissionBean = submissionBean;
	}
}