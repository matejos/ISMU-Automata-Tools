// Skript.cpp : Defines the entry point for the console application.
//

#ifdef _WIN32
#include "stdafx.h"
#else
#include <stdio.h>
#endif
#include <iostream>
#include <fstream>
#include <algorithm>
#include <string>

using namespace std;

const string prefixContentFileName = "Skript_prefix.txt";
const string parsersLocationFileName = "Skript_parsers.txt";

string trim(string& str)
{
	size_t first = str.find_first_not_of(' ');
	size_t last = str.find_last_not_of(' ');
	if (first == string::npos)
		return str;
	return str.substr(first, (last - first + 1));
}

int main(int argc, char* argv[])
{
	if (argc < 2) 
	{
		cout << "ERROR: Use at least one file name as argument!" << endl;
		cout << "Program closed." << endl;
		return 0;
	}

	string s, s2, parsersLocation, prefixContent;

	ifstream parsersLocationInput(parsersLocationFileName);
	if (parsersLocationInput.is_open())
	{
		getline(parsersLocationInput, s);
		parsersLocation += s;
		parsersLocationInput.close();
	}
	else
	{
		cout << "ERROR: Could not read parsers location from file " << parsersLocationFileName << endl;
		cout << "Exporting aborted." << endl;
		return 0;
	}

	ifstream prefixContentInput(prefixContentFileName);
	if (prefixContentInput.is_open())
	{
		while (!prefixContentInput.eof())
		{
			getline(prefixContentInput, s);
			prefixContent += s + "\n";
		}
		prefixContentInput.close();
	}
	else
	{
		cout << "ERROR: Could not read prefix content from file " << prefixContentFileName << endl;
		cout << "Exporting aborted." << endl;
		return 0;
	}

	string suffix;
	cout << "Enter suffix of the export name: ";
	cin >> suffix;
	
	for (int i = 1; i < argc; i++)
	{
		string inputName = argv[i];
		string outputName = inputName;
		outputName.insert(outputName.find_last_of('.'), suffix);
		if (outputName == prefixContentFileName || outputName == parsersLocationFileName)
		{
			cout << "ERROR: Forbidden name! Cannot convert " << inputName << " to " << outputName << ". Skipping..." << endl;
			continue;
		}
		cout << "Converting " << inputName << " to " << outputName << endl;
		ifstream input(inputName);
		if (input.is_open())
		{
			int q = 0;
			ofstream output(outputName);
			output << "++" << endl << prefixContent
				<< "<script src=\"" << parsersLocation << "utilIS.js\" type=\"text/javascript\"></script>" << endl
				<< "<style type=\"text/css\">@import \"" << parsersLocation << "parser_style.css\";</style>" << endl << "--" << endl;
			while (!input.eof())
			{
				getline(input, s);
				
				if (s.find(":e") != string::npos)
				{
					getline(input, s2);
					string formtype = "";
					string type = s2.substr(s2.find_first_of('-') + 1, 3);
					if (type == "DFA" || type == "MIN" || type == "MIC" || type == "TOT" || type == "TOC" || type == "CAN")
					{
						formtype = "DFA";
					}
					else if (type == "REG" || type == "GRA" || type == "NFA" || type == "EFA")
					{
						formtype = type;
					}
					if (formtype != "")
					{
						q++;
						string formtypelower = formtype;
						std::transform(formtypelower.begin(), formtypelower.end(), formtypelower.begin(), ::tolower);
						if (formtype == "DFA" || formtype == "NFA" || formtype == "EFA")
						{
							output << "<input name=\"q" << q << "\" type=\"hidden\" value=\"\" />"
								 << "<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" << parsersLocation << formtypelower << "parserN.js\" type=\"text/javascript\"></script>"
								 << "<div id=\"q" << q << "-div\" class=\"parser_text_default\"> :e <span id=\"q" << q << "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" << q << "\", " << formtypelower << "Parser.parse)</script>" << endl;
							output << "<ul class=\"nav nav-tabs\"><li class=\"myli active\"><a data-toggle=\"tab\" data-target=\"#q" << q
								<< "a\">Graf</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" << q
								<< "b\">Tabulka</a></li><li class=\"myli\"><a data-toggle=\"tab\" data-target=\"#q" << q
								<< "c\">Text</a></li></ul></ul>" << endl;
							output << "<div id=\"q" << q << "\" class=\"tab-content\"><script>init(\"q" << q << "\", \"" << type << "\");</script></div>" << endl;
						}
						else
						{
							output << "<input name=\"q" << q << "\" type=\"hidden\" value=\"\" />" << endl;
							output << "<noscript>(Nemate zapnuty JavaScript, ale pro spravnou funkci otazky je JavaScript nutny. Jako prohlizec je doporuceny Firefox.) </noscript><script src=\"" << parsersLocation << formtypelower << "parserN.js\" type=\"text/javascript\"></script>" << endl;
							output << "<div id=\"q" << q << "-div\" class=\"parser_text_default\"> :e <br><span id=\"q" << q << "-error\" class=\"parser_error\"></span></div><script type=\"text/javascript\">register(\"q" << q << "\", " << formtypelower << "Parser.parse)</script>" << endl;
						}
					}
					output << s2 << endl;
				}
				else if (s.find("<input") == string::npos)
				{
					output << s << endl;
				}
			}
			cout << "-Successfully converted " << q << " questions." << endl;
			input.close();
			output.close();
		}
	}
	cout << "Finished. Press Enter to continue." << endl;
	getchar();
	getchar();
    return 0;
}

