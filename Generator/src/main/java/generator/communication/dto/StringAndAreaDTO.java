/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.communication.dto;

import javax.swing.JTextArea;

/**
 * @author drasto
 */
public class StringAndAreaDTO
{

	private String text;
	private JTextArea area;

	public StringAndAreaDTO(String text, JTextArea area)
	{
		if (text == null)
		{
			throw new NullPointerException("grammar");
		}
		if (area == null)
		{
			throw new NullPointerException("area");
		}
		this.text = text;
		this.area = area;
	}

	public JTextArea getArea()
	{
		return area;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public String toString()
	{
		return "StringAndArea [grammar=" + text + ", area=" + area + "]";
	}

}
