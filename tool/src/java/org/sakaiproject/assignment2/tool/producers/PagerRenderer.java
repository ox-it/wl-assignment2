package org.sakaiproject.assignment2.tool.producers;

import org.sakaiproject.assignment2.tool.producers.*;
import org.sakaiproject.assignment2.tool.params.PagerViewParams;
import org.sakaiproject.assignment2.tool.beans.PagerBean;

import uk.org.ponder.rsf.components.ELReference;
import uk.org.ponder.rsf.components.UIBoundList;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import java.util.HashMap;
import java.util.Map;

public class PagerRenderer {

	private PagerBean pagerBean;
	
    public void makePager(UIContainer tofill, String divID, String currentViewID) {
    	
        UIJointContainer joint = new UIJointContainer(tofill, divID, "pagerDivContainer:", ""+1);
        
        //Currently Viewing
        UIMessage.make(joint, "pager_viewing", "assignment2.pager.viewing", 
        		new Object[] {pagerBean.getViewingStart(), pagerBean.getViewingEnd(), pagerBean.getViewingTotal()} );
        
        //Form
        UIForm form = UIForm.make(joint, "pager_form");
                
        //Select Box
        UISelect select_box = UISelect.make(form, "pager_select_box");
        select_box.selection = new UIInput();
        select_box.selection.valuebinding = new ELReference("#{PagerBean.currentSelect}");
        UIBoundList comboValues = new UIBoundList();
        comboValues.setValue(new String[] {"5","10","20","50","100","200"});
        select_box.optionlist = comboValues;
		UIBoundList comboNames = new UIBoundList();
		comboNames.setValue(new String[] {"Show 5 items", "Show 10 items", "Show 20 items", "Show 50 items", "Show 100 items", "Show 200 items"});
		select_box.optionnames = comboNames;
		Map attrmap = new HashMap(); 
		attrmap.put("onchange", "this.form.submit();");
		select_box.decorators = new DecoratorList(new UIFreeAttributeDecorator(attrmap)); 

				
		//Paging Buttons
		UICommand first_page = UICommand.make(form, "pager_first_page", 
				UIMessage.make("assignment2.pager.pager_first_page"), "#{PagerBean.goToFirstPage}");
		UICommand prev_page = UICommand.make(form, "pager_prev_page", 
				UIMessage.make("assignment2.pager.pager_prev_page"), "#{PagerBean.goToPrevPage}");
		UICommand next_page = UICommand.make(form, "pager_next_page", 
				UIMessage.make("assignment2.pager.pager_next_page"), "#{PagerBean.goToNextPage}");
		UICommand last_page = UICommand.make(form, "pager_last_page", 
				UIMessage.make("assignment2.pager.pager_last_page"), "#{PagerBean.goToLastPage}");
		
		Map disabledAttr = new HashMap();
		disabledAttr.put("disabled", "disabled");
		DecoratorList disabledDecoratorList = new DecoratorList(new UIFreeAttributeDecorator(disabledAttr));
		
		//Disable buttons not in use
		if (pagerBean.getCurrentStart() == 0){
			//disable if on first page
			first_page.decorators = disabledDecoratorList;
			prev_page.decorators = disabledDecoratorList;
		}
		if ((pagerBean.getCurrentStart() + pagerBean.getCurrentCount() ) > pagerBean.getTotalCount()){
			//disable if on last page
			next_page.decorators = disabledDecoratorList;
			last_page.decorators = disabledDecoratorList;
		}
        
    }
    
    public void setPagerBean(PagerBean pagerBean){
    	this.pagerBean = pagerBean;
    }
}
