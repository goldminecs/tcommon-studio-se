// ============================================================================
//
// Talend Community Edition
//
// // Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.commons.ui.swt.tableviewer.behavior;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableItem;
import org.talend.commons.ui.runtime.image.EImage;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorNotModifiable;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreatorColumnNotModifiable;
import org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener;
import org.talend.commons.ui.swt.tableviewer.sort.IColumnSortedListener;

/**
 * zhangyi class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class CheckColumnSelectionListener implements ITableColumnSelectionListener {

    private TableViewerCreatorColumnNotModifiable tableViewerCreatorColumn;

    private TableViewerCreatorNotModifiable tableViewerCreator;

    private boolean checked;

    /**
     * zhangyi CheckColumnSelectionListener constructor comment.
     */
    public CheckColumnSelectionListener(TableViewerCreatorColumnNotModifiable tableViewerCreatorColumn, TableViewerCreatorNotModifiable tableViewerCreator) {
        this.tableViewerCreatorColumn = tableViewerCreatorColumn;
        this.tableViewerCreator = tableViewerCreator;
        this.checked = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#addColumnSortedListener(org.talend
     * .commons.ui.swt.tableviewer.sort.IColumnSortedListener)
     */
    public void addColumnSortedListener(IColumnSortedListener columnSortedListener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#getTableViewerCreator()
     */
    public TableViewerCreatorNotModifiable getTableViewerCreator() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#getTableViewerCreatorColumn()
     */
    public TableViewerCreatorColumnNotModifiable getTableViewerCreatorColumn() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#removeColumnSortedListener(org.
     * talend.commons.ui.swt.tableviewer.sort.IColumnSortedListener)
     */
    public void removeColumnSortedListener(IColumnSortedListener columnSortedListener) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#setTableViewerCreator(org.talend
     * .commons.ui.swt.tableviewer.TableViewerCreator)
     */
    public void setTableViewerCreator(TableViewerCreatorNotModifiable tableViewerCreator) {
        this.tableViewerCreator = tableViewerCreator;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.commons.ui.swt.tableviewer.selection.ITableColumnSelectionListener#setTableViewerCreatorColumn(org
     * .talend.commons.ui.swt.tableviewer.TableViewerCreatorColumn)
     */
    public void setTableViewerCreatorColumn(TableViewerCreatorColumnNotModifiable tableViewerCreatorColumn) {
        this.tableViewerCreatorColumn = tableViewerCreatorColumn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e) {
        if (tableViewerCreator != null && tableViewerCreator.isReadOnly()) {
            return;
        }
        TableItem items[] = tableViewerCreator.getTable().getItems();
        String columnId = tableViewerCreatorColumn.getId();
        boolean modified = false;
        for (TableItem tableItem : items) {
            tableViewerCreator.getCellModifier().modify(tableItem, columnId, checked ? false : true);
            if (!modified) {
                modified = true;
            }
        }
        if (modified) {
            if (checked) {
                checked = false;
            } else {
                checked = true;
            }
            tableViewerCreatorColumn.getTableColumn().setImage(
                    checked ? ImageProvider.getImage(EImage.CHECKED_ICON) : ImageProvider.getImage(EImage.UNCHECKED_ICON));
        }
    }
}
