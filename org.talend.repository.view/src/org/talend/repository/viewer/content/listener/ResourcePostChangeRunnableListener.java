// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.viewer.content.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class ResourcePostChangeRunnableListener implements IResourceChangeListener {

    private final Viewer viewer;

    private List<IResourceDeltaVisitor> resourcevisitors = new ArrayList<IResourceDeltaVisitor>();

    public ResourcePostChangeRunnableListener(Viewer v) {
        super();
        this.viewer = v;
    }

    public boolean addVisitor(IResourceDeltaVisitor visitor) {
        if (visitor != null) {
            return resourcevisitors.add(visitor);
        }
        return false;
    }

    public boolean removeVisitor(IResourceDeltaVisitor visitor) {
        if (visitor != null) {
            return resourcevisitors.remove(visitor);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent
     * )
     */
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        if (viewer == null || resourcevisitors == null || resourcevisitors.isEmpty()) {
            return;
        }
        final Control ctrl = viewer.getControl();
        if (ctrl == null || ctrl.isDisposed()) {
            return;
        }

        // visit
        final Collection<Runnable> runnables = new HashSet<Runnable>();
        for (IResourceDeltaVisitor visitor : resourcevisitors) {
            if (visitor instanceof RunnableResourceVisitor) {
                ((RunnableResourceVisitor) visitor).setRunnables(runnables);
            }

            try {
                event.getDelta().accept(visitor);
            } catch (CoreException e) {
                //
            }
        }

        if (!runnables.isEmpty()) {
            // Are we in the UIThread? If so spin it until we are done
            if (ctrl.getDisplay().getThread() == Thread.currentThread()) {
                runUpdates(runnables);
            } else {
                ctrl.getDisplay().asyncExec(new Runnable() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see java.lang.Runnable#run()
                     */
                    @Override
                    public void run() {
                        // Abort if this happens after disposes
                        Control ctrl2 = viewer.getControl();
                        if (ctrl2 == null || ctrl2.isDisposed()) {
                            return;
                        }

                        runUpdates(runnables);

                    }
                });
            }
        }
    }

    /**
     * Run all of the runnables that are the widget updates
     * 
     * @param runnables
     */
    private void runUpdates(Collection<Runnable> runnables) {
        //
        final Collection<Runnable> orderedRunnables = order(runnables);

        for (Runnable runnable : orderedRunnables) {
            runnable.run();
        }

    }

    /**
     * Make sure the bin top level node should be the last one to do runnable task.
     * 
     * THis related to the ProjectRepositoryNode.addItemToRecycleBin, so must be after other nodes.
     * 
     * @param originalRunnables
     */
    private Collection<Runnable> order(final Collection<Runnable> originalRunnables) {
        // make all runables are only for one top level node.
        Collection<Runnable> newRunnables = new ArrayList<Runnable>(originalRunnables);

        // for bin
        List<Runnable> binNodeRunnables = new ArrayList<Runnable>();
        Runnable mainBinNodeRunnable = null;

        Iterator<Runnable> iterator = newRunnables.iterator();
        while (iterator.hasNext()) {
            Runnable runnable = iterator.next();
            if (runnable instanceof TopLevelNodeRunnable) {
                TopLevelNodeRunnable nodeRunnable = (TopLevelNodeRunnable) runnable;
                RepositoryNode topLevelNode = nodeRunnable.getTopLevelNode();
                if (topLevelNode != null && topLevelNode.isBin()) {
                    if (ProjectManager.getInstance().isInCurrentMainProject(topLevelNode)) {
                        mainBinNodeRunnable = runnable;
                    } else { // others for ref-project
                        binNodeRunnables.add(runnable);
                    }
                    iterator.remove(); // remove the bin runnables.
                }
            }
        }

        // add all binNodes
        newRunnables.addAll(binNodeRunnables);
        if (mainBinNodeRunnable != null) { // main bin is last.
            newRunnables.add(mainBinNodeRunnable);
        }
        return newRunnables;
    }

}
