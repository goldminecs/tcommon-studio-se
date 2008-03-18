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
package org.talend.dq.sql.converters;

import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataquality.domain.sql.SqlRelationalOperator;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.expressions.ExpressionsFactory;

import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.resource.relational.Column;

import Zql.ZConstant;
import Zql.ZExp;
import Zql.ZExpression;

/**
 * @author scorreia
 * 
 * A class for building expressions.
 * @param <T> the type of value for instances.
 */
public class CwmZExpression<T> {

    private SqlRelationalOperator operator;

    private Column column1;

    private Column column2;

    private T instance;

    public CwmZExpression(SqlRelationalOperator op) {
        assert op != null : "null operator given";
        this.operator = op;
    }

    protected String getOperator() {
        return this.operator.getLiteral();
    }

    public void setOperands(Column column, T value) {
        this.column1 = column;
        this.instance = value;
    }

    public void setOperands(Column col1, Column col2) {
        this.column1 = col1;
        this.column2 = col2;
    }

    protected ZExp getColumn1() {
        if (column1 != null) {
            return new ZConstant(ColumnHelper.getFullName(column1), ZConstant.COLUMNNAME);
        }
        return null;
    }

    protected ZExp getColumn2() {
        if (column2 != null) {
            return new ZConstant(ColumnHelper.getFullName(column2), ZConstant.COLUMNNAME);
        }
        return null;
    }

    protected ZExp getInstance() {
        if (instance != null) {
            int type = ZConstant.UNKNOWN; // TODO scorreia set the correct type here?
            return new ZConstant(this.instance.toString(), type);
        }
        return null;
    }

    public ZExpression generateZExpression() {
        ZExpression expr = new ZExpression(this.getOperator());
        expr.addOperand(getColumn1());
        if (instance != null) {
            expr.addOperand(getInstance());
        } else {
            expr.addOperand(getColumn2());
        }
        return expr;
    }

    public BooleanExpressionNode generateExpressions() {
        BooleanExpressionNode expr = ExpressionsFactory.eINSTANCE.createBooleanExpressionNode();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(this.generateZExpression().toString());
        expression.setLanguage("SQL");
        expr.setExpression(expression);
        return expr;
    }
}
