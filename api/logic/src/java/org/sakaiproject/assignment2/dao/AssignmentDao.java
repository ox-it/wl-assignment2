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
	 * Used to identify the next sort index for new assignments
	 * @param contextId
	 * @return the highest sort index for the existing assignments; null if
	 * no assignments in context
	 */
	public Integer getHighestSortIndexInSite(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @return all of the assignments in the given site with the associated
	 * AssignmentGroup data populated
	 */
	public List<Assignment2> getAssignmentsWithGroups(String contextId);
	
	/**
	 * 
	 * @param assignmentId
	 * @return the Assignment2 object associated with the given id with the
	 * associated AssignmentGroup and AssignmentAttachment data populated
	 */
	public Assignment2 getAssignmentByIdWithGroupsAndAttachments(Long assignmentId);

}
