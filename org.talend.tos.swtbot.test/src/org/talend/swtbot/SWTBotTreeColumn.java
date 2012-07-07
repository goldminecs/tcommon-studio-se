// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.swtbot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.hamcrest.SelfDescribing;

/**
 * DOC vivian class global comment. Detailled comment
 */
public class SWTBotTreeColumn extends AbstractSWTBot<TreeColumn> {

    private final Tree parent;

    /**
     * Constructs a new instance of this object.
     * 
     * @param w the widget.
     * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
     * @since 2.0
     */
    public SWTBotTreeColumn(final TreeColumn w) throws WidgetNotFoundException {
        this(w, UIThreadRunnable.syncExec(new WidgetResult<Tree>() {

            public Tree run() {
                return w.getParent();
            }
        }));
    }

    /**
     * Constructs a new instance of this object.
     * 
     * @param w the widget.
     * @param parent the parent Tree.
     * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
     */
    public SWTBotTreeColumn(TreeColumn w, Tree parent) throws WidgetNotFoundException {
        this(w, parent, null);
    }

    /**
     * Constructs a new instance of this object.
     * 
     * @param w the widget.
     * @param parent the parent Tree.
     * @param description the description of the widget, this will be reported by {@link #toString()}
     * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
     */
    public SWTBotTreeColumn(TreeColumn w, Tree parent, SelfDescribing description) throws WidgetNotFoundException {
        super(w, description);
        this.parent = parent;
    }

    /**
     * Clicks the item.
     */
    public SWTBotTreeColumn click() {
        waitForEnabled();
        notify(SWT.Selection);
        notify(SWT.MouseUp, createMouseEvent(0, 0, 1, SWT.BUTTON1, 1), parent);
        return this;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
