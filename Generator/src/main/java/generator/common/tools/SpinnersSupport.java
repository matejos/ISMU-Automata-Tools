/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.common.tools;

import generator.modules.CFGExampleGeneratorModule;
import generator.modules.cfgexamplegenerator.ColorController;
import generator.modules.cfgexamplegenerator.ColorPriority;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author drasto
 */
public final class SpinnersSupport
{

	public static final Color DISABLE_CRITERIA_COLOR = new Color(245, 245, 245);
	public static final Color ENABLE_COLOR = Color.WHITE;
	private static final String DISABLE_CRITERIA_VALUE = "-";
	public static final String ENABLE_TEXT = "Enable";
	public static final String DISABLE_TEXT = "Disable";
	public static final AbstractFormatterFactory NOT_USED_FACTORY = new JFormattedTextField.AbstractFormatterFactory()
	{

		@Override
		public String toString()
		{
			return "me";
		}

		@Override
		public AbstractFormatter getFormatter(JFormattedTextField tf)
		{
			return NOT_USED_FORMATTER;
		}
	};
	public static final AbstractFormatter NOT_USED_FORMATTER = new AbstractFormatter()
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object stringToValue(String text) throws ParseException
		{
			return DISABLE_CRITERIA_VALUE;
		}

		@Override
		public String valueToString(Object value) throws ParseException
		{
			return DISABLE_CRITERIA_VALUE;
		}
	};
	// public final PropertyChangeListener defaultSpinnerListener = new ChangeListener();

	private Map<JSpinner, JFormattedTextField.AbstractFormatterFactory> oldFactories = new HashMap<JSpinner, AbstractFormatterFactory>();
	private Set<JSpinner> registered = new HashSet<JSpinner>();
	private JPopupMenu disMenu;
	private JMenuItem disMenuItem;
	private ColorController colorCont;
	private Map<JSpinner, Integer> lastKnownPositiveValue = new HashMap<JSpinner, Integer>();
	private Set<JSpinner> disabled = new HashSet<JSpinner>();
	private Map<JSpinner, PropertyChangeSupport> supports = new HashMap<JSpinner, PropertyChangeSupport>();
	private Set<JSpinner> valueRequired = new HashSet<JSpinner>();

	public SpinnersSupport(JPopupMenu menu, JMenuItem item, ColorController colorCont)
	{

		disMenu = menu;
		disMenuItem = item;
		this.colorCont = colorCont;
		disMenuItem.setAction(new AbstractAction()
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				JMenuItem item = (JMenuItem) e.getSource();
				JPopupMenu menu = (JPopupMenu) item.getParent();
				JSpinner spin;
				if (menu.getInvoker() instanceof JFormattedTextField)
				{
					JFormattedTextField field = (JFormattedTextField) menu.getInvoker();
					spin = (JSpinner) field.getParent().getParent();
				}
				else
				{
					spin = (JSpinner) menu.getInvoker();
				}

				if (disabled.contains(spin))
				{
					enable(spin);
				}
				else
				{
					disable(spin);
				}
			}

		});

		disMenu.addPopupMenuListener(new PopupMenuListener()
		{

			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				JPopupMenu menu = (JPopupMenu) e.getSource();
				JSpinner spin;
				if (menu.getInvoker() instanceof JFormattedTextField)
				{
					JFormattedTextField field = (JFormattedTextField) menu.getInvoker();
					spin = (JSpinner) field.getParent().getParent();
				}
				else
				{
					spin = (JSpinner) menu.getInvoker();
				}

				if (spin.isEnabled())
				{
					disMenuItem.setEnabled(true);
				}
				else
				{
					disMenuItem.setEnabled(false);
				}

				if (disabled.contains(spin))
				{
					disMenuItem.setText(ENABLE_TEXT);
				}
				else
				{
					disMenuItem.setText(DISABLE_TEXT);
				}

			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
			}

			public void popupMenuCanceled(PopupMenuEvent e)
			{
			}
		});
	}

	public void disable(JSpinner spin)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}

		if (!disabled.contains(spin))
		{
			disabled.add(spin);
			// if ((Integer) spin.getValue() >= 0)
			// {
			// lastKnownPositiveValue.put(spin, (Integer) spin.getValue());
			// }
			JSpinner.DefaultEditor edit = (DefaultEditor) spin.getEditor();
			oldFactories.put(spin, edit.getTextField().getFormatterFactory());
			edit.getTextField().setFormatterFactory(NOT_USED_FACTORY);
			spin.setValue(-5);
			// colorCont.addNewSpinnerProp(spin, ColorPriority.NOT_USED);
			disabled.add(spin);
			supports.get(spin).firePropertyChange("used", true, false);
		}
	}

	public void enable(JSpinner spin)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}

		if (spin.isEnabled())
		{
			disabled.remove(spin);
			JSpinner.DefaultEditor edit = (DefaultEditor) spin.getEditor();

			edit.getTextField().setFormatterFactory(oldFactories.get(spin));
			// edit.getTextField().setValue(lastKnownPositiveValue.get(spin));
			spin.setValue(lastKnownPositiveValue.get(spin));
			enableNoChange(spin);
		}
	}

	private void enableNoChange(JSpinner spin)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}

		if (spin.isEnabled())
		{
			colorCont.removeSpinnerProp(spin, ColorPriority.NOT_USED);
			disabled.remove(spin);
			supports.get(spin).firePropertyChange("used", false, true);
		}
	}

	public void registerSpinner(final JSpinner newSpinner)
	{
		if (newSpinner == null)
		{
			throw new NullPointerException("newSpinner");
		}

		JSpinner.DefaultEditor edit = (DefaultEditor) newSpinner.getEditor();
		registered.add(newSpinner);
		lastKnownPositiveValue.put(newSpinner, Math.max(0, (Integer) newSpinner.getValue()));

		newSpinner.addChangeListener(new ChangeListener()
		{

			public void stateChanged(ChangeEvent e)
			{
				evaluateState((JSpinner) e.getSource());
			}

		});

		edit.getTextField().addFocusListener(new FocusListener()
		{

			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				JFormattedTextField field = (JFormattedTextField) e.getSource();
				evaluateState((JSpinner) field.getParent().getParent());
			}
		});

		edit.getTextField().addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				JFormattedTextField source = (JFormattedTextField) e.getSource();
				JSpinner spin = (JSpinner) source.getParent().getParent();
				if (!isEnabled(spin) && spin.isEnabled())
				{
					enable(spin);
				}
			}

		});

		edit.getTextField().addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!disMenu.isShowing() && e.isPopupTrigger())
				{
					disMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());
					disMenu.show(newSpinner, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!disMenu.isShowing() && e.isPopupTrigger())
				{
					disMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());
					disMenu.show(newSpinner, e.getX(), e.getY());
				}
			}

		});

		for (Component c : newSpinner.getComponents())
		{
			if (c instanceof Button || c instanceof JButton)
			{
				c.addMouseListener(new MouseAdapter()
				{

					private boolean enabledWhenPressed;

					@Override
					public void mouseClicked(MouseEvent e)
					{
						Component comp = (JComponent) e.getSource();
						JSpinner spin = (JSpinner) comp.getParent();
						// JSpinner.DefaultEditor edit = (DefaultEditor) spin.getEditor();
						if (!isEnabled(spin) && !enabledWhenPressed && spin.isEnabled())
						{
							spin.setValue(Math.max(lastKnownPositiveValue.get(spin), 0));
						}
					}

					@Override
					public void mousePressed(MouseEvent e)
					{
						Component comp = (JComponent) e.getSource();
						JSpinner spin = (JSpinner) comp.getParent();
						enabledWhenPressed = isEnabled(spin);
					}

				});
			}
		}

		newSpinner.addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
			{
				JSpinner source = (JSpinner) evt.getSource();

				if (evt.getPropertyName().equals("enabled"))
				{
					if (evt.getNewValue().equals(true))
					{
						colorCont.removeSpinnerProp(source, ColorPriority.DISABLED);
					}
					else
					{
						colorCont.addNewSpinnerProp(source, ColorPriority.DISABLED);
					}
				}
			}

		});
	}

	private void evaluateState(JSpinner source)
	{
		if ((Integer) source.getValue() >= 0)
		{
			lastKnownPositiveValue.put(source, (Integer) source.getValue());
			if (disabled.contains(source))
			{
				enable(source);
			}
		}
		if ((Integer) source.getValue() < 0)
		{
			disable(source);
		}
	}

	public void defaultDisabled(JSpinner spinner)
	{
		if (spinner == null)
		{
			throw new NullPointerException("spinner");
		}
		if (!registered.contains(spinner))
		{
			registerSpinner(spinner);
		}

		disable(spinner);
	}

	public boolean isEnabled(JSpinner spinner)
	{
		if (spinner == null)
		{
			throw new NullPointerException("spinner");
		}
		return !disabled.contains(spinner);
	}

	public boolean isCriteriaUsed(JSpinner spinner)
	{
		if (spinner == null)
		{
			throw new NullPointerException("spinner");
		}
		boolean vis = true;
		Component comp = spinner;
		while (comp != null)
		{
			comp = comp.getParent();
			if (comp instanceof JComponent)
			{
				JComponent panel = (JComponent) comp;
				if (panel.getClientProperty(CFGExampleGeneratorModule.PANE_USED_CLIENT_PROPERTY) != null)
				{
					vis = vis
						&& panel.getClientProperty(CFGExampleGeneratorModule.PANE_USED_CLIENT_PROPERTY).equals(true);
				}
			}
		}

		return isEnabled(spinner) && spinner.isEnabled() && vis;
	}

	public void addSpinnerNotUsedListener(JSpinner spin, PropertyChangeListener listener)
	{
		if (spin == null)
		{
			throw new NullPointerException("spin");
		}
		if (listener == null)
		{
			return;
		}

		if (!supports.containsKey(spin))
		{
			supports.put(spin, new PropertyChangeSupport(this));
		}

		supports.get(spin).addPropertyChangeListener(listener);
	}

	public void requireValue(JSpinner spin)
	{
		valueRequired.add(spin);
	}

	public boolean isValueRequired(JSpinner spin)
	{
		return valueRequired.contains(spin);
	}
}
