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

package org.sakaiproject.assignment2.logic;

import org.sakaiproject.assignment2.model.Assignment2;


/**
 * This is the interface for the Assignment object
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public interface AssignmentLogic {
	
	/**
	 * 
	 * @param assignmentId
	 * @return Returns the Assignment based on its assignmentId
	 */
	public Assignment2 getAssignmentById(Long assignmentId);
	
	/**
	 * Create or update an Assignment
	 * @param assignment
	 * 			the Assignment to create or update
	 */
	public void saveAssignment(Assignment2 assignment);
	
	/**
	 * Delete an Assignment
	 * @param assignment
	 * 			the Assignment to delete
	 */	
	public void deleteAssignment(Assignment2 assignment);

}