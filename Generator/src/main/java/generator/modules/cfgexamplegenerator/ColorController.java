/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;

/**
 * @author drasto
 */
public class ColorController
{

	private Map<JSpinner, Map<ColorPriority, Color>> spinCol = new HashMap<JSpinner, Map<ColorPriority, Color>>();
	private static Color defaultSpinnerBackground;

	public ColorController(JSpinner toGetDefaultBackgorund)
	{
		defaultSpinnerBackground = toGetDefaultBackgorund.getBackground();
	}

	public boolean addNewSpinnerProp(JSpinner spin, ColorPriority prior, Color color)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}
		if (prior == null)
		{
			throw new NullPointerException("prior");
		}
		if (color == null)
		{
			return addNewSpinnerProp(spin, prior);
		}

		if (!spinCol.containsKey(spin))
		{
			spinCol.put(spin, new HashMap<ColorPriority, Color>());
			spinCol.get(spin).put(ColorPriority.DEFAULT, ColorPriority.DEFAULT.getDefaultColor());
		}
		spinCol.get(spin).put(prior, color);

		JSpinner.DefaultEditor edit = (DefaultEditor) spin.getEditor();
		Color oldColor = edit.getTextField().getBackground();

		Color setCol = updateBackground(spin, edit);

		return !oldColor.equals(setCol);
	}

	public boolean removeSpinnerProp(JSpinner spin, ColorPriority prior)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}
		if (prior == null)
		{
			throw new NullPointerException("prior");
		}
		if (prior == ColorPriority.DEFAULT)
		{
			throw new IllegalArgumentException("Default spinners color cannot be removed");
		}

		if (spinCol.containsKey(spin) && spinCol.get(spin).containsKey(prior))
		{
			spinCol.get(spin).remove(prior);
		}
		else
		{
			return false;
		}

		JSpinner.DefaultEditor edit = (DefaultEditor) spin.getEditor();
		Color oldColor = edit.getTextField().getBackground();

		Color setCol = updateBackground(spin, edit);

		return !oldColor.equals(setCol);
	}

	public boolean addNewSpinnerProp(JSpinner spin, ColorPriority prior)
	{
		return addNewSpinnerProp(spin, prior, prior.getDefaultColor());
	}

	private Color updateBackground(JSpinner spin, DefaultEditor edit)
	{
		Color newCol = null;
		Color newSpinCol = null;

		for (ColorPriority p : ColorPriority.values())
		{
			if (spinCol.get(spin).containsKey(p))
			{
				newCol = spinCol.get(spin).get(p);

				if (p == ColorPriority.ERROR || p == ColorPriority.HARD_TO_FULLFILL || p == ColorPriority.WARNING)
				{
					newSpinCol = newCol;
				}
				if (p == ColorPriority.DEFAULT)
				{
					newSpinCol = defaultSpinnerBackground;
				}
				break;
			}
		}

		edit.getTextField().setBackground(newCol);
		if (newSpinCol != null)
		{
			spin.setBackground(newSpinCol);
		}

		return newCol;
	}
}
