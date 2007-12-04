package org.sakaiproject.assignment2.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.tool.beans.Assignment2Bean;
import org.sakaiproject.assignment2.tool.beans.PreviewAssignmentBean;
import org.sakaiproject.assignment2.tool.params.SimpleAssignmentViewParams;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentAttachment;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.tool.api.SessionManager;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class StudentSubmitProducer implements ViewComponentProducer, NavigationCaseReporter, ViewParamsReporter {
	public static final String VIEW_ID = "student-submit";
	public String getViewID() {
		return this.VIEW_ID;
	}
	
    String reqStar = "<span class=\"reqStar\">*</span>";

    private TextInputEvolver richTextEvolver;
    private MessageLocator messageLocator;
    private AssignmentLogic assignmentLogic;
    private ExternalLogic externalLogic;
    private ExternalGradebookLogic externalGradebookLogic;
    private PreviewAssignmentBean previewAssignmentBean;
    private Locale locale;
    private Assignment2Bean assignment2Bean;
    private SessionManager sessionManager;
    private EntityBeanLocator assignment2BeanLocator;
    private AttachmentListRenderer attachmentListRenderer;
    private EntityBeanLocator assignmentSubmissionBeanLocator;

    @SuppressWarnings("unchecked")
	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    	SimpleAssignmentViewParams params = (SimpleAssignmentViewParams) viewparams;
    		
    	
    	//get Passed assignmentId to pull in for editing if any
    	Long assignmentId = params.assignmentId;
    	if (assignmentId == null){
    		//error 
    		return;
    	}
    	Assignment2 assignment = (Assignment2) assignment2BeanLocator.locateBean(assignmentId.toString());
    	if (assignment == null) {
    		//error 
    		return;
    	}
    	
    	//Now do submission stuff
    	String assignmentSubmissionOTP = "assignmentSubmission.";
    	String ASOTPKey = EntityBeanLocator.NEW_PREFIX + "1";
    	assignmentSubmissionOTP += ASOTPKey;
    	AssignmentSubmission assignmentSubmission = (AssignmentSubmission) assignmentSubmissionBeanLocator.locateBean(ASOTPKey); 
    	
    	
    	// use a date which is related to the current users locale
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        
    	UIOutput.make(tofill, "heading_status", " - In Progress");	//HERE
    	
    	//Display Assignment Info
    	UIOutput.make(tofill, "header.title", assignment.getTitle());
    	UIOutput.make(tofill, "header.due_date", (assignment.getDueDate() != null ? df.format(assignment.getDueDate()) : ""));
    	UIOutput.make(tofill, "header.status", "In Progress");		//HERE
    	UIOutput.make(tofill, "header.grade_scale", "Grade Scale from Gradebook");  //HERE
    	if (assignment.getModifiedTime() != null) {
    		UIOutput.make(tofill, "modified_by_header_row");
    		UIOutput.make(tofill, "header.modified_by", df.format(assignment.getModifiedTime()));
    	}
    	UIVerbatim.make(tofill, "instructions", assignment.getInstructions());
    	Set<String> refSet = new HashSet();
        attachmentListRenderer.makeAttachmentFromAssignmentAttachmentSet(tofill, "attachment_list:", params.viewID, 
        	assignment.getAttachmentSet(), Boolean.FALSE);

    	
    	UIForm form = UIForm.make(tofill, "assignment_form");
    	UIOutput.make(form, "submission_instructions"); //Fill in with submission type specifc instructions
    	UIVerbatim.make(form, "instructions", messageLocator.getMessage("assignment2.student-submit.instructions"));
    	
        //Rich Text Input
        UIInput text = UIInput.make(form, "text:", assignmentSubmissionOTP + ".text", assignment.getInstructions());
        richTextEvolver.evolveTextInput(text);
    }
	
	public List reportNavigationCases() {
    	List<NavigationCase> nav= new ArrayList<NavigationCase>();
        nav.add(new NavigationCase("post", new SimpleViewParameters(
            StudentAssignmentListProducer.VIEW_ID)));
        //nav.add(new NavigationCase("preview", new AssignmentAddViewParams(
        //	AssignmentPreviewProducer.VIEW_ID, null, AssignmentAddProducer.VIEW_ID)));
        nav.add(new NavigationCase("save_draft", new SimpleViewParameters(
        	StudentAssignmentListProducer.VIEW_ID)));
        nav.add(new NavigationCase("cancel", new SimpleViewParameters(
        	StudentAssignmentListProducer.VIEW_ID)));
        return nav;
    }
	
    public ViewParameters getViewParameters() {
        return new SimpleAssignmentViewParams();
    }
    
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
    
    public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
        this.richTextEvolver = richTextEvolver;
    }
    
    public void setAssignmentLogic(AssignmentLogic assignmentLogic) {
    	this.assignmentLogic = assignmentLogic;
    }
    
    public void setExternalLogic(ExternalLogic externalLogic) {
    	this.externalLogic = externalLogic;
    }
    
    public void setExternalGradebookLogic(ExternalGradebookLogic externalGradebookLogic) {
    	this.externalGradebookLogic = externalGradebookLogic;
    }
    
    public void setPreviewAssignmentBean(PreviewAssignmentBean previewAssignmentBean) {
    	this.previewAssignmentBean = previewAssignmentBean;
    }
    
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
    
    public void setAssignment2Bean(Assignment2Bean assignment2Bean) {
    	this.assignment2Bean = assignment2Bean;
    }
    
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	public void setAssignment2EntityBeanLocator(EntityBeanLocator entityBeanLocator) {
		this.assignment2BeanLocator = entityBeanLocator;
	}
	
	public void setAssignmentSubmissionBeanLocator(EntityBeanLocator entityBeanLocator) {
		this.assignmentSubmissionBeanLocator = entityBeanLocator;
	}
	
	public void setAttachmentListRenderer(AttachmentListRenderer attachmentListRenderer){
		this.attachmentListRenderer = attachmentListRenderer;
	}
	
	public void setAssignmentSubmissionEntityBeanLocator(EntityBeanLocator entityBeanLocator) {
		this.assignmentSubmissionBeanLocator = entityBeanLocator;
	}

}