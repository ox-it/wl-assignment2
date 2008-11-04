package org.sakaiproject.assignment2.tool.params;

public class StudentSubmissionParams extends SimpleAssignmentViewParams {
    public boolean preview;

    public StudentSubmissionParams() {}
    
    public StudentSubmissionParams(String viewId, Long assignmentId) {
        this.viewID = viewId;
        this.assignmentId = assignmentId;
    }
    
    public StudentSubmissionParams(String viewid, Long assignmentId, boolean preview) {
        this.viewID = viewid;
        this.assignmentId = assignmentId;
        this.preview = preview;
    }
}
