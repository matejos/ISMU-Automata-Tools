/*
 * Class for converting arabic numbers to roman numbers and back
 */

package generator.modules.reglang.alphabet;

/**
 * converts arabian number to roman
 * 
 * @author Jana Kadlecova
 */
public class RomanNumbersConverter
{

	/**
	 * converts arabian number to roman
	 * 
	 * @param number
	 *            number to convart
	 * @return roman number as a string
	 */
	public static String arabicToRoman(int number)
	{
		if (number == 0)
		{
			return "0";
		}
		StringBuilder romanNumber = new StringBuilder();
		int thousands = number / 1000;
		for (int i = 0; i < thousands; i++)
		{ // converts thousands
			romanNumber.append("M");
		}
		// converts hundreds
		int hundreds = (number - thousands * 1000) / 100;
		if (hundreds < 4)
		{
			for (int i = 0; i < hundreds; i++)
			{
				romanNumber.append("C");
			}
		}
		if (hundreds == 4)
		{
			romanNumber.append("CD");
		}
		if (hundreds >= 5 && hundreds < 9)
		{
			romanNumber.append("D");
			for (int i = 0; i < hundreds - 5; i++)
			{
				romanNumber.append("C");
			}
		}
		if (hundreds == 9)
		{
			romanNumber.append("CM");
		}
		// convers tens
		int tens = ((number - thousands * 1000) - hundreds * 100) / 10;
		if (tens < 4)
		{
			for (int i = 0; i < tens; i++)
			{
				romanNumber.append("X");
			}
		}
		if (tens == 4)
		{
			romanNumber.append("XL");
		}
		if (tens >= 5 && tens < 9)
		{
			romanNumber.append("L");
			for (int i = 0; i < tens - 5; i++)
			{
				romanNumber.append("X");
			}
		}
		if (tens == 9)
		{
			romanNumber.append("XC");
		}
		// converts units
		int units = (((number - thousands * 1000) - hundreds * 100) - tens * 10);
		if (units < 4)
		{
			for (int i = 0; i < units; i++)
			{
				romanNumber.append("I");
			}
		}
		if (units == 4)
		{
			romanNumber.append("IV");
		}
		if (units >= 5 && units < 9)
		{
			romanNumber.append("V");
			for (int i = 0; i < units - 5; i++)
			{
				romanNumber.append("I");
			}
		}
		if (units == 9)
		{
			romanNumber.append("IX");
		}
		return romanNumber.toString();
	}

	/**
	 * converts given roman number to arabian number
	 * 
	 * @param romanNumber
	 * @return given roman number as an arabian number
	 */
	public static int romanToArabic(String romanNumber) throws NumberFormatException
	{
		if (romanNumber == null)
		{
			throw new NullPointerException("NumberToRomanConverter.romanToArabic()");
		}
		int number = 0;
		char[] roman = new char[romanNumber.length()];
		for (int i = 0; i < romanNumber.length(); i++)
		{
			roman[i] = Character.toUpperCase(romanNumber.charAt(i));
		}
		for (int i = 0; i < roman.length; i++)
		{
			char c = roman[i];
			char t = '0';
			if (i + 1 != roman.length)
			{
				t = roman[i + 1];
			}
			switch (c)
			{
				case 'M':
					number = number + 1000;
					break;
				case 'D':
					number = number + 500;
					break;
				case 'L':
					number = number + 50;
					break;
				case 'X':
					number = number + 10;
					break;
				case 'V':
					number = number + 5;
					break;
				case 'C':
					number = number + 100;
					break;
				case 'I':
					switch (t)
					{
						case '0':
							number++;
							break;
						case 'I':
							number++;
							break;
						case 'M':
							number = number + 999;
							i++;
							break;
						case 'D':
							number = number + 499;
							i++;
							break;
						case 'C':
							number = number + 99;
							i++;
							break;
						case 'L':
							number = number + 49;
							i++;
							break;
						case 'X':
							number = number + 9;
							i++;
							break;
						case 'V':
							number = number + 4;
							i++;
							break;
					}
					break;
				default:
					throw new NumberFormatException("NumberToRomanConverter.romanToArabic()" + romanNumber);
			}

		}
		return number;
	}

}
