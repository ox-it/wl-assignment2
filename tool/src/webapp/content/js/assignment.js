function a2SetMainFrameHeight(){
	if (iframeId != ""){
		if(arguments[0] != null){
			height = arguments[0];
		} else {
			height = jQuery(document).height();// + 10;
		}
		jQuery("#" + iframeId, parent.document).height(height);
	}
}


groups_toggle = function(){
	el = jQuery("input[type='radio'][value='false'][name='page-replace\:\:access_select-selection']").get(0);
	if (el && el.checked) {
		jQuery('li#groups_table_li').hide();
	} else {
		jQuery('li#groups_table_li').show();
	}
}

function toggle_group_checkboxes(check_all_box){
	if (check_all_box.checked){
		jQuery('table#groupTable :checkbox').attr('checked', 'checked');
	} else {
		jQuery('table#groupTable :checkbox').removeAttr('checked');
	}
}

function update_resubmit_until(){
	el = jQuery("input:checkbox[@name='page-replace\:\:resubmit_until']").get(0);
	if (el){
	if (el.checked) {
		jQuery(".resubmit_until_toggle").show();
	} else {
		jQuery(".resubmit_until_toggle").hide();
	}
	}
}

function override_submission_settings(){
    override_checkbox = jQuery("input:checkbox[@name='page-replace\:\:override_settings']").get(0);
    
    if (override_checkbox){
        if (override_checkbox.checked) {
            // change text back to normal
            jQuery("#override_settings_container").removeClass("inactive");
            
            // enable all of the form elements
            jQuery("select[@name='page-replace\:\:resubmission_additional-selection']").removeAttr("disabled");
            jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").removeAttr("disabled");
            // TODO - get the rsf date widget to work when these are disabled -it
            // is throwing syntax error upon submission
            //jQuery("input[@name='page-replace\:\:accept_until\:1\:date-field']").removeAttr("disabled");
           //jQuery("input[@name='page-replace\:\:accept_until\:1\:time-field']").removeAttr("disabled");
        } else {
            // gray out the text
            jQuery("#override_settings_container").addClass("inactive");
            
            // disable all form elements
            jQuery("select[@name='page-replace\:\:resubmission_additional-selection']").attr("disabled","disabled");
            jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").attr("disabled","disabled");
           // jQuery("input[@name='page-replace\:\:accept_until\:1\:date-field']").attr("disabled","disabled");
            //jQuery("input[@name='page-replace\:\:accept_until\:1\:time-field']").attr("disabled","disabled");
        }
    }
}

function set_accept_until_on_submission_level(){
    el = jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").get(0);
    if (el){
        if (el.checked) {
            jQuery("#accept_until_container").show();
        } else {
            jQuery("#accept_until_container").hide();
        }
    }
}

jQuery(document).ready(function(){
	update_resubmit_until();
	asnn2.initializeSorting();
});

function slide_submission(img){
	jQuery(img).parent('h4').next('div').toggle();
	flip_image(img)
}
function slideFieldset(img) {
	jQuery(img).parent('legend').next('ol').toggle();
	flip_image(img);
	a2SetMainFrameHeight();
}
function slideDiv(img) {
    jQuery(img).parent('legend').next('div').toggle();
    flip_image(img);
    a2SetMainFrameHeight();
}
function flip_image(img){
	if(img.src.match('collapse')){
		img.src=img.src.replace(/collapse/, 'expand');
	}else{
		img.src=img.src.replace(/expand/, 'collapse');
	}
}


function updateAttachments(imgsrc, filename, link, ref, filesize){
	//Check if current last row is "demo"
   if (jQuery("#attachmentsFieldset ol:first li:last").hasClass("skip")){
   	newRow = jQuery("#attachmentsFieldset ol:first li:last").get(0);
   	jQuery(newRow).removeClass("skip");
   } else {
    newRow = jQuery("#attachmentsFieldset ol:first li:last").clone(true).appendTo("#attachmentsFieldset ol:first").get(0);
   }
   jQuery("img", newRow).attr("src",imgsrc);
   jQuery("a:first", newRow).attr("href", link);
   jQuery("a:first", newRow).html(filename);
   jQuery("input", newRow).attr("value", ref);
   jQuery("span:first", newRow).html(filesize);
   
   updateDisplayNoAttachments();
}

function removeAttachment(attach) {
    // we need to leave at least one "skipped" demo for use for new attachments
    var li = jQuery("#attachmentsFieldset li");
    if (li.size() <=1) {
        // this is the only one in the list, so we need to just
        // change it to "skipped"
        jQuery(attach).parent('span').parent('li').addClass("skip");
    } else {
        jQuery(attach).parent('span').parent('li').remove();
    }
}

function updateDisplayNoAttachments(){
    var li = jQuery("#attachmentsFieldset li").get(0);
    if (li) {
        var skipped = jQuery("#attachmentsFieldset ol:first li:last").hasClass("skip");
        if (skipped) {
            jQuery("span.no_attachments_yet").show();
        } else {
            jQuery("span.no_attachments_yet").hide();
        }
    } else {
        jQuery("span.no_attachments_yet").show();
    }
}

/*
 * Some functions and utilities which may be useful outside of Assignments2
 */
var asnn2util = asnn2util || {};

(function (jQuery, asnn2util) {
	/**
     * Turns on the 2.x portals background overlay
     */
    asnn2util.turnOnPortalOverlay = function() {
	jQuery("body", parent.document).append('<div id="portalMask" style="position:fixed;width:100%;height:100%;"></div>');
    	jQuery("#" + iframeId, parent.document).css("z-index", "9001").css("position", "relative").css("background", "#fff");
    };
    
    /**
     * Turns off the 2.x portal background overlay
     */
    asnn2util.turnOffPortalOverlay = function() {
    	jQuery("#portalMask", parent.document).trigger("unload").unbind().remove();
        jQuery("#" + iframeId, parent.document).css("z-index", "0");
    };
	
    /**
     * This opens the jQuery object, hopefully representing a hidden element
     * somewhere on the page, as a dialog.  In addition to opening as a modal
     * dialog, it has support for blanking out the background portion of the 
     * Sakai 2.x Series Portal.
     * 
     * @param dialogObj  Should be a jQuery object for the hidden element to
     * be used as the dialog.
     */
    asnn2util.openDialog = function(dialogObj) {
    	dialogOptions = {
            resizable: false,
            width: 520,
            modal: true,
            overlay: {
                opacity: 0.5,
                background: "#eee"
            }
        };
    	asnn2util.turnOnPortalOverlay();
    	dialogObj.dialog(dialogOptions).show();
    };
    
    /**
     * This will close dialog that was opened with asnn2util.openDialog.
     * 
     * @param dialogObj The jQuery object representing the element being used
     * as the modal dialog.
     */
    asnn2util.closeDialog = function(dialogObj) {
    	dialogObj.dialog('destroy');
    	// Remove our event handlers so they are created fresh each time.
        asnn2util.turnOffPortalOverlay();
    };
	
})(jQuery, asnn2util);

var asnn2 = asnn2 || {};

(function (jQuery, asnn2) {

    // for reordering of assignments
    asnn2.saveOrdering = function(serializedString) {
        serializedString = serializedString.replace(/\[\]/g, "").replace(/row\ /g,"");
        if (serializedString) {
            serializedString = serializedString.replace(/&/g,"");
            var orderedAssignIds = serializedString.split("sortable=");
            var queries = new Array();
            queries.push(RSF.renderBinding("ReorderAssignmentsAction.orderedAssignIds",orderedAssignIds));
            queries.push(RSF.renderActionBinding("ReorderAssignmentsAction.execute"))
            var body = queries.join("&");
            jQuery.post(document.URL, body);
        }
    }
    
    gbItemName = "";
    gbDueTime = "";

    asnn2.finishedGBItemHelper = function(newGbItemName, newGbDueTime){
        gbItemName = newGbItemName;
        gbDueTime = newGbDueTime;
    }
    
    /**
     * pushes the due date from the "add gradebook item" helper to the due date field on the "Add Assignment" screen
     */
    asnn2.populateDueDateWithGBItemDueDate = function() {
        // will be empty if no due date required
        if (gbDueTime != "") {
    	var require_due_date = jQuery("input[name='page-replace\:\:require_due_date']").get(0);
            var curr_req_due_date = require_due_date.checked;
            
            // if it doesn't currently req due date, we will replace it with gb item due date
            if (!curr_req_due_date) {
                // set the text box holding the date
                var dueDate = new Date();
                dueDate.setTime(gbDueTime);
                var dateInput = jQuery("input[name='page-replace\:\:due_date\:1\:date-field']");
                dateInput.val(dueDate.formatDueDate());
                dateInput.change(); // need a change event to update the calendar widget
                
                // enable the due date section
                require_due_date.checked = true;
                var due_date_container = jQuery('#page-replace\\:\\:require_due_date_container').get(0);
                asnn2.showHideByCheckbox(require_due_date, due_date_container);
            }
        }
    }
    
    /**
     * Since Windows and Mac display different values for date.toLocaleDateString(),
     * we need make sure the display is consistently xx/yy/zz format instead
     * of January 1, 2008 format.  This method will use the PUC_DATE_FORMAT
     * used by the date picker to define the locale-aware date format we
     * should use for populating the due date
     */
    Date.prototype.formatDueDate = function() {
	// uses the PUC_DATE_FORMAT variable used by the date widget
	// as the default format
	var date = this;
	var month = (date.getMonth() + 1).toString(); // month is 0-based
	var day = date.getDate().toString();
	var fullYear = date.getFullYear().toString();
	var twoDigitYear = fullYear.substr(2);
	
	var dateFormat = PUC_DATE_FORMAT;
	if (!dateFormat) {
	    dateFormat = "M/d/yy";
	}

	var formattedDueDate = dateFormat;
	// check for use of MM, DD, and YYYY before we replace
	if (dateFormat.indexOf("MM") != -1) {
	    formattedDueDate = formattedDueDate.replace("MM", month);
	} else {
	    formattedDueDate = formattedDueDate.replace("M", month);
	}
	
	if (dateFormat.indexOf("dd") != -1) {
	    formattedDueDate = formattedDueDate.replace("dd", day);
	} else {
	    formattedDueDate = formattedDueDate.replace("d", day);
	}
	
	if (dateFormat.indexOf("yyyy") != -1) {
	    formattedDueDate = formattedDueDate.replace("yyyy", fullYear);
	} else {
	    formattedDueDate = formattedDueDate.replace("yy", twoDigitYear);
	}
	
	return formattedDueDate;
    }
    
    /**
     * automatically select the newly created gb item created via the helper
     */ 
    asnn2.populateTitleWithGbItemName = function() {
        var curr_title = jQuery("input[name='page-replace\:\:title']").get(0).value;
        if (!curr_title || curr_title == "") {
            // get the currently selected gb item
            var gbSelect = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
            var gbSelIndex = gbSelect.selectedIndex;
            if (gbSelIndex != 0) { 
                var selectedItem = gbSelect.options[gbSelIndex].text;
                // replace the empty title field with the new_title
                jQuery("input[name='page-replace\:\:title']").val(selectedItem);
            }
        }
    }

    /**
     * Select the graded/ungraded radio button depending on whether a gb item
     * has been selected in the drop down
     */
    asnn2.selectGraded = function() {
        el = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
        if (el.selectedIndex != 0){
            jQuery("input[type='radio'][id='page-replace\:\:select_graded']").get(0).checked=true;
        } else {
            jQuery("input[type='radio'][id='page-replace\:\:select_ungraded']").get(0).checked=true;
        }
    }
    
    /**
     * change the selected gb item based upon the gbItemName variable
     */
    asnn2.changeValue = function(){   
        el = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
        if(el){
            for(i=0;i<el.length;i++){
                if(el.options[i].text == gbItemName){
                    el.selectedIndex = i;  
                }
            }
        }

        asnn2.selectGraded();
        asnn2.populateTitleWithGbItemName();
        asnn2.populateDueDateWithGBItemDueDate();
    }
    
    /**
     * if user has entered an assignment title before clicking the 
     * "Add Gradebook Item" helper link, this method will add that name
     * as a parameter so that the helper is auto-populated with the 
     * assignment title
     */
    asnn2.update_new_gb_item_helper_url = function() {
        var gbUrlWithoutName = jQuery("a[id='page-replace\:\:gradebook_url_without_name']").attr("href");
        var new_title = jQuery("input[name='page-replace\:\:title']").get(0).value
        
        // encode unsafe characters that may be in the assignment title
       
        var escaped_title = "";
        if (new_title) {
            escaped_title = escape(new_title);
        }
        
        var modifiedUrl = gbUrlWithoutName + "&name=" + escaped_title;
        
        jQuery("a[id='page-replace\:\:gradebook_item_new_helper']").attr("href", modifiedUrl);
    }
    
    /**
     * If due date changes and no accept until date has been set, populate
     * the accept until text field with the new due date. This does not
     * select the accept until date as "required," it just sets the default
     * value if someone then goes on to require it
     */
    asnn2.populate_accept_until_with_due_date = function() {
	var require_accept_until = jQuery("input[name='page-replace\:\:require_accept_until']").get(0);
	if (!require_accept_until.checked) {
	    // get the due date 
	    var dueDate = jQuery("input[name='page-replace\:\:due_date\:1\:date-field']").val();
	    if (dueDate) {
		var acceptUntilDate = jQuery("input[name='page-replace\:\:accept_until\:1\:date-field']");
		acceptUntilDate.val(dueDate);
		acceptUntilDate.change();
	    }
	}
    }
    
    /**
     * If due time changes and no accept until has been set, populate the accept until time
     * with the due time. This does not select the accept until date as "required," it
     * just sets the default value if someone then goes on to require it.
     */
    asnn2.populate_accept_until_time_with_due_time = function() {
	var require_accept_until = jQuery("input[name='page-replace\:\:require_accept_until']").get(0);
	if (!require_accept_until.checked) {
	    // get the due time 
	    var dueTime = jQuery("input[name='page-replace\:\:due_date\:1\:time-field']").val();
	    if (dueTime) {
		var acceptUntilTime = jQuery("input[name='page-replace\:\:accept_until\:1\:time-field']");
		acceptUntilTime.val(dueTime);
	    }
	}
    }
    
    //Sorting functions
    var sortBy; 
    var sortDir; 
    var pStart=0; 
    var pLength=5;
    
    function sortPageRows(b,d) {
       pLength = jQuery("select[name='page-replace\:\:pagerDiv:1:pager_select_box-selection']").val();
       sortBy=b; sortDir=d;
       var trs = jQuery.makeArray(jQuery("table#sortable tr:gt(0)"));
       trs.sort(function(a,b){
          return (jQuery("." + sortBy, a).get(0).innerHTML.toLowerCase() < jQuery("." + sortBy, b).get(0).innerHTML.toLowerCase()
          ? -1 : (jQuery("." + sortBy, a).get(0).innerHTML.toLowerCase()> jQuery("." + sortBy, b).get(0).innerHTML.toLowerCase()
             ? 1 : 0));
       });
       if (sortDir == 'desc') {trs.reverse();}
       jQuery(trs).appendTo(jQuery("table#sortable tbody"));
       jQuery("a img", "table#sortable tr:first").remove();
       imgSrc = "<img src=\"/sakai-assignment2-tool/content/images/bullet_arrow_" + (d == 'asc' ? 'up' : 'down') + ".png\" />";
       jQuery("a." + b, "table#sortable tr:first").append(imgSrc);
       //Now paging
       jQuery("table#sortable tr:gt(0)").hide();
       jQuery("table#sortable tr:gt(" + pStart + "):lt(" + pLength +")").show();
       trsLength = jQuery("table#sortable tr:gt(0)").size();
       if (pStart == 0){
         jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_first_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_prev_page']").attr('disabled', 'disabled');
       } else {
          jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_first_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_prev_page']").removeAttr('disabled');
       }
       if (Number(pStart) + Number(pLength) >= trsLength) {
          jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_next_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_last_page']").attr('disabled', 'disabled');
       } else {
          jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_next_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_last_page']").removeAttr('disabled');  
       }
       
       //now parse the date
       format = jQuery("div.pagerDiv div.pager_viewing_format").get(0).innerHTML;
       format = format.replace(/\{0\}/, Number(pStart) + 1);
       last = Number(pStart) + Number(pLength) > trsLength ? trsLength : Number(pStart) + Number(pLength);
       format = format.replace(/\{1\}/, last);
       format = format.replace(/\{2\}/, jQuery("table#sortable tr:gt(0)").size());
       jQuery("div.pagerDiv div.pagerInstruction").html(format);
       
       // align the grading box
       asnn2.alignGrading();

    };
    
    asnn2.initializeSorting = function() {
        if (jQuery("table#sortable").get(0)) {
            sortPageRows(defaultSortBy,'asc');
            pStart=0;
        } 
    };
    
    asnn2.changeSort = function(newBy) {
        sortPageRows(newBy, (newBy!=sortBy ? 'asc' : (sortDir == 'asc' ? 'desc' : 'asc')));
    };
    
    asnn2.changePage = function(newPStart){
        total = jQuery("table#sortable tr:gt(0)").size();
        if ("first" == newPStart) {
            pStart = 0;
        } else if ("prev" == newPStart) {
            pStart = pStart - pLength;
            if (pStart < 0) pStart =0;
        } else if ("next" == newPStart) {
            pStart = Number(pStart) + Number(pLength);
            if (pStart > total) {
                pStart = total - (total % pLength);
            }
        } else if ("last" == newPStart) {
            if (total > pLength) {
                pStart = total - (total % pLength);
            } else {
                pStart = 0;
            }
        }
       sortPageRows(sortBy, sortDir);   
    };
    
    
})(jQuery, asnn2);



/* New Asnn2 functions that are namespaced. Will need to go back
 * and namespace others eventually.
 */

(function (jQuery, asnn2) {
    var EXPAND_IMAGE = "/sakai-assignment2-tool/content/images/expand.png";
    var COLLAPSE_IMAGE = "/sakai-assignment2-tool/content/images/collapse.png";
    var NEW_FEEDBACK_IMAGE = "/library/image/silk/email.png";
    var READ_FEEDBACK_IMAGE = "/library/image/silk/email_open.png";
    
    asnn2.toggle_hideshow_by_id = function (arrowImgId, toggledId) {
        toggle_hideshow(jQuery('#'+arrowImgId.replace(/:/g, "\\:")),
                        jQuery('#'+toggledId.replace(/:/g, "\\:")));
    }
    
    function toggle_hideshow(arrowImg, toggled) {
        if (arrowImg.attr('src') == EXPAND_IMAGE) {
            arrowImg.attr('src', COLLAPSE_IMAGE);
            toggled.show();
        }
        else {
            arrowImg.attr('src', EXPAND_IMAGE);
            toggled.hide();
        }
    };
    
    function mark_feedback(submissionId, versionId) {
        var queries = new Array();
        queries.push(RSF.renderBinding("MarkFeedbackAsReadAction.asnnSubId",submissionId));
        queries.push(RSF.renderBinding("MarkFeedbackAsReadAction.asnnSubVersionId",versionId));
        queries.push(RSF.renderActionBinding("MarkFeedbackAsReadAction.execute"))
        var body = queries.join("&");
        jQuery.post(document.URL, body);
    };
    
    /**
     * Custom javascript for the Assignment Add/Edit page. When the "Require
     * Submissions" box is checked or unchecked, the Submission Details below it
     * need to show/hide. It does this by hiding/removing all siblings at the
     * same level (ie. the rest of the <li/> in the current list). it also
     * needs to show/hide the notifications block, as well
     * 
     * @param element The jQuery element whose downstream siblings will be 
     * shown or hidden
     * @param show Whether or not to show them.
     */
    asnn2.showHideSiblings = function(element, show) {
    	if (show == true) {
    		jQuery(element).nextAll().show();
    		jQuery("#notifications").show();
    	}
    	else {
    		jQuery(element).nextAll().hide();
    		jQuery("#notifications").hide();
    	}
    };
    
    /**
     * Given a checkbox element, hide or show the area element whenever checkbox
     * is clicked.  Checking the box shows the area, unchecking the box hides
     * the area. 
     */
    asnn2.showHideByCheckbox = function(checkboxElem, areaElem) {
    	var area = jQuery(areaElem);
    	if (checkboxElem.checked) {
    		area.show();
    	}
    	else {
    		area.hide();
    	}
    };

    /**
     * Setup the element for a Assignment Submission Version. This includes
     * hooking up the (un)collapse actions, as well as the Ajax used to mark
     * feedback as read when the div is expanded.
     *
     * If the markup changes, this will need to change as well as it depends
     * on the structure.
     */
    asnn2.assnSubVersionDiv = function (elementId, feedbackRead, submissionId, versionId, readFBAltText) {
        var escElemId = elementId.replace(/:/g, "\\:");
        var versionHeader = jQuery('#'+escElemId+ ' h3');
        var arrow = versionHeader.find("img:first");
        var toggled = jQuery('#'+escElemId+ ' div')
        var envelope = versionHeader.find("img:last");
        versionHeader.click(function() {
            toggle_hideshow(arrow, toggled);
            if (envelope.attr('src') == NEW_FEEDBACK_IMAGE) {
                envelope.attr('src', READ_FEEDBACK_IMAGE);
                envelope.attr('alt', readFBAltText);
                envelope.attr('title', readFBAltText);
                mark_feedback(submissionId, versionId);
            }
        });
    };
    
    /**
     * Used to generate the confirmation dialog for When the student clicks
     * submit on either the Edit Submission or Preview Submission page. 
     */
    asnn2.studentSubmissionConfirm = function(buttonform) {
        // first, let's make sure the user has checked the honor pledge, if needed.
        // look for the honor pledge checkbox
        var honor_pledge = jQuery('#page-replace\\:\\:portletBody\\:1\\:assignment-edit-submission\\:\\:honor_pledge').get(0);
        if (honor_pledge) {
            if (!honor_pledge.checked) {
                //display the error and return
                jQuery('#page-replace\\:\\:portletBody\\:1\\:assignment-edit-submission\\:\\:honor_pledge_error').show();
                return false;
            }
        }
        
        // display the confirmation dialog
        confirmDialog = jQuery('#submit-confirm-dialog');

        var submitButton = buttonform;
        jQuery('#submission-confirm-button').click( function (event) {
        	asnn2util.closeDialog(confirmDialog);
            submitButton.onclick = function (event) { return true };
            submitButton.click();
        });

        jQuery('#submission-cancel-button').click( function (event) {
        	asnn2util.closeDialog(confirmDialog);
        });

        asnn2util.openDialog(confirmDialog);
        return false;
    };
    
    
    
    /**
     * Aligns the "Apply to Unassigned" box with the grading column 
     */
    asnn2.alignGrading = function() {
        var p = jQuery("td.grade:first");
        var position = p.position();
        if (position) {
            var applyToUnassigned = jQuery('#unassigned-apply');
            applyToUnassigned.attr("style", "margin-left:" + position.left + "px");
        }
    };
    
    /**
     * Release/Retract all feedback confirmation dialog
     * 
     * This function uses the asnn2 dialog utility. The same function
     * is used for both releasing and retracting feedback, distinguished by the 
     * "release" boolean parameter. We just use string substitution to
     * differentiate the release and retract dialogs. Be careful if you
     * change the naming conventions!
     * 
     * @param submitButtonId - the id of the html element that actually is submitted
     * @param release - true if you want the "release" dialog; false for the "retract" dialog
     */
    asnn2.releaseFeedbackDialog = function(submitButtonId, release) {
        var releaseText;
        if (release) {
            releaseText = 'release';
        } else {
            releaseText = 'retract';
        }
        
        var confirmDialog = jQuery('#' + releaseText + '-feedback-dialog');
        var submitButton = jQuery('input[id=\'' + submitButtonId + '\']');
        var confirmButton = jQuery('#page-replace\\:\\:' + releaseText + '-feedback-confirm');
        confirmButton.click( function (event) {
            asnn2util.closeDialog(confirmDialog);
            submitButton.onclick = function (event) { return true };
            submitButton.click();
        });

        var cancelButton = jQuery('#page-replace\\:\\:' + releaseText + '-feedback-cancel').click( function (event) {
            asnn2util.closeDialog(confirmDialog);
        });

        asnn2util.openDialog(confirmDialog);
        return false;
    };  
    
    /**
     * Release/Retract all grades 
     * 
     * This function uses the asnn2 dialog utility.  The same function is used for
     * both releasing and retracting grades. This is because the two cases
     * are handled by different translations being used in the java code. The
     * two cases use the same submit button for the form.
     * 
     * @param submitButtonId the id of the html element that actually is submitted
     * @param contextId the contextId for the submission
     * @param gradebookItemId the gradebookItemId associated w/ the assignment to release grades
     * @param release true if you want to release, false if you want to retract grades
     */
    asnn2.releaseGradesDialog = function(submitButtonId, contextId, gradebookItemId, release) {
        var confirmDialog = jQuery('#release-grades-dialog');
        var submitButton = jQuery('input[id=\'' + submitButtonId + '\']');
        var confirmButton = jQuery('#page-replace\\:\\:release-grades-confirm');
        confirmButton.click( function (event) {
            var confirmCheckbox = jQuery("#confirm-checkbox").get(0);
            if (confirmCheckbox && !confirmCheckbox.checked) {
                jQuery("#page-replace\\:\\:checkbox-error").show();
            } else {
                // include the value of the "include in course grade" option 
                var includeInCourseGrade = false;
                var includeInGradeEl = jQuery("#release-and-count").get(0);
                if (includeInGradeEl) {
                    includeInCourseGrade = includeInGradeEl.checked;
                }
                var queries = new Array();
                queries.push(RSF.renderBinding("ReleaseGradesAction.gradebookItemId", gradebookItemId));
                queries.push(RSF.renderBinding("ReleaseGradesAction.curContext", contextId));
                queries.push(RSF.renderBinding("ReleaseGradesAction.releaseGrades",release));
                queries.push(RSF.renderBinding("ReleaseGradesAction.includeInCourseGrade",includeInCourseGrade));
                
                queries.push(RSF.renderActionBinding("ReleaseGradesAction.execute"));
                var body = queries.join("&");
                jQuery.post(document.URL, body);
                
                asnn2util.closeDialog(confirmDialog);
                submitButton.onclick = function (event) { return true };
                submitButton.click();
            }
        });

        var cancelButton = jQuery('#page-replace\\:\\:release-grades-cancel').click( function (event) {
            asnn2util.closeDialog(confirmDialog);
        });

        asnn2util.openDialog(confirmDialog);
        return false;
    };
    
})(jQuery, asnn2);

// This namespace is for the Assignment Authoring and Editing Screen
var asnn2editpage = asnn2editpage || {};

(function (jQuery, asnn2editpage) {
    asnn2editpage.validate = function () {
    	titleMsg = jQuery("#page-replace\\:\\:assignment_title_empty");
    	nogbMsg = jQuery("#page-replace\\:\\:assignment_graded_no_gb_item");
    	dueBeforeOpenMsg = jQuery("#page-replace\\:\\:assignment_due_before_open");
    	acceptBeforeOpenMsg = jQuery("#page-replace\\:\\:assignment_accept_before_open");
    	acceptBeforeDueMsg = jQuery("#page-replace\\:\\:assignment_accept_before_due");
    	
    	/*titleMsg.hide();
    	nogbMsg.hide();
    	dueBeforeOpenMsg.hide();
    	acceptBeforeOpenMsg.hide();
    	acceptBeforeDueMsg.hide();*/
    	
    	// hide all error messages. some may come from the date widget
    	// built-in validator
    	jQuery("li.alertMessageInline").hide();
	
    	var valid = true;
    	// Reference: You can see these in  Assignment2Validator.java
    	
    	// check for empty title
    	var title = jQuery("input[name='page-replace\:\:title']").get(0)
    	if (title.value == '') {
    	    titleMsg.show();
    	    valid = false;
    	}
    	
    	//check for graded but no gradebookItemId
    	var usegb = jQuery("input[id='page-replace\:\:select_graded']").get(0);
    	var gbitem = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
    	if (usegb.checked && gbitem.value == '0') {
    	    nogbMsg.show();
    	    valid = false;
    	}
    	
    	var openDateStr = jQuery("#page-replace\\:\\:open_date\\:1\\:true-date").get(0).value;
    	var acceptDateStr = jQuery("#page-replace\\:\\:accept_until\\:1\\:true-date").get(0).value;
    	var dueDateStr = jQuery("#page-replace\\:\\:due_date\\:1\\:true-date").get(0).value;
    	
    	// if the user requires a due date, we need to validate it against the
    	// open date
    	var require_due_date = jQuery("input[name='page-replace\:\:require_due_date']").get(0).checked;
    	if (require_due_date) {
    	    // check for due date after open date
    	    // if the due date is null, we'll let the date widget take care of
    	    // that validation
    	    if (dueDateStr != '' && dueDateStr <= openDateStr) {
    	        dueBeforeOpenMsg.show();
    	        valid = false;
    	    }
    	}
        
        // if the user requires an accept until date, we need to validate it
        // against the open and due dates
        var require_accept_until = jQuery("input[name='page-replace\:\:require_accept_until']").get(0).checked;
        if (require_accept_until) {
            // we'll let the date widget take care of the null and formatting checks           
            if (require_due_date) {
                // check for accept until before due date
                if (acceptDateStr != '' && acceptDateStr < dueDateStr) {
                    acceptBeforeDueMsg.show();
                    valid = false;
                }
            } else {
                // check for accept until date before open date
                if (acceptDateStr != '' && acceptDateStr <= openDateStr) {
                    acceptBeforeOpenMsg.show();
                    valid = false;
                }
            }
        }
    	
    	if (!valid) {
    	    window.parent.scrollTo(0,0);
    	    return false;
    	}
    	
        return true;
    };
})(jQuery, asnn2editpage);

var asnn2listpage = asnn2listpage || {};

(function (jQuery, asnn2listpage) {
	// Variable to track the current thing being removed so we can fade it out.
	var currRemoveAsnnFadeoutElement;
	
	function tableVersionSetup() {
		jQuery(".assignmentEdits").hide();
		jQuery("tr.movable").hover(
			function() {
				jQuery(".assignmentEdits", this).show();
			},
			function() {
				jQuery(".assignmentEdits", this).hide();
			}
		);	
	}
	
	function listVersionSetup() {
		jQuery("ul#assignmentList li.row").css('cursor', 'move');
		jQuery("tr.movable").css('cursor', 'move');
	}

    /**
     *  This is the Fluid event that will be called whenever an Asnn row is
     *  dropped to a new location.
     */
    function onFluidReorder(item, requestedPosition, movables) {
        alert("I've been refactored");
    }

    /**
     *  This is the initialization code for setting up the Fluid Reorderer on
     *  the Asnns List Instructor Landing page.
     */
    function setupFluidReorderer() {
        var opts = {
            listeners: {
                afterMove: onFluidReorder
            },
            selectors: {
                movables: ".movable"
            }
        }
        //fluid.reorderList("#asnn-table-body", opts);
        fluid.reorderList("#assignmentList", opts)
    }
    
    function setupJQuerySortable() {
    	jQuery("#assignmentList").sortable({items: ">li.row", axis: "y", containment: "parent", update: saveSortables});
    }
    
    function setupRemoveDialog() {
    	var removeDialog = jQuery('#remove-asnn-dialog');
    	
    	jQuery('#page-replace\\:\\:remove-asnn-button').click( function (event)  {
    		var queries = new Array();
    		queries.push(RSF.renderBinding("RemoveAssignmentCommand.assignmentId",jQuery("#asnn-to-delete-id").html()));
    		queries.push(RSF.renderActionBinding("RemoveAssignmentCommand.execute"));
    		var body = queries.join("&");
    		jQuery.post(document.URL, body);
    		
    		// Close the dialog
    		asnn2util.closeDialog(removeDialog);
    		
    		jQuery("#removed-asnn-msg").clone().show().appendTo(currRemoveAsnnFadeoutElement);
    		setTimeout( function() {
    		    jQuery(currRemoveAsnnFadeoutElement).fadeOut();},
    		    500);
    		    
    	});
    	
    	jQuery('#page-replace\\:\\:cancel-remove-asnn-button').click( function (event) {
    		asnn2util.closeDialog(removeDialog);
    		// Setting this to empty to be sure the value doesn't get cached and
    		// accidentally used for deleting in the future. ASNN-427
    		jQuery("#asnn-to-delete-id").html('');
    		jQuery("#asnn-to-delete-title").html('');
			jQuery("#asnn-to-delete-due").html('');
			jQuery("#asnn-to-delete-numsubmissions").html('');
    	});
    }
    
    /**
     * This is used from the Instructor Landing page list.html to put up a
     * prompt dialog when the assignment delete link (trashcan) is clicked.
     */
    asnn2listpage.removeAsnnDialog = function(asnnId, fadeOutElement) {
    	currRemoveAsnnFadeoutElement = fadeOutElement;
    	
    	var removeDialog = jQuery('#remove-asnn-dialog');
    	
    	// This Regexp will handle the following cases:
    	// http://149.166.143.211:10080/portal/tool/a5a78a8d-9098-4f01-a634-dc93c791a04e/list
    	// http://149.166.143.211:10080/portal/tool/a5a78a8d-9098-4f01-a634-dc93c791a04e?panel=Main
    	var toolurlPat = /\/portal\/tool\/[^?/]*/
    	
    	var urlprefix = document.location.toString().match(toolurlPat);
    	
    	// TODO FIXME This URL is not guaranteed to have the same prefix. Route
    	// this through the entity broker
    	jQuery.getJSON(urlprefix + '/assignmentinfo/' + asnnId, 
    		function(data) {
    			jQuery("#asnn-to-delete-id").html(asnnId);	
    			jQuery("#asnn-to-delete-title").html(data['title']);
    			jQuery("#asnn-to-delete-due").html(data['due']);
    			jQuery("#asnn-to-delete-numsubmissions").html(data['numsubmissions'].toString());
    			asnn2util.openDialog(removeDialog);
    	});
    	
    	return false;
    };

    asnn2listpage.setupAsnnList = function() {
    	listVersionSetup();
    	setupRemoveDialog();
        //setupFluidReorderer();
        //setupJQuerySortable();
    };
})(jQuery, asnn2listpage);
