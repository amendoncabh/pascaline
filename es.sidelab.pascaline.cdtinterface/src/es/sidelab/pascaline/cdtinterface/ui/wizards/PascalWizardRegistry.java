/*******************************************************************************
 * Copyright (c) 2004, 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *******************************************************************************/
package es.sidelab.pascaline.cdtinterface.ui.wizards;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;

import org.eclipse.cdt.ui.CUIPlugin;

import es.sidelab.pascaline.cdtinterface.CDTInterfacePlugin;

/**
 * Convenience class for drop-in C/C++ Wizard contributions.
 */
public class PascalWizardRegistry {

	private final static String TAG_WIZARD = "wizard"; //$NON-NLS-1$
	private final static String ATT_CATEGORY = "category";//$NON-NLS-1$
	private final static String ATT_PROJECT = "project";//$NON-NLS-1$
	private final static String TAG_PARAMETER = "parameter";//$NON-NLS-1$
	private final static String TAG_NAME = "name";//$NON-NLS-1$
	private final static String TAG_VALUE = "value";//$NON-NLS-1$
	private final static String ATT_CTYPE = "ctype";//$NON-NLS-1$
	private final static String ATT_CFILE = "cfile";//$NON-NLS-1$
	private final static String ATT_CFOLDER = "cfolder";//$NON-NLS-1$
	private final static String ATT_CPROJECT = "cproject";//$NON-NLS-1$
	private final static String ATT_CCPROJECT = "ccproject";//$NON-NLS-1$
    private final static String TAG_CLASS = "class"; //$NON-NLS-1$
    private final static String TAG_ID = "id"; //$NON-NLS-1$
    private final static String PL_NEW = "newWizards"; //$NON-NLS-1$

	
	
	
	/**
	 * Checks if wizard supports Pascal projects.
	 * 
	 * @param element the wizard element
	 * 
	 * @return <code>true</code> if the given wizard element applies to a C Project
	 */
	public static boolean isPascalProjectWizard(IConfigurationElement element) {
	    String category = element.getAttribute(ATT_CATEGORY);
	    return (category != null && category.equals(CDTInterfacePlugin.PASCAL_WIZARD_CATEGORY_ID));
	}
    
	/**
	 * Returns IDs of all Pascal project wizards contributed to the workbench.
	 * 
	 * @return an array of wizard ids
	 */
	public static String[] getProjectWizardIDs() {
		return getWizardIDs(getProjectWizardElements());
	}

	/**
	 * Returns extension data for all the C/C++ project wizards contributed to the workbench.
	 *     <wizard
	 *         name="My C Wizard"
	 *         icon="icons/cwiz.gif"
	 *         category="org.eclipse.cdt.ui.newCWizards"
	 *         id="xx.MyCWizard"
	 *         class="org.xx.MyCWizard"
	 *         project="true">
	 *         <description>
	 *             My C Wizard
	 *         </description>
	 *      </wizard>
	 *
	 * 
	 * @return an array of IConfigurationElement
	 */
	public static IConfigurationElement[] getProjectWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isProjectWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
    private static boolean isProjectWizard(IConfigurationElement element) {
	    String project = element.getAttribute(ATT_PROJECT);
	    if (project != null) {
	        return Boolean.valueOf(project).booleanValue();
	    }

	    IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && (name.equals(ATT_CPROJECT) || name.equals(ATT_CCPROJECT))) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
			return false;
		}
		// fall back, if no <class> element found then assume it's a project wizard
		return true;
    }
    
    public static IAction[] getProjectWizardActions() {
	    return createActions(getProjectWizardElements());
    }
    
	/**
	 * Returns IDs of all C/C++ type wizards contributed to the workbench.
	 * 
	 * @return an array of wizard ids
	 */
	public static String[] getTypeWizardIDs() {
		return getWizardIDs(getTypeWizardElements());
	}
    
	/**
	 * Returns extension data for all the C/C++ type wizards contributed to the workbench.
	 *     <wizard
	 *         name="My C Wizard"
	 *         icon="icons/cwiz.gif"
	 *         category="org.eclipse.cdt.ui.newCWizards"
	 *         id="xx.MyCWizard">
	 *         <class class="org.xx.MyCWizard">
	 *             <parameter name="ctype" value="true" />
	 *         </class> 
	 *         <description>
	 *             My C Wizard
	 *         </description>
	 *      </wizard>
	 * 
	 * @return an array of IConfigurationElement
	 */
	public static IConfigurationElement[] getTypeWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isTypeWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
    private static boolean isTypeWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_CTYPE)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }
	
    public static IAction[] getTypeWizardActions() {
	    return createActions(getTypeWizardElements());
    }
    
	/**
	 * Returns IDs of all C/C++ file wizards contributed to the workbench.
	 * 
	 * @return an array of wizard ids
	 */
	public static String[] getFileWizardIDs() {
		return getWizardIDs(getFileWizardElements());
	}

	/**
	 * Returns extension data for all the C/C++ file wizards contributed to the workbench.
	 *     <wizard
	 *         name="My C File Wizard"
	 *         icon="icons/cwiz.gif"
	 *         category="org.eclipse.cdt.ui.newCWizards"
	 *         id="xx.MyCWizard">
	 *         <class class="org.xx.MyCFileWizard">
	 *             <parameter name="cfile" value="true" />
	 *         </class> 
	 *         <description>
	 *             My C File Wizard
	 *         </description>
	 *      </wizard>
	 * 
	 * @return an array of IConfigurationElement
	 */
	public static IConfigurationElement[] getFileWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isFileWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
    private static boolean isFileWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_CFILE)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }
    
    public static IAction[] getFolderWizardActions() {
	    return createActions(getFolderWizardElements());
    }

	/**
	 * Returns IDs of all C/C++ folder wizards contributed to the workbench.
	 * 
	 * @return an array of wizard ids
	 */
	public static String[] getFolderWizardIDs() {
		return getWizardIDs(getFolderWizardElements());
	}

	/**
	 * Returns extension data for all the C/C++ folder wizards contributed to the workbench.
	 *     <wizard
	 *         name="My C Folder Wizard"
	 *         icon="icons/cwiz.gif"
	 *         category="org.eclipse.cdt.ui.newCWizards"
	 *         id="xx.MyCWizard">
	 *         <class class="org.xx.MyCFolderWizard">
	 *             <parameter name="cfolder" value="true" />
	 *         </class> 
	 *         <description>
	 *             My C Folder Wizard
	 *         </description>
	 *      </wizard>
	 * 
	 * @return an array of IConfigurationElement
	 */
	public static IConfigurationElement[] getFolderWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
	    IConfigurationElement[] elements = getAllWizardElements();
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isFolderWizard(element)) {
			    elemList.add(element);
            }
	    }
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
    private static boolean isFolderWizard(IConfigurationElement element) {
		IConfigurationElement[] classElements = element.getChildren(TAG_CLASS);
		if (classElements.length > 0) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(TAG_PARAMETER);
				for (IConfigurationElement curr : paramElements) {
					String name = curr.getAttribute(TAG_NAME);
					if (name != null && name.equals(ATT_CFOLDER)) {
					    String value = curr.getAttribute(TAG_VALUE);
					    if (value != null)
					        return Boolean.valueOf(value).booleanValue();
					}
				}
			}
		}
		return false;
    }
    
    public static IAction[] getFileWizardActions() {
	    return createActions(getFileWizardElements());
    }
    
	private static String[] getWizardIDs(IConfigurationElement[] elements) {
	    List<String> idList = new ArrayList<String>();

	    // add C wizards first
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element= elements[i];
			if (isPascalProjectWizard(element)) {
	            String id = element.getAttribute(TAG_ID);
	            if (id != null && !idList.contains(id)) {
	            	idList.add(id);
	            }
			}
	    }
	    
		return idList.toArray(new String[idList.size()]);
	}
    
    private static IAction[] createActions(IConfigurationElement[] elements) {
	    List<String> idList = new ArrayList<String>();
	    List<IAction> actionList = new ArrayList<IAction>();

	    // add C wizards first
	    for (int i = 0; i < elements.length; ++i) {
			IConfigurationElement element = elements[i];
			if (isPascalProjectWizard(element)) {
	            String id = element.getAttribute(TAG_ID);
	            if (id != null && !idList.contains(id)) {
	            	idList.add(id);
	            }
			}
	    }
	    
		return actionList.toArray(new IAction[actionList.size()]);
    }

    /**
     * This is a modification of the <code>CWizardRegistry.getAllWizardElements()</code> method returning only Pascal wizards.
     * 
	 * @return an array of IConfigurationElement
	 */
	public static IConfigurationElement[] getAllWizardElements() {
		List<IConfigurationElement> elemList = new ArrayList<IConfigurationElement>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PlatformUI.PLUGIN_ID, PL_NEW);
		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(TAG_WIZARD)) {
				    String category = element.getAttribute(ATT_CATEGORY);
				    if (category != null &&
				        category.equals(CDTInterfacePlugin.PASCAL_WIZARD_CATEGORY_ID)) {
			            elemList.add(element);
				    }
				}
			}
		}
		return elemList.toArray(new IConfigurationElement[elemList.size()]);
	}
	
}
