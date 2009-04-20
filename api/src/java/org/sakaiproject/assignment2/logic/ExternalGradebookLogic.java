/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.assignment2.logic;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.exception.GradebookItemNotFoundException;
import org.sakaiproject.assignment2.exception.InvalidGradeForAssignmentException;
import org.sakaiproject.assignment2.exception.NoGradebookDataExistsException;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.site.api.Group;

/**
 * This is the interface for logic which is related to the integration
 * with the Gradebook tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public interface ExternalGradebookLogic {

    /** 
     * @param gradedAssignments
     * @param contextId
     * @return Given a list of graded Assignment2 objects, filter out the objects
     * that the user is not authorized to view according to the gradebook
     * permissions and return a list of graded assignments that the user is
     * authorized to view. Also populates the gb-specific fields for each
     * Assignment2 object. If associated gradebook item has been deleted, sets the
     * gradebookItemId to null on the returned Assignment2 object.
     * Does NOT handle filtering according to AssignmentGroup 
     * restrictions
     */
	public List<Assignment2> getViewableGradedAssignments(List<Assignment2> gradedAssignments, String contextId);
	
	/**
	 * The Assignment2 tool stores all grading information in the gradebook. Thus,
	 * the gradebook backend must exist for the Assignment2 tool to work. This
	 * method will check to see if the gradebook data exists and, if not, will
	 * add it
	 * @param contextId
	 */
	public void createGradebookDataIfNecessary(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @return a map of gradebook item id to title for all of the gradebook
	 * items that the current user may view or grade.
	 */
	public Map<Long, String> getViewableGradebookItemIdTitleMap(String contextId);
	
	/**
	 * returns a list of GradebookItem objects that represent all of
	 * the gradebook items currently defined in the gradebook tool
	 * @param contextId
	 * @return
	 * @throws SecurityException if user does not have edit or grade perm
	 */
	public List<GradebookItem> getAllGradebookItems(String contextId);
	
	/**
	 * returns a list of all groups that the current user is authorized to view
	 * according to the gradebook grader permissions
	 * @param contextId
	 * @return
	 */
	public List<Group> getViewableGroupsInGradebook(String contextId);
	
	/**
	 * Using the grader permissions, returns a map of all of the student ids of 
	 * the students that the given user is allowed to view or grade
	 * to the actual function (grade/view)
	 * @param userId
	 * @param contextId
	 * @param gradebookItemId
	 * @return
	 * @throws SecurityException if the current user is not allowed to access student info for
	 * the given gb item
	 */
	public Map<String, String> getViewableStudentsForGradedItemMap(String userId, String contextId, Long gradebookItemId);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to edit the gradebook
	 */
	public boolean isCurrentUserAbleToEdit(String contextId);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to grade all in gradebook
	 */
	public boolean isCurrentUserAbleToGradeAll(String contextId);
	
	/**
     * @param contextId
     * @param userId
     * @return true if the given user is authorized to grade all in gradebook
     */
    public boolean isUserAbleToGradeAll(String contextId, String userId);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to grade in some 
	 * capacity in the gradebook.  (ie they may grade all or grade
	 * section)
	 */
	public boolean isCurrentUserAbleToGrade(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @param userId
	 * @return true if the given user is authorized to grade in some
	 * capacity in the gradebook. (ie they may grade all or grade section)
	 */
	public boolean isUserAbleToGrade(String contextId, String userId);
	
	/**
	 * @param contextId
	 * @return true if current user is authorized to view their own
	 * grades in the gradebook
	 */
	public boolean isCurrentUserAbleToViewOwnGrades(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @return true if the current user does not have grading or editing 
	 * privileges in the gradebook but does have the view own grades perm
	 */
	public boolean isCurrentUserAStudentInGb(String contextId);
	
	/**
	 * @contextId
	 * @param studentId
	 * @param gradebookItemId
	 * @return true if the current user is authorized to grade the given student
	 * for the gradebook item with the given gradebookItemId
	 */
	public boolean isCurrentUserAbleToGradeStudentForItem(String contextId, String studentId, Long gradebookItemId);
	
	/**
	 * 
	 * @param contextId
	 * @param studentId
	 * @param gbItemId
	 * @return AssignmentConstants.GRADE if current user is able to grade this item for this student,
	 * AssignmentConstants.VIEW if user is only able to view the grade, and null if
	 * the user may not view this student for the given item at all
	 */
	public String getGradeViewPermissionForCurrentUserForStudentForItem(String contextId, String studentId, Long gbItemId);
	
	/**
	 * 
	 * @param contextId
	 * @param studentId
	 * @param gradebookItemId
	 * @return the grade in the gb for the given gradebookItemId and student. null if no
	 * grade or if the gb item does not exist. 
	 * @see #getGradeInformationForStudent(String, Long, String) This method is used
	 * for retrieving comment info. Look here for thrown exceptions
	 */
	public String getStudentGradeForItem(String contextId, String studentId, Long gradebookItemId);
	
	/**
	 * 
	 * @param contextId
	 * @param studentId
	 * @param gradebookItemId
	 * @return the grade comment in the gb for the given gradebookItemId and student. null if no
	 * comment or if the gb item does not exist.  
     * @see #getGradeInformationForStudent(String, Long, String) This method is used
     * for retrieving grade info. Look here for thrown exceptions
	 */
	public String getStudentGradeCommentForItem(String contextId, String studentId, Long gradebookItemId);
	

	/**
	 * 
	 * @param contextId
	 * @param submission
	 * @return a GradeInformation object containing the grade information from the
	 * Gradebook for the given submission's assignment's associated gb item.
	 * Returns an "empty" GradeInformation object if the assignment is ungraded or
	 * gb item does not exist. 
     * @see #getGradeInformationForStudent(String, Long, String) This method is used
     * for retrieving grade info. Look here for thrown exceptions
	 */
	public GradeInformation getGradeInformationForSubmission(String contextId, AssignmentSubmission submission);
	
	/**
	 * 
	 * @param contextId not null
	 * @param gradebookItemId not null
	 * @param studentId not null
	 * @return a GradeInformation object containing the grade information from the
     * Gradebook for the given studentId and gradebookItemId.
     * Returns an "empty" GradeInformation object if the gb item does not exist - may
     * indicate it was deleted from the gradebook tool and the a2 link is stale
     * @throws SecurityException if user is not authorized to view grade info for
     * the student for the gb item in the Gradebook tool
     * @throws GradebookNotFoundException if no gradebook exists in the given contextId
	 */
	public GradeInformation getGradeInformationForStudent(String contextId, Long gradebookItemId, String studentId);
	
	/**
	 * Create a gradebook item in the gradebook tool with the given information.
	 * @param contextId
	 * @param title
	 * @param pointsPossible
	 * @param dueDate
	 * @param releasedToStudents - true if this item should be available to students
	 * @param countedInCourseGrade - true if grades for this gb item will be included
	 * 			in course grade - may only be true if releasedToStudents is true
	 * @return id of the newly created gradebook item in the gradebook.
	 */
	public Long createGbItemInGradebook(String contextId, String title, Double pointsPossible, Date dueDate,
			boolean releasedToStudents, boolean countedInCourseGrade);
	
	/**
	 * @param contextId
	 * @param gradebookItemId
	 * @return GradebookItem object associated with the given gradebookItemId
     * in the given contextId in the gradebook tool
     * @throws GradebookItemNotFoundException if no gradebook item exists with the given gradebookItemId
	 */
	public GradebookItem getGradebookItemById(String contextId, Long gradebookItemId);
	
	/**
	 * Save the given grade and comment for the given student, gb item, and context
	 * in the gradebook. 
	 * @param contextId
	 * @param gradebookItemId
	 * @param studentId
	 * @param grade
	 * @param comment
	 * @throws InvalidGradeForAssignmentException if the grade is invalid according
	 * to the gradebook's grade entry type
	 * @throws SecurityException if user is not auth to grade the student
	 * @throws NoGradebookDataExistsException if there is no gradebook data in the given context
	 * @throws GradebookItemNotFoundException if there is no gradebook item with the
	 * associated gradebookItemId
	 * 
	 */
	public void saveGradeAndCommentForStudent(String contextId, Long gradebookItemId, 
			String studentId, String grade, String comment) throws InvalidGradeForAssignmentException;
	
	/**
	 * Given a list of GradeInformation objects with the gradebookGrade and gradebookComment
	 * information populated with the info you want to update, will update the
	 * grades and comments in the gradebook. GradeInformation must all be for the same
	 * gradebook item
	 * @param contextId
	 * @param gradebookItemId - the id of the associated gradebook item
	 * @param gradeInfoList - list of GradeInformation objects populated with
	 * the grade and comment you want to update. Note: this will save whatever
	 * you have passed for grade AND comment, even if they are null
	 * @throws InvalidGradeForAssignmentException if any passed grade is invalid according
	 * to the gradebook's grade entry type
	 * @throws SecurityException if user is not auth to grade any student in the list
	 * @throws NoGradebookDataExistsException if there is no gradebook data in the given context
	 * @throws GradebookItemNotFoundException if there is no gradebook item with the
	 * associated gradebookItemId
	 */
	public void saveGradesAndComments(String contextId, Long gradebookItemId, 
			List<GradeInformation> gradeInfoList);
	
	/**
	 * 
	 * @param contextId
	 * @return true if gradebook data exists in the given contextId. This does
	 * not mean that the tool is in the site, it indicates that the backend data
	 * is there for integration
	 */
	public boolean gradebookDataExistsInSite(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @param studentIdToGradeMap - a map of studentId to the entered grade
	 * 			
	 * @return a list of studentIds associated with invalid grades according
	 * to the gradebook.  returns empty list if all grades are valid
	 */
	public List<String> identifyStudentsWithInvalidGrades(String contextId, Map<String, String> studentIdToGradeMap);
	
	/**
	 * 
	 * @param contextId
	 * @param grade
	 * @return true if the given grade is valid for the gradebook. if you are
	 * processing more than one student, use getStudentsWithInvalidGrades for
	 * efficiency
	 */
	public boolean isGradeValid(String contextId, String grade);
	
	/**
	 * 
	 * @param studentIdList
	 * @param contextId
	 * @param gradebookItemId the id of the gradebook item associated with the assignment
	 * @return a map of the studentId to the GradeInformation record populated
	 * with the student's grade info from the gradebook item associated with the
	 * given assignment.
	 * @throws IllegalArgumentException - if contextId or gradebookItemId are null
	 * @throws SecurityException - if the current user is not authorized to
	 * view grade information for a student in the list for the assoc gb item
	 */
	public Map<String, GradeInformation> getGradeInformationForStudents(Collection<String> studentIdList, String contextId, Long gradebookItemId);
	
	/**
	 * 
	 * @param contextId
	 * @param gradebookItemId
	 * @return true if the current user may view this gradebook item in the gradebook
	 */
	public boolean isCurrentUserAbleToViewGradebookItem(String contextId, Long gradebookItemId);
	
	/**
	 * Assign the given grade to all students in the list who do not have a grade
	 * yet (grade is null or empty string) for this gradebook item.
	 * @param contextId
	 * @param gradebookItemId - the id of the gradebook item the assignment is associated with
	 * @param studentIds - ids of the students that the current user is allowed to grade for this assignment
	 * @param grade non-null grade to be saved for all of the students who do not yet have a grade
	 * @throws InvalidGradeException if the passed grade is not valid for this gradebook item
	 * @throws SecurityException - if the current user is not authorized to
     * grade a student in the list for the assoc gb item
	 */
	public void assignGradeToUngradedStudents(String contextId, Long gradebookItemId, List<String> studentIds, String grade);
	
	/**
	 * Modify the gradebook item with the given gradebookItemId to release or
	 * retract the grade information to students. Note: if the grades are retracted,
	 * the grades are also changed to "not counted" in this gradebook item since 
	 * grades cannot be counted but not released
	 * @param contextId
	 * @param gradebookItemId
	 * @param release if true, will release grade info. if false, will retract grade info
	 * @param includeInCourseGrade if true and release is true, will also include grade info in the course
	 * grade. leave null if you do not want to modify this setting
	 * @throw {@link GradebookItemNotFoundException} if no gradebook item exists with the given gradebookItemId
	 */
	public void releaseOrRetractGrades(String contextId, Long gradebookItemId, boolean release, Boolean includeInCourseGrade);
	
	/**
	 * 
	 * @param contextId
	 * @return true if the gradebook is set up for grade entry by points
	 */
	public boolean isGradingByPoints(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @param gradebookItemId
	 * @return the lowest possible grade for the gb item with the given
	 * gradebookItemId in the gradebook. for example, it may return 0 or F or null
	 * depending on the gradebook and gb item settings
	 * @throws GradebookItemNotFoundException if no gradebook item exists with the given gradebookItemId
	 */
	public String getLowestPossibleGradeForGradebookItem(String contextId, Long gradebookItemId);
	
	/**
	 * 
	 * @param gbItemId
	 * @return true if the gradebook item with the given gbItemId exists in the Gradebook.
	 * Useful if you want to check if the gradebook item associated with an assignment
	 * still exists
	 */
	public boolean gradebookItemExists(Long gbItemId);
}
