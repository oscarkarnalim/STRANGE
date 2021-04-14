# STRANGE

**STRANGE** \(Similarity TRacker in Academia with Natural lanGuage Explanation\) is a tool for observing similarities and the surface differences among Java/Python source code files. For Python,  the compiler needs to be installed and the absolute path should be set in 'pythoncompilerpath.txt'. Further details can be seen in [the corresponding paper](https://doi.org/10.1109/ACCESS.2021.3073703) published in IEEE Access. You can see this guideline in Indonesian below.

## STRANGE Modes 
## 1. Pair observation
This mode captures the similarities shared between two given source code files and stores them as an interactive HTML page. This mode can be embedded when developing other similarity detection software. 

*Quick execution command*:
```
pair <codepath1> <codepath2> <programming_language> <target_html_filepath>
```
*Complete execution command*: 
```
pair <codepath1> <codepath2> <programming_language> <target_html_filepath> <projectname1> <projectname2> <home_filepath> <syntax_minimum_match_length> <additional_keywords_path>
```
## 2. Assignment comparison
This mode pairwise compares student code files for an assignment and shows the similarities in interactive HTML pages. It accepts an assignment directory with each sub-directory represents a student submission containing one code file. The results are stored as a directory with 'index.html' as the entry page. Please preprocess the assignment directory with mode 4 if the assignment is broken down to sub-assignments, or with mode 5 if the assignment requires more than one code file per submission. 

*Quick execution command*: 
```
comp <assignment_root_dirpath> <programming_language> <target_html_dirpath>
```
*Complete execution command*: 
```
comp <assignment_root_dirpath> <programming_language> <target_html_dirpath> <sim_threshold> <syntax_minimum_match_length> <additional_keywords_path>
```
## 3. JPlag result update
This mode upgrades JPlag-style HTML pages for similarity observation with the ones from STRANGE. It accepts a JPlag-generated directory and an assignment directory with each sub-directory represents a student submission containing one file. Each of the HTML pages will be replaced with the STRANGE-style ones. 
*Quick execution command*: 
```
jplag <jplag_root_dirpath> <assignment_root_dirpath> <programming_language>
```
*Complete execution command*: 
```
jplag <jplag_root_dirpath> <assignment_root_dirpath> <programming_language> <syntax_minimum_match_length> <additional_keywords_path>
```
## 4. Assignment code grouping
This mode groups student submissions based on their sub-assignments' filename patterns. It accepts an assignment directory with each sub-directory represents a student submission containing several code files, and then group the code files based on the sub-assignments' filename patterns. The results will be stored in a directory named '\[grouped\]' + assignment directory name. Each sub-assignment will be represented as one sub-directory in which the name is based on the pattern but with only alphanumerics. Those that do not share one of the given file name pattern will be grouped as 'uncategorised'. 

*Execution command*: 
```
group <assignment_root_dirpath> <programming_language> <name_pattern_1> <name_pattern_2> ... <name_pattern_N> 
```
## 5. Assignment code file merging
This mode merges all code files in each student submission. It accepts an assignment directory containing students projects as the sub-directories, and then merge each project's code files as one large code file. The results will be stored in a directory named '\[merged\]' + assignment directory name, where each sub-directory represents one student project.

*Execution command*: 
```
merge <assignment_root_dirpath> <programming_language>
```
## 6. Template code removal
This mode removes template code from all code files in an assignment directory. It accepts an assignment directory and a template code file. The results will be stored in a directory named '\[template removed\]' + assignment directory name, where the directory structure is similar to the given assignment directory, except that only processed    code files are copied. The removed template code will be informed per code file as comments. Template code is detected with a greedy algorithm and it is not guarantee to be completely accurate (despite it is still acceptable).

*Execution command*: 
```
template <assignment_root_dirpath> <programming_language> <template_path>
```
## Parameters description
### Mode-1 \('pair'\) exclusive parameters description \(sorted alphabetically\):
  *\<codepath1\>*: a string representing the file path for the first code. Please use quotes if the path contains spaces. 
  *\<codepath2\>*: a string representing the file path for the second code. Please use quotes if the path contains spaces. 
  *\<home_filepath\>*: a string representing the file path for home html page \(i.e., the HTML page displayed before accessing the resulted page\). Enter null if no such a page exists. Please use quotes if the path contains spaces. This parameter is null by default. 
  *\<projectname1\>*: a string representing the project name for the first code. This can be used to distinguish given code files if they share the same name. This is assigned to *\<codepath1\>* by default. 
  *\<projectname2\>*: a string representing the project name for the second code. This can be used to distinguish given code files if they share the same name. This is assigned to *\<codepath2\>* by default. 
  *\<target_html_filepath\>*: a string representing the file path for the generated HTML. Please use quotes if the path contains spaces. 

### Mode-2 \('comp'\) exclusive parameters description \(sorted alphabetically\):
  *\<sim_threshold\>*: a floating number representing the threshold of suspicion. Only source code pairs with similarity degree greater than or equal to the threshold are displayed in the results. This is assigned with 0 by default. Value: a floating number between 0 to 1 \(inclusive\). 
  *\<target_html_dirpath\>*: a string representing the directory path for the generated HTML pages. Please use quotes if the path contains spaces. 

### Mode-3 \('jplag'\) exclusive parameters description:
  *\<jplag_root_dirpath\>*: a string representing the directory path of JPlag result. Please use quotes if the path contains spaces.

### Mode-4 \('group'\) exclusive parameters description:
  *\<name_pattern\>*: a string representing the name pattern of a sub-assignment, written as Java regular expression. Please use quotes if the path contains spaces.

### Mode-6 \('template'\) exclusive parameters description:
  *\<template_path\>*: a string representing a file containing template code that will be removed. The template code should be written in compliance to the programming language's syntax.

### Shared parameters description \(sorted alphabetically\):
  *\<additional_keywords_path\>*: a string representing a file containing additional keywords with newline as the delimiter. Keywords with more than one token should be written by embedding spaces between the tokens. For example, 'System.out.print' should be written as 'System . out . print'. Set this parameter to null if not needed. 
  *\<assignment_root_dirpath\>*: a string representing the assignment root directory. That directory should contain directories in which each of them refers to one student program and has one Java or Python code file. Please use quotes if the path contains spaces. 
  *\<programming_language\>*: a constant depicting the programming language used on given source code files. Value: 'java' \(for Java\) or 'py' \(for Python\). 
  *\<syntax_minimum_match_length\>*: a number depicting the lowest length of captured similar syntax code fragments. This is assigned 2 by default. Value: a positive integer. 


## Acknowledgments
This tool uses [ANTLR](https://www.antlr.org/) to tokenise given programs, [Apache Lucene](https://lucene.apache.org/) to identify stop words, [Google Prettify](https://github.com/google/code-prettify) to display source code, [google-java-format](https://github.com/google/google-java-format) to reformat Java program, [YAPF](https://github.com/google/yapf) to reformat Python program, and [JSoup](https://jsoup.org/) to parse JPlag's index page.

# Indonesian guideline for STRANGE
In progress
