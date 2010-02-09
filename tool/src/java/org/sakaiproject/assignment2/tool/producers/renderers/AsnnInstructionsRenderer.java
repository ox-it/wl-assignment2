package org.sakaiproject.assignment2.tool.producers.renderers;

import org.sakaiproject.assignment2.model.Assignment2;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.producers.BasicProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Renders the read only view of the Assignment Instructions. This currently
 * involves the Text and Attachments. It does not render the title of the 
 * assignment currently, as it is tooled to be used inside pages and displays.
 * 
 * 
 * @author sgithens
 *
 */
public class AsnnInstructionsRenderer implements BasicProducer {

    // Dependency
    private ViewParameters viewParameters;
    public void setViewParameters(ViewParameters viewParameters) {
        this.viewParameters = viewParameters;
    }

    // Dependency
    private AttachmentListRenderer attachmentListRenderer;
    public void setAttachmentListRenderer (AttachmentListRenderer attachmentListRenderer) {
        this.attachmentListRenderer = attachmentListRenderer;
    }
    
    private AsnnToggleRenderer toggleRenderer;
    public void setAsnnToggleRenderer(AsnnToggleRenderer toggleRenderer) {
        this.toggleRenderer = toggleRenderer;
    }
    
    /**
     * 
     * @param tofill
     * @param divID
     * @param assignment
     * @param includeToggle true if the instructions should be toggleable
     * @param includeToggleBar true if the toggleable instructions should use a toggle bar
     * and not just the toggle arrow. if includeToggle is false, will ignore this property
     * @param toggleExpanded true if the toggle should be expanded. if includeToggle is false, will 
     * ignore this property
     */
    public void makeInstructions(UIContainer tofill, String divID, Assignment2 assignment, boolean includeToggle, boolean includeToggleBar, boolean toggleExpanded){

        UIJointContainer joint = new UIJointContainer(tofill, divID, "assn2-assignment-instructions-widget:");

        if (includeToggle) {
            String hoverText = messageLocator.getMessage("assignment2.instructions.toggle.hover");
            String heading = messageLocator.getMessage("assignment2.instructions.heading");
            
            toggleRenderer.makeToggle(joint, "instructions_toggle_section:", null, includeToggleBar, 
                    heading, hoverText, false, false, false, false, null);
        } else {
            UIMessage.make(joint, "instructions_heading", "assignment2.instructions.heading");
        }
        
        UIOutput instructionsSection = UIOutput.make(joint, "instructionsSection");
        if (includeToggle) {
            // everything below the toggle is a subsection
            instructionsSection.decorate(new UIFreeAttributeDecorator("class", "toggleSubsection subsection1"));
            
            // should we hide or show the instructions section?
            if(!toggleExpanded) {
                instructionsSection.decorate(new UIFreeAttributeDecorator("style", "display: none;"));
            }
            
            // display a different heading for the attachments
            UIMessage.make(joint, "toggle_attach_heading", "assignment2.instructions.attachments");
        } else {
            UIMessage.make(joint, "attach_heading", "assignment2.instructions.attachments");
        }
        
        // Instructions
        if (assignment.getInstructions() == null || assignment.getInstructions().equals("")) {
            UIMessage.make(joint, "instructions", "assignment2.instructions.none");
        }
        else {
            UIVerbatim.make(joint, "instructions", assignment.getInstructions());
        }
        
        if (assignment.getAttachmentSet() != null && !assignment.getAttachmentSet().isEmpty()) {
            UIOutput.make(joint, "assignAttachmentsFieldset");
            attachmentListRenderer.makeAttachmentFromAssignmentAttachmentSet(tofill, "assign_attach_list:", viewParameters.viewID, 
                    assignment.getAttachmentSet());
        }
    }

    public void fillComponents(UIContainer parent, String clientID) {

    }
    
    private MessageLocator messageLocator;
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

}
