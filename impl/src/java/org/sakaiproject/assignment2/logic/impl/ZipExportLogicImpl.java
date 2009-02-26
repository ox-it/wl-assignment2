package org.sakaiproject.assignment2.logic.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.logic.AssignmentBundleLogic;
import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.logic.AssignmentPermissionLogic;
import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.logic.ExternalContentLogic;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.logic.GradeInformation;
import org.sakaiproject.assignment2.logic.ZipExportLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentSubmissionVersion;
import org.sakaiproject.assignment2.model.AttachmentBase;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.util.Validator;

/**
 * Handles the "download all" functionality
 * @author michellewagner
 *
 */
public class ZipExportLogicImpl implements ZipExportLogic
{
    private static Log log = LogFactory.getLog(ZipExportLogicImpl.class);

    private AssignmentLogic assignmentLogic;
    public void setAssignmentLogic(AssignmentLogic assignmentLogic)	{
        this.assignmentLogic = assignmentLogic;
    }

    private AssignmentSubmissionLogic assignmentSubmissionLogic;
    public void setAssignmentSubmissionLogic(
            AssignmentSubmissionLogic assignmentSubmissionLogic) {
        this.assignmentSubmissionLogic = assignmentSubmissionLogic;
    }

    private ExternalGradebookLogic gradebookLogic;
    public void setGradebookLogic(ExternalGradebookLogic gradebookLogic) {
        this.gradebookLogic = gradebookLogic;
    }

    private ExternalLogic externalLogic;
    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    private ExternalContentLogic contentLogic;
    public void setExternalContentLogic(ExternalContentLogic contentLogic) {
        this.contentLogic = contentLogic;
    }

    private AssignmentBundleLogic bundle;
    public void setAssignmentBundleLogic(AssignmentBundleLogic bundle) {
        this.bundle = bundle;
    }

    private AssignmentPermissionLogic permissionLogic;
    public void setAssignmentPermissionLogic(AssignmentPermissionLogic permissionLogic) {
        this.permissionLogic = permissionLogic;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.assignment2.tool.handlerhooks.ZipExporterI#getSubmissionsZip(java.io.OutputStream, java.lang.Long)
     */
    public void getSubmissionsZip(OutputStream outputStream, Long assignmentId)	{
        Assignment2 assignment = assignmentLogic.getAssignmentById(assignmentId);
        if (log.isDebugEnabled())
            log.debug(this + ": getSubmissionsZip reference=" + assignmentId);

        if (!gradebookLogic.isCurrentUserAbleToGrade(assignment.getContextId())) {
            throw new SecurityException("User attempted to download submissions without permission!");
        }

        List<AssignmentSubmission> submissions = assignmentSubmissionLogic
        .getViewableSubmissionsWithHistoryForAssignmentId(assignment.getId(), null);

        zipSubmissions(assignment, submissions, outputStream);

    } // getSubmissionsZip
    

    public String extractIdFromFolderName(String folderName, Pattern pattern) {
        String id = null;
        Matcher idMatcher = pattern.matcher(folderName);
        if (idMatcher.find()) {
            id = idMatcher.group();
        }
        return id;
    }

    /**
     * Create a zip file of all of the viewable submissions with the submission
     * and feedback info
     * @param assignment
     * @param submissionsWithHistory
     * @param outputStream
     */
    protected void zipSubmissions(Assignment2 assignment,
            List<AssignmentSubmission> submissionsWithHistory, OutputStream outputStream)
    {
        if (assignment == null) {
            throw new IllegalArgumentException("Null assignment passed to zipSubmissions");
        }

        String contextId = externalLogic.getCurrentContextId();
        String currUserId = externalLogic.getCurrentUserId();
        String siteTitle = externalLogic.getSiteTitle(contextId);

        List<String> viewableStudents = permissionLogic.getViewableStudentsForUserForItem(currUserId, assignment);
        Map<String, User> userIdUserMap = externalLogic.getUserIdUserMap(viewableStudents);

        String formatWithTime = bundle.getString("assignment2.assignment_grade_assignment.downloadall.filename_date_format_with_time");
        DateFormat df_withTime = new SimpleDateFormat(formatWithTime, bundle.getLocale());
        String feedbackFolderName = bundle.getString("assignment2.assignment_grade_assignment.downloadall.feedback_folder_name");
        String feedbackFileName = bundle.getString("assignment2.assignment_grade_assignment.downloadall.filename_feedback_comments") + ".html";
        String annotatedTextFileName = bundle.getString("assignment2.assignment_grade_assignment.downloadall.filename_annotated_text") + ".html";
        String submittedTextFileName = bundle.getString("assignment2.assignment_grade_assignment.downloadall.filename_submitted_text") + ".html";

        if (submissionsWithHistory == null || submissionsWithHistory.isEmpty()) {
            if (log.isDebugEnabled()) log.debug("Nothing to download!!");
        } else {

            try
            {
                ZipOutputStream out = new ZipOutputStream(outputStream);

                // create the folder structure - named after the assignment's title
                String root = escapeZipEntry(assignment.getTitle() + "-" + siteTitle, null) + Entity.SEPARATOR;

                if (submissionsWithHistory != null && !submissionsWithHistory.isEmpty())
                {
                    Pattern studentIdPattern = Pattern.compile("(?<=\\().*(?=\\))");
                    // Create the ZIP file
                    for (AssignmentSubmission s : submissionsWithHistory)
                    {
                        // we create a folder for every student, regardless
                        // of their submission status
                        User submitterUser = userIdUserMap.get(s.getUserId());
                        if (submitterUser != null) {
                            // the zip will contain a folder for each submission
                            String submissionFolder = root + getSubmissionFolderName(submitterUser);
                            
                            Set<AssignmentSubmissionVersion> versionHistory = s.getSubmissionHistorySet();
                            if (versionHistory == null || versionHistory.isEmpty()) {
                                // if there are no submitted versions, add a feedback folder
                                // that will be considered "feedback w/o submission"
                                // create the text file for feedback comments
                                addFeedbackToZip(out, null, submissionFolder, feedbackFolderName, feedbackFileName, annotatedTextFileName);
                            } else {
                                // we will add all of the student's submitted versions
                                // as folders with the timestamp for the folder name

                                for (AssignmentSubmissionVersion version : versionHistory) {
                                    // check for feedback prior to submission
                                    if (version.getSubmittedVersionNumber() == 0) {
                                        // let's add a feedback folder at the top level
                                        addFeedbackToZip(out, version, submissionFolder, feedbackFolderName, feedbackFileName, annotatedTextFileName);
                                    };
                                    // only include submitted versions
                                    if (version.getSubmittedDate() != null) {
                                        // we will create a folder for each submitted version for
                                        // this student
                                        String versionFolder = submissionFolder + Entity.SEPARATOR 
                                        + getVersionFolderName(version, df_withTime);

                                        addSubmissionInfoToZip(out, version, versionFolder, submittedTextFileName);                             
                                        addFeedbackToZip(out, version, versionFolder, feedbackFolderName, feedbackFileName, annotatedTextFileName);
                                    }
                                }
                            }
                        }
                    }
                }
                // create the grades csv file if assign is graded
                if (assignment.isGraded() && assignment.getGradebookItemId() != null) {
                    String gradesCSVString = getGradesAsCSVString(currUserId, assignment, userIdUserMap);

                    if (gradesCSVString != null && gradesCSVString.length() > 0) {
                        // create a grades.csv file and add to zip
                        String gradesFile = escapeZipEntry(assignment.getTitle() + "-" + siteTitle, null);
                        String fullGradesFileName =  root + gradesFile + ".csv";

                        ZipEntry gradesCSVEntry = new ZipEntry(fullGradesFileName);
                        out.putNextEntry(gradesCSVEntry);
                        byte[] grades = gradesCSVString.getBytes();
                        out.write(grades);
                        gradesCSVEntry.setSize(grades.length);
                        out.closeEntry();
                    }
                }

                // Complete the ZIP file
                out.finish();
                out.flush();
                out.close();
            }
            catch (IOException e)
            {
                log.warn(this + ": getSubmissionsZip--IOException unable to create " +
                        "the zip file for assignment " + assignment.getTitle());
            }
        }
    }

    public String escapeZipEntry(String value, String replaceSpaces) {
        if (value != null) {
            if (replaceSpaces != null) {
                value = value.replaceAll(" ", "_");
            }
            
            //truncate long names
            if (value.length() > MAX_FILE_NAME_LENGTH) {
                value = value.substring(0, MAX_FILE_NAME_LENGTH);
            }
            
            value = Validator.escapeZipEntry(value);
        }

        return value;
    }

    private void zipAttachments(ZipOutputStream out, String versionFolder, 
            Set<? extends AttachmentBase> attachments)
    {
        int attachedUrlCount = 0;
        for (AttachmentBase r : attachments)
        {
            InputStream content = null;
            BufferedInputStream bContent = null;
            try
            {
                ContentResource resource = contentLogic.getContentResource(r
                        .getAttachmentReference());

                if (resource == null) 
                {
                    log.warn("Unable to retrieve ContentResource with reference:" + 
                            r.getAttachmentReference() + ". This attachment was " +
                    "not included in the zip file.");
                } else
                {
                    String contentType = resource.getContentType();

                    ResourceProperties props = resource.getProperties();
                    String displayName = props.getPropertyFormatted(props
                            .getNamePropDisplayName());

                    // for URL content type, encode a redirect to the body URL
                    if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
                    {
                        displayName = "attached_URL_" + attachedUrlCount;
                        attachedUrlCount++;
                    }

                    // buffered stream input
                    content = resource.streamContent();
                    byte data[] = new byte[1024 * 10];
                    bContent = new BufferedInputStream(content,
                            data.length);

                    ZipEntry attachmentEntry = new ZipEntry(versionFolder
                            + Entity.SEPARATOR + displayName);
                    out.putNextEntry(attachmentEntry);
                    int bCount = -1;
                    while ((bCount = bContent.read(data, 0, data.length)) != -1)
                    {
                        out.write(data, 0, bCount);
                    }
                    out.closeEntry();
                    content.close();
                }
            }
            catch (IOException e)
            {
                log	.warn(this + ": getSubmissionsZip--IOException: Problem in " +
                        "creating the attachment file: parentFolder="
                        + versionFolder + " attachment reference=" + r);
            }
            catch (ServerOverloadException e)
            {
                log.warn(this + ": getSubmissionsZip--ServerOverloadException: " +
                        "parentFolder=" + versionFolder + " attachment reference=" + r);
            }
            finally
            {
                if (content != null)
                {
                    try
                    {
                        content.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("IOException when closing content stream", e);
                    }
                }
                if (bContent != null)
                {
                    try
                    {
                        bContent.close();
                    }
                    catch (IOException e)
                    {
                        log.warn("IOException when closing bContent stream", e);
                    }
                }
            }
        } // for
    }

    /**
     * 
     * @param currUserId
     * @param assignment
     * @param userIdUserMap
     * @return a string representation of the grade data in csv format for this assignment
     */
    private String getGradesAsCSVString(String currUserId, Assignment2 assignment, Map<String, User> userIdUserMap) {
        // the buffer used to store grade information
        StringBuilder gradesBuilder = new StringBuilder();
        gradesBuilder.append(
                bundle.getFormattedMessage("assignment2.assignment_grade-assignment.downloadall.header",
                        new Object[] {assignment.getTitle()}))
                        .append("\n");

        // now iterate through all GRADABLE students in this class to create the grades file
        List<String> gradableStudents = permissionLogic.getGradableStudentsForUserForItem(currUserId, assignment);

        // get the grade information
        Map<String, GradeInformation> userIdGradeMap = gradebookLogic.getGradeInformationForStudents(gradableStudents, assignment.getContextId(), assignment.getGradebookItemId());

        for (String studentId : gradableStudents) {
            // get their User info
            User student = userIdUserMap.get(studentId);
            if (student != null) {
                gradesBuilder.append("\"");
                gradesBuilder.append(student.getDisplayId());
                gradesBuilder.append("\"");
                gradesBuilder.append(",");
                gradesBuilder.append("\"");
                gradesBuilder.append(student.getSortName());
                gradesBuilder.append("\"");
                gradesBuilder.append(",");

                // now check for grade information
                GradeInformation gradeInfo = userIdGradeMap.get(studentId);
                if (gradeInfo != null) {
                    String gradebookGrade = "";
                    String gradebookComment = "";

                    if (gradeInfo.getGradebookGrade() != null) {
                        gradebookGrade = gradeInfo.getGradebookGrade();
                    }
                    if (gradeInfo.getGradebookComment() != null) {
                        gradebookComment = gradeInfo.getGradebookComment();
                    }
                    gradesBuilder.append("\"");
                    gradesBuilder.append(gradebookGrade);
                    gradesBuilder.append("\"");
                    gradesBuilder.append(",");
                    gradesBuilder.append("\"");
                    gradesBuilder.append(gradebookComment);
                    gradesBuilder.append("\"");
                    gradesBuilder.append(",");
                } else {
                    gradesBuilder.append(",,");
                }

                gradesBuilder.append("\n");
            }
        }

        return gradesBuilder.toString();
    }

    /**
     * 
     * @param submitterUser the submitter's User object
     * @return the name for this student's submission folder composed of
     * his/her sort name + (displayId)
     * ie Wagner, Michelle (wagnermr)
     */
    private String getSubmissionFolderName(User submitterUser) {
        String submissionFolderName = submitterUser.getSortName() + 
        " (" + submitterUser.getDisplayId() + ")";
        submissionFolderName = FormattedText.encodeUnicode(submissionFolderName);
        return escapeZipEntry(submissionFolderName, null);
    }

    /**
     * 
     * @param version
     * @param dateFormat
     * @return the name for the version folder. It is created using the timestamp
     * of the version's submitted date and time plus the version id in parentheses
     * ie 20090225_0930AM (12345)
     */
    private String getVersionFolderName(AssignmentSubmissionVersion version, DateFormat dateFormat) {
        String versionFolderName = dateFormat.format(version.getSubmittedDate()) + " (" +
        version.getId() + ")";

        return versionFolderName;
    }

    /**
     * 
     * @param out ZipOutputStream used for zipping up submission info
     * @param version AssignmentSubmissionVersion for submission info
     * @param parentFolder the parent folder that the submission info will be added to
     * ie Homework 1/Wagner, Michelle (wagnermr)
     * @param submittedTextFileName
     */
    private void addSubmissionInfoToZip(ZipOutputStream out, AssignmentSubmissionVersion version,
            String parentFolder, String submittedTextFileName) {
        try {
            if (version != null) {
                if (version.getSubmittedText() != null && 
                        version.getSubmittedText().trim().length() > 0) {
                    // create the text file only when submittedText exists  
                    ZipEntry textEntry = new ZipEntry(parentFolder + Entity.SEPARATOR 
                            + submittedTextFileName);
                    out.putNextEntry(textEntry);
                    byte[] text = version.getSubmittedText().getBytes();
                    out.write(text);
                    textEntry.setSize(text.length);
                    out.closeEntry();
                }

                // add the submission attachments
                if (version.getSubmissionAttachSet() != null && !version.getSubmissionAttachSet().isEmpty()) {
                    zipAttachments(out, parentFolder, version.getSubmissionAttachSet());
                }
            }
        } catch (IOException ioe) {
            log.error("IOException while downloading submission info for zip", ioe);
        }
    }

    /**
     * 
     * @param out ZipOutputStream used for zipping up submission info
     * @param version AssignmentSubmissionVersion that you are adding fb for. if null,
     * will add a feedback folder with a blank feedback comments file
     * @param parentFolder the parent folder that the feedback info will be added to
     * ie Homework 1/Wagner, Michelle (wagnermr)
     * @param feedbackFolderName the name of your feedback folder
     * @param feedbackFileName the name of your feedback file
     * @param annotatedTextFileName the name of your annotated submitted text file
     */
    private void addFeedbackToZip(ZipOutputStream out, AssignmentSubmissionVersion version, 
            String parentFolder, String feedbackFolderName, 
            String feedbackFileName, String annotatedTextFileName) {

        // add the feedback folder
        String versionFeedbackFolder = parentFolder + Entity.SEPARATOR
        + feedbackFolderName;

        // inside the feedback folder we will have:
        //    1) a file for annotating the submitted text if there
        //        is submitted text
        //    2) a file for feedback comments
        //    3) any existing feedback attachments

        try {
            // only add the annotated text and fb attachments if a version exists
            if (version != null) {
                if (version.getAnnotatedText() != null && version.getAnnotatedText().trim().length() > 0) {
                    // create the text file only when annotatedText exists
                    ZipEntry textEntry = new ZipEntry(versionFeedbackFolder + Entity.SEPARATOR 
                            + annotatedTextFileName);
                    out.putNextEntry(textEntry);
                    byte[] text = version.getSubmittedText().getBytes();
                    out.write(text);
                    textEntry.setSize(text.length);
                    out.closeEntry();
                }
                // add any feedback attachments
                if (version.getFeedbackAttachSet() != null && !version.getFeedbackAttachSet().isEmpty()) {
                    zipAttachments(out, versionFeedbackFolder, version.getFeedbackAttachSet());
                }
            }

            // add the feedback comments file
            ZipEntry fbTextEntry = new ZipEntry(versionFeedbackFolder + Entity.SEPARATOR 
                    + feedbackFileName);
            out.putNextEntry(fbTextEntry);
            if (version != null && version.getFeedbackNotes() != null) {
                byte[] text = version.getFeedbackNotes().getBytes();
                out.write(text);
                fbTextEntry.setSize(text.length);
            } else {
                fbTextEntry.setSize(0);
            }

            out.closeEntry();
        } catch (IOException ioe) {
            log.error("An I/O Exception occurred while attempting to zip up feedback info", ioe);
        }
    }
}
