===================================================
PROJECT:   Assignment 1
===================================================

AUTHOR:	   Matthew D Brown

TERM:	   Fall 2022

COURSE:	   CSC365-HY1

PROFESSOR: Doug Lea


===================================================
GOAL
===================================================

The goal of this project is to provide the user 
with an interface, via which they may enter a URL 
and be presented with a related URL.


===================================================
DESIGN
===================================================

The project structure follows a modified MVC design 
pattern. The UI is designed with Java Swing. The 
initial result delivered to the user is based on 
text scraped from 21 other URLs, which are pulled 
from a stored text file. The text scraping is 
performed with JSoup. The process of matching 
results is performed with the aid of a TF-IDF 
calculation algorithm.


===================================================
NOTABLE FEATURES
===================================================

- A customized list of 'ignored words' allows the 
  program to disregard common, inisgnificant word 
  types, including prepositions, conjunctions, 
  articles, and pronouns, which boosts the accuracy 
  of results.

- For as long as the program continues running, 
  each valid URL entered by the user is added to 
  the program's URL list and may turn up as a 
  matching result in subsequent searches.

- When searching for the best match for a URL, the 
  program ignores any identical URL, thereby 
  avoiding presenting the user with a URL identical 
  to the one they entered.

- The program validates user-entered URLs before 
  attempting to process match results, thereby 
  avoiding crashes due to user error.
