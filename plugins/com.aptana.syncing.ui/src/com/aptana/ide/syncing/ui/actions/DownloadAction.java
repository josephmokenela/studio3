/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.syncing.core.model.ISyncItem.Operation;
import com.aptana.syncing.ui.internal.SyncUIManager;

/**
 * @author Michael Xia (mxia@aptana.com)
 * @author Max Stepanov
 */
public class DownloadAction extends BaseSyncAction {

	private IJobChangeListener jobListener = null;

	private static String MESSAGE_TITLE = StringUtil.ellipsify(Messages.DownloadAction_MessageTitle);

	protected void performAction(final IAdaptable[] files, ISiteConnection siteConnection) throws CoreException {
		IPath[] paths = new IPath[files.length];
		for (int i = 0; i < files.length; ++i) {
			paths[i] = EFSUtils.getRelativePath(siteConnection.getSource(), SyncUtils.getFileStore(files[i]));
		}
		Job job = SyncUIManager.getInstance().initiateOperation(siteConnection, paths, Operation.COPY_TO_LEFT);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				refreshWorkspaceResources(files);
			}
		});
		if (jobListener != null) {
			job.addJobChangeListener(jobListener);
		}		
	}

	public void addJobListener(IJobChangeListener listener) {
		jobListener = listener;
	}

	protected String getMessageTitle() {
		return MESSAGE_TITLE;
	}	
}
