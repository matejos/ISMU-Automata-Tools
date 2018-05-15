# IS MU Automata Tools
Tools for ROPOTs of automata courses at Faculty of Informatics, Masaryk University, Brno.

## Generator

Standalone java application.
Use for generating set of questions for various exercises of formal languages.
Output IS format into a .qdef file.

## Converter

Use this command line java application to import **Editor** to .qdef files. Now you can use them to make a ROPOT.
Takes paths to .qdef files as input parameters. Use argument *-?* for more help.

## Editor

Tool for more comfortable filling out of finite automata and C-Y-K algorithm ROPOTs.
Must be imported into them by **Convertor** beforehand.

## Resolver

Serves for resolving ROPOTs answers and giving feedback. Also front-end part available at http://arran.fi.muni.cz:8180/fjamp/

## ParsersGrammars

Source files of grammars used to generate parsers for syntax checking with program from https://is.muni.cz/auth/th/172451/fi_b.
