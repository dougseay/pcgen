/*
 * TableCellUtilities.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Aug 10, 2008, 3:37:34 PM
 */
package pcgen.gui2.util.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class TableCellUtilities
{

	private static final DefaultTableCellRenderer dummyRenderer = new DefaultTableCellRenderer();

	private TableCellUtilities()
	{
	}

	public static void setToRowBackground(Component c, JTable table, int row)
	{
		dummyRenderer.getTableCellRendererComponent(table, null, false, false, row, 0);
		Color bg = dummyRenderer.getBackground();
		// We have to create a new color object because Nimbus returns
		// a color of type DerivedColor, which behaves strange, not sure
		// why.
		c.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
	}

	public static class RadioButtonEditor extends AbstractCellEditor
			implements ActionListener,
			TableCellEditor
	{

		private JRadioButton button;

		public RadioButtonEditor()
		{
			this.button = new JRadioButton();
			button.setHorizontalAlignment(JRadioButton.CENTER);
			button.addActionListener(this);
		}

		public Object getCellEditorValue()
		{
			return Boolean.valueOf(button.isSelected());
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
													 boolean isSelected,
													 int row, int column)
		{
			boolean selected = false;
			if (value instanceof Boolean)
			{
				selected = ((Boolean) value).booleanValue();
			}
			else if (value instanceof String)
			{
				selected = value.equals("true");
			}
			button.setSelected(selected);
			return button;
		}

		public void actionPerformed(ActionEvent e)
		{
			stopCellEditing();
		}

	}

	public static class SpinnerEditor extends AbstractCellEditor
			implements TableCellEditor, ChangeListener
	{

		protected final JSpinner spinner;

		public SpinnerEditor()
		{
			this(new JSpinner());
		}

		public SpinnerEditor(SpinnerModel model)
		{
			this(new JSpinner(model));
		}

		public SpinnerEditor(JSpinner spinner)
		{
			this.spinner = spinner;
			spinner.addChangeListener(this);
		}

		public Object getCellEditorValue()
		{
			return spinner.getValue();
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
													 boolean isSelected,
													 int row,
													 int column)
		{
			spinner.setValue(value);
			return spinner;
		}

		public void stateChanged(ChangeEvent e)
		{
			stopCellEditing();
		}

		@Override
		public boolean stopCellEditing()
		{
			try
			{
				spinner.commitEdit();
			}
			catch (ParseException ex)
			{
				return false;
			}
			return super.stopCellEditing();
		}

	}

	public static class ToggleButtonRenderer extends DefaultTableCellRenderer
	{

		private JToggleButton button;

		public ToggleButtonRenderer(JToggleButton button)
		{
			this.button = button;
			button.setHorizontalAlignment(CENTER);
			button.setBorderPainted(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean isSelected,
													   boolean hasFocus,
													   int row,
													   int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
												hasFocus, row,
												column);
			if (value == null)
			{
				return this;
			}
			button.setForeground(getForeground());
			button.setBackground(getBackground());
			button.setBorder(getBorder());

			button.setSelected(((Boolean) value).booleanValue());
			return button;
		}

	}

	public static class SpinnerRenderer extends DefaultTableCellRenderer
	{

		private final JSpinner spinner;

		public SpinnerRenderer()
		{
			this(new JSpinner());
		}

		public SpinnerRenderer(JSpinner spinner)
		{
			this.spinner = spinner;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean isSelected,
													   boolean hasFocus,
													   int row,
													   int column)
		{
			if (value == null)
			{
				return super.getTableCellRendererComponent(table, value,
														   isSelected,
														   hasFocus, row, column);
			}
			spinner.setValue(value);
			spinner.setEnabled(table.isCellEditable(row, column));
			return spinner;
		}

	}

	/**
	 * Align the cell text in a column
	 */
	public static final class AlignRenderer extends DefaultTableCellRenderer
	{

		/**
		 * align is one of:
		 * SwingConstants.LEFT
		 * SwingConstants.CENTER
		 * SwingConstants.RIGHT
		 **/
		private int align = SwingConstants.LEFT;

		public AlignRenderer(int anInt)
		{
			super();
			align = anInt;
			setHorizontalAlignment(align);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setEnabled((table == null) || table.isEnabled());
			setHorizontalAlignment(align);
			return this;
		}

	}

}