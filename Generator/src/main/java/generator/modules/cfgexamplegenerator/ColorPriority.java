/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator;

import generator.common.tools.CriteriaConstraintsChecker;
import generator.common.tools.SpinnersSupport;
import java.awt.Color;
import java.awt.SystemColor;

/**
 * @author drasto
 */
public enum ColorPriority
{

	DISABLED(1, SystemColor.control),
	NOT_USED(2, SpinnersSupport.DISABLE_CRITERIA_COLOR),
	ERROR(3, CriteriaConstraintsChecker.ERROR_COLOR),
	WARNING(5, CriteriaConstraintsChecker.WARNING_COLOR),
	HARD_TO_FULLFILL(4, Color.WHITE),
	DEFAULT(6, Color.WHITE);

	private int priority;
	private Color defColor;

	private ColorPriority(int priority, Color defCol)
	{
		this.priority = priority;
		this.defColor = defCol;
	}

	public int getPriority()
	{
		return priority;
	}

	public Color getDefaultColor()
	{
		return defColor;
	}
}
