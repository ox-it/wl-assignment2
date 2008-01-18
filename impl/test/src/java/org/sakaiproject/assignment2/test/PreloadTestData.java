/******************************************************************************
 * PreloadDataImpl.java - created by aaronz@vt.edu
 * 
 * Copyright (c) 2007 Virginia Polytechnic Institute and State University
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 * 
 * Contributors:
 * Aaron Zeckoski (aaronz@vt.edu) - primary
 * 
 *****************************************************************************/

package org.sakaiproject.assignment2.test;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.dao.AssignmentDao;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;


/**
 * This preloads data needed for testing<br/>
 * Do not load this data into a live or production database<br/>
 * Load this after the normal preload<br/>
 * Add the following (or something like it) to a spring beans def file:<br/>
 * <pre>
	<!-- create a test data preloading bean -->
	<bean id="org.sakaiproject.assignment.test.PreloadTestData"
		class="org.sakaiproject.assignment.test.PreloadTestData"
		init-method="init">
		<property name="assignmentDao"
			ref="org.sakaiproject.assignment.dao.EvaluationDao" />
		<property name="pdl"
			ref="org.sakaiproject.assignment.dao.PreloadData" />
	</bean>
 * </pre>
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class PreloadTestData {

	private static Log log = LogFactory.getLog(PreloadTestData.class);

	private AssignmentDao assignmentDao;
	public void setAssignmentDao(AssignmentDao assignmentDao) {
		this.assignmentDao = assignmentDao;
	}

	private AssignmentTestDataLoad atdl;
	/**
	 * @return the test data loading class with copies of all saved objects
	 */
	public AssignmentTestDataLoad getAtdl() {
		return atdl;
	}

	public void init() {
		log.info("INIT");
		preloadDB();
	}

	/**
	 * Preload the data
	 */
	public void preloadDB(){
		log.info("preloading DB...");
		
		
		atdl = new AssignmentTestDataLoad();
		atdl.createTestData(assignmentDao);
	}
}