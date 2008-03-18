// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.javasql.IndicatorEvaluator;
import org.talend.dq.sql.converters.CwmZQuery;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * Run analysis on columns.
 */
public class ColumnAnalysisExecutor extends AnalysisExecutor {

    private static Logger log = Logger.getLogger(ColumnAnalysisExecutor.class);

    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        // --- the catalog or schema
        Set<orgomg.cwm.objectmodel.core.Package> schemata = new HashSet<orgomg.cwm.objectmodel.core.Package>();

        IndicatorEvaluator eval = new IndicatorEvaluator();
        // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(indicator.getAnalyzedElement());
            if (tdColumn == null) {
                continue;
            }
            // --- get the schema owner
            if (!belongToSameSchemata(tdColumn, schemata)) {
                return false;
            }
            String columnName = ColumnHelper.getFullName(tdColumn);
            eval.storeIndicator(columnName, indicator);
        }

        // open a connection
        TypedReturnCode<Connection> connection = getConnection(analysis, schemata.iterator().next());
        if (!connection.isOk()) {
            log.error(connection.getMessage());
            this.errorMessage = connection.getMessage();
            return false;
        }

        // set it into the evaluator
        eval.setConnection(connection.getObject());
        // when to close connection
        boolean closeAtTheEnd = true;
        ReturnCode rc = eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
        if (!rc.isOk()) {
            log.warn(rc.getMessage());
            this.errorMessage = rc.getMessage();
        }
        return rc.isOk();
    }

    /**
     * DOC scorreia Comment method "belongToSameSchemata".
     * 
     * @param tdColumn
     * @param schemata
     * @return
     */
    private boolean belongToSameSchemata(final TdColumn tdColumn, final Set<Package> schemata) {
        assert tdColumn != null;
        ColumnSet owner = ColumnHelper.getColumnSetOwner(tdColumn);
        if (owner == null) {
            this.errorMessage = "No owner found for this column: " + tdColumn.getName();
            return false;
        }
        Package schema = ColumnSetHelper.getParentCatalogOrSchema(owner);
        if (schema == null) {
            this.errorMessage = "No schema or catalog found for this column: " + owner.getName() + "."
                    + tdColumn.getName();
            return false;
        }

        schemata.add(schema);
        if (schemata.size() != 1) {
            this.errorMessage = "given columns do not belong all to the same schema (or catalog). Cannot run this kind of analysis!";
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected String createSqlStatement(Analysis analysis) {
        CwmZQuery query = new CwmZQuery();
        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
        if (analysedElements.isEmpty()) {
            this.errorMessage = "Nothing to analyze for given analysis: " + analysis.getName()
                    + ". Cannot create the SQL statement!";
            return null;
        }
        Set<ColumnSet> fromPart = new HashSet<ColumnSet>();
        for (ModelElement modelElement : analysedElements) {
            // --- preconditions
            TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            if (col == null) {
                this.errorMessage = "Given element is not a column: " + modelElement;
                return null;
            }
            ColumnSet colSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(col.getOwner());
            if (colSet == null) {
                this.errorMessage = "No container found for given column: " + col.getName() + ". Container= " + colSet;
                return null;
            }
            // else add into select

            if (!query.addSelect(col)) {
                this.errorMessage = "Problem adding the SELECT part of the SQL statement.";
                return null;
            }
            // add from
            fromPart.add(colSet);

        }

        if (!query.addFrom(fromPart)) {
            this.errorMessage = "Problem adding the from part of the SQL statement.";
            return null;
        }
        return query.generateStatement();
    }

}
