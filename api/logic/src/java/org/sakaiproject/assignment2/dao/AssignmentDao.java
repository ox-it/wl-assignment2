/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/api/logic/src/java/org/sakaiproject/assignment2/dao/AssignmentDao.java $
 * $Id: AssignmentDao.java 12544 2006-05-03 15:06:26Z wagnermr@iupui.edu $
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

package org.sakaiproject.assignment2.dao;

import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentAttachment;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentFeedbackAttachment;
import org.sakaiproject.assignment2.model.AssignmentSubmissionAttachment;
import org.sakaiproject.genericdao.api.CompleteGenericDao;

/**
 * Basic DAO functionality for the Assignment2 tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public interface AssignmentDao extends CompleteGenericDao {
	
	/**
	 * Returns list of Assignment objects mapped to a string value indicating
	 * whether this user may only view or may view/grade each assignment.
	 * Assignments that the user does not have permission to view or grade will
	 * not be returned.
	 * @param userId
	 * @return
	 */
	//public Map<Assignment2, String> getAvailableAssignments(String userId);
	
	/**
	 * 
	 * @param assignment
	 * @return the AssignmentAttachments associated with this assignment
	 */
	//public List<AssignmentAttachment> getAssignmentAttachments(Assignment2 assignment);
	
	/**
	 * @param assignment
	 * @return Returns list of AssignmentSubmissions associated with the given assignmentId
	 */
	//public List<AssignmentSubmission> getAssignmentSubmissions(Assignment2 assignment);

	/**
	 * 
	 * @param submission
	 * 			the associated AssignmentSubmission object
	 * @return the attachments (AssignmentSubmissionAttachment objects) associated
	 * with the given AssignmentSubmission. 
	 */
	//public List<AssignmentSubmissionAttachment> getAssignmentSubmissionAttachments(AssignmentSubmission submission);
	
	/**
	 * 
	 * @param submission
	 * @return the feedback attachments (AssignmentFeedbackAttachment objects) that were
	 * added to this AssignmentSubmission by the grader
	 */
	//public List<AssignmentFeedbackAttachment> getAssignmentFeedbackAttachments(AssignmentSubmission submission);

}