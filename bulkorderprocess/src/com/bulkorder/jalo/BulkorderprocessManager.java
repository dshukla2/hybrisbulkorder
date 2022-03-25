/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.bulkorder.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.bulkorder.constants.BulkorderprocessConstants;

public class BulkorderprocessManager extends GeneratedBulkorderprocessManager
{
	public static final BulkorderprocessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (BulkorderprocessManager) em.getExtension(BulkorderprocessConstants.EXTENSIONNAME);
	}
	
}
