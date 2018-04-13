/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * @author drasto
 */
public class BooleanPropertyWithDefault
{

	private JCheckBox check;
	// private boolean enabledVal = true;
	private boolean defaultVal;
	private boolean validVal;
	private JComponent connectedComponent;
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private ActionListener checkListener = new ActionListener()
	{

		public void actionPerformed(ActionEvent e)
		{
			changeSupport.firePropertyChange("changed", !check.isSelected(), check.isSelected());
		}
	};

	protected BooleanPropertyWithDefault(boolean defaultVal, final JCheckBox check, JComponent conectedComp)
	{

		if (check == null)
		{
			throw new NullPointerException("check");
		}

		this.connectedComponent = conectedComp;

		this.check = check;
		this.check.setSelected(defaultVal);
		this.defaultVal = defaultVal;
		this.validVal = defaultVal;

		this.check.addActionListener(checkListener);
		if (conectedComp != null)
		{
			conectedComp.addComponentListener(new ComponentAdapter()
			{

				@Override
				public void componentShown(ComponentEvent e)
				{
					if (!check.isVisible())
					{
						check.setEnabled(true);
						check.setSelected(false);
						validVal = false;
					}
				}
			});

			addValidPropertyChangeListener(new PropertyChangeListener()
			{

				private int index;
				private String title;
				private JTabbedPane pane;

				public void propertyChange(PropertyChangeEvent evt)
				{
					if (validVal)
					{
						pane = (JTabbedPane) connectedComponent.getParent();
						index = pane.indexOfComponent(connectedComponent);
						title = pane.getTitleAt(index);
						connectedComponent.getParent().remove(connectedComponent);
						pane.validate();
						pane.repaint();
					}
					else
					{
						pane.add(connectedComponent, index);
						pane.setTitleAt(index, title);
						pane.validate();
						pane.repaint();
					}

				}
			});
		}
	}

	public void addValidPropertyChangeListener(PropertyChangeListener listener)
	{
		if (listener == null)
		{
			throw new NullPointerException("listener");
		}
		support.addPropertyChangeListener(listener);
	}

	public void addChangedPropertyListener(PropertyChangeListener listener)
	{
		if (listener == null)
		{
			throw new NullPointerException("listener");
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	public void apply()
	{
		if (check.isEnabled())
		{
			boolean oldValue = validVal;
			validVal = check.isSelected();
			if (validVal != oldValue)
			{
				support.firePropertyChange("", validVal, oldValue);
			}
		}
	}

	public void setDefault()
	{
		if (check.isEnabled())
		{
			check.setSelected(defaultVal);
		}
	}

	public void disable(boolean newValue)
	{
		if (check.isEnabled())
		{
			check.setSelected(newValue);
			check.setEnabled(false);
		}
	}

	public void disable()
	{
		disable(check.isSelected());
	}

	public void enable(boolean newValue)
	{
		if (!check.isEnabled())
		{
			check.setEnabled(true);
			check.setSelected(newValue);
		}
	}
	public void enable()
	{
		enable(defaultVal);
	}

	public boolean isChanged()
	{
		return check.isSelected() != validVal;
	}

	public void cancel()
	{
		check.setEnabled(check.isEnabled());
		check.setSelected(validVal);
	}

	public JCheckBox getComponet()
	{
		return check;
	}

	public boolean isDefaultVal()
	{
		return defaultVal;
	}

	public boolean isValidVal()
	{
		return validVal;
	}

}
