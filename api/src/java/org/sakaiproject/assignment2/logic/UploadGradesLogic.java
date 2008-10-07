/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/tool/src/java/org/sakaiproject/assignment2/tool/beans/UploadBean.java $
 * $Id: UploadBean.java 49293 2008-05-22 15:30:04Z stuart.freeman@et.gatech.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation.
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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.exception.InvalidGradeForAssignmentException;

public interface UploadGradesLogic
{
	/**
	 * 
	 * @param file
	 * @return a list of each row of content that is then parsed into
	 * a list for the row's content for the given csv file. Assumes your
	 * file will contain a header row and this header row is not included
	 * in the returned content.
	 */
	public List<List<String>> getCSVContent(File file);
	
	/**
	 * Given the parsed file, will process the content and save the grades and comments.
	 * @param displayIdUserIdMap - map of displayId to userId for all of the students
	 * in the site. if a student in the content is not found in this map, it is skipped
	 * @param assignmentId
	 * @param parsedContent file content returned by getCSVContent
	 * @return a list of displayIds of students from the file who were not updated
	 * because the current user does not have grading perm for that particular student.
	 * the user must have some kind of grading privileges, though, or SecurityException is thrown
	 * @throws SecurityException if current user does not have general grading permission
	 * for the given assignment
	 * @throws InvalidGradeForAssignmentException if a grade contained in the content 
	 * is not valid
	 */
	public List<String> uploadGrades(Map<String, String> displayIdUserIdMap, Long assignmentId, List<List<String>> parsedContent);
	
	/**
	 * @param displayIdUserIdMap - map for converting the displayId to the
	 * internal id. if a displayId is found in the content that is not in this
	 * map, it is deemed invalid and added to the returned list
	 * @param parsedContent
	 * @return a list of the displayIds contained in the given parsedContent that are
	 * not associated with a member of the given site. useful for validating your
	 * content before you try to save anything
	 */
	public List<String> getInvalidDisplayIdsInContent(Map<String, String> displayIdUserIdMap, List<List<String>> parsedContent);
	
	/**
	 * 
	 * @param parsedContent
	 * @param contextId
	 * @return a list of the displayIds contained in the given parsedContent
	 * that are associated with invalid grades. useful for validating your
	 * content before you try to save anything
	 */
	public List<String> getStudentsWithInvalidGradesInContent(List<List<String>> parsedContent, String contextId);
}