/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/api/logic/src/java/org/sakaiproject/assignment2/logic/ExternalGradebookLogicImpl.java $
 * $Id: ExternalGradebookLogic.java 12544 2006-05-03 15:06:26Z wagnermr@iupui.edu $
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

package org.sakaiproject.assignment2.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.GradebookItem;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;


/**
 * This is the implementation for logic to interact with the Sakai
 * Gradebook tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class ExternalGradebookLogicImpl implements ExternalGradebookLogic {

    private static Log log = LogFactory.getLog(ExternalGradebookLogicImpl.class);

    public void init() {
    	log.debug("init");
    }
    
    public List<Assignment2> getViewableAssignmentsWithGbData(List<Assignment2> gradedAssignments, String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("contextId is null in getViewableAssignmentsWithGbData");
    	}
    	
        GradebookService gradebookService = (org.sakaiproject.service.gradebook.shared.GradebookService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService"); 
    	
    	List<Assignment2> viewableAssignmentsWithGbData = new ArrayList();
    	if (gradedAssignments == null || gradedAssignments.isEmpty()) {
    		return viewableAssignmentsWithGbData;
    	}
    	
    	List<org.sakaiproject.service.gradebook.shared.Assignment> gbAssignments = 
    		gradebookService.getViewableAssignmentsForCurrentUser(contextId);
		
		Map goIdGbAssignmentMap = new HashMap();
		if (gbAssignments != null) {
			for (Iterator gbIter = gbAssignments.iterator(); gbIter.hasNext();) {
				org.sakaiproject.service.gradebook.shared.Assignment gbObject = 
					(org.sakaiproject.service.gradebook.shared.Assignment) gbIter.next();
				if (gbObject != null) {
					goIdGbAssignmentMap.put(gbObject.getId(), gbObject);
				}
			}
		}
		
		// there are 2 situations in which the gradable object associated with the assignment
		// is not included in the list returned from the gradebook:
		// 1) the user does not have permission to view that GO
		// 2) the GO was deleted from the gb
		
		for (Iterator assignIter = gradedAssignments.iterator(); assignIter.hasNext();) {
			Assignment2 gradedAssignment = (Assignment2) assignIter.next();
			if (gradedAssignment != null) {
				Long goId = gradedAssignment.getGradableObjectId();
				if (goId != null) {
					org.sakaiproject.service.gradebook.shared.Assignment gbItem =
						(org.sakaiproject.service.gradebook.shared.Assignment)goIdGbAssignmentMap.get(goId);
					if (gbItem != null) {
						gradedAssignment.setDueDate(gbItem.getDueDate());
						gradedAssignment.setPointsPossible(gbItem.getPoints());
						viewableAssignmentsWithGbData.add(gradedAssignment);
					} else {
						// TODO we need to do something if this GO was deleted!
						// check to see if this gradable object exists anymore
						if (!gradebookService.isGradableObjectDefined(goId)) {
							// then the GO was deleted!
						}
					}
				}
			}
		}
		
		return viewableAssignmentsWithGbData;
    }
    
    public void createGradebookDataIfNecessary(String contextId) {
    	// we need to check if there is gradebook data defined for this site. if not,
        // create it (but will not actually add the tool, just the backend)
    	
    	GradebookFrameworkService frameworkService = (org.sakaiproject.service.gradebook.shared.GradebookFrameworkService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookFrameworkService");
        if (!frameworkService.isGradebookDefined(contextId)) {
        	if (log.isInfoEnabled()) 
        		log.info("Gradebook data being added to context " + contextId + " by Assignment2 tool");
        	frameworkService.addGradebook(contextId, contextId);
        }
    }
    
    public Map getViewableGradableObjectIdTitleMap(String contextId) {
    	GradebookService gradebookService = (org.sakaiproject.service.gradebook.shared.GradebookService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService"); 
    	
    	List<org.sakaiproject.service.gradebook.shared.Assignment> viewableGbItems = 
    		gradebookService.getViewableAssignmentsForCurrentUser(contextId);
    	
    	Map<Long, String> idTitleMap = new HashMap();
    	if (viewableGbItems == null || viewableGbItems.isEmpty()) {
    		return idTitleMap;
    	}
    	
    	for (Iterator itemIter = viewableGbItems.iterator(); itemIter.hasNext();) {
    		org.sakaiproject.service.gradebook.shared.Assignment assign =
    			(org.sakaiproject.service.gradebook.shared.Assignment)itemIter.next();
    		
    		if (assign != null) {
    			idTitleMap.put(assign.getId(), assign.getName());
    		}
    	}
    	
    	return idTitleMap;
    }
    
    public List<GradebookItem> getViewableGradebookItems(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("null contextId passed to getViewableGradebookItems");
    	}
    	
    	List<GradebookItem> gradebookItems = new ArrayList();
    	
    	GradebookService gradebookService = (org.sakaiproject.service.gradebook.shared.GradebookService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService"); 
    	
    	List<org.sakaiproject.service.gradebook.shared.Assignment> viewableGbItems = 
    		gradebookService.getViewableAssignmentsForCurrentUser(contextId);
    	
    	if (viewableGbItems == null || viewableGbItems.isEmpty()) {
    		return gradebookItems;
    	}
    	
    	for (Iterator itemIter = viewableGbItems.iterator(); itemIter.hasNext();) {
    		org.sakaiproject.service.gradebook.shared.Assignment assign =
    			(org.sakaiproject.service.gradebook.shared.Assignment)itemIter.next();
    		
    		if (assign != null) {
    			GradebookItem item = 
    				new GradebookItem(assign.getId(), assign.getName(), assign.getPoints(), assign.getDueDate());
    			gradebookItems.add(item);
    		}
    	}
    	
    	return gradebookItems;
    }
    
    public Map<String, String> getViewableGroupIdToTitleMap(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("Null contextId passed to getViewableGroupIdToTitleMap");
    	}
    	
    	GradebookService gradebookService = (org.sakaiproject.service.gradebook.shared.GradebookService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookService"); 
    	
    	return gradebookService.getViewableSectionUuidToNameMap(contextId);
    }
    
    //This should only be a temporary method, replaced by a real helper tool in the gradebook
    public String getGradebookItemHelperUrl(String contextId) {
    	String url = "";
    	try {
	    	url = ServerConfigurationService.getPortalUrl() + "/tool/";
	    	final Site site = SiteService.getSite(contextId);
	    	
	    	ToolConfiguration gbTool = site.getToolForCommonId("sakai.gradebook.tool");
	    	url += gbTool.getId() + "/addAssignmentHelper.jsf";
	    	
    	}
    	catch (IdUnusedException e) {
    		
    	}	
    	return url;
    }

}