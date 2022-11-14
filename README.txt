===================================================
PROJECT:   Assignment 2
===================================================

AUTHORS:   Matthew D Brown, Thomas Moskal

TERM:	   Fall 2022

COURSE:	   CSC365-HY1

PROFESSOR: Doug Lea


===================================================
GOAL
===================================================

The goal of this project is to provide the user 
with an interface, via which they may enter a URL 
and be presented with a best-matching URL, along
with a list of other related URLs. All associated
data is to be persistent.


===================================================
DESIGN
===================================================

The project structure follows a modified MVC design 
pattern. The UI is designed with Java Swing. The 
initial result delivered to the user is based on 
text scraped from 100+ other URLs, which are pulled
from a stored text file. The text scraping is 
performed with JSoup. The process of matching 
results is performed with the aid of a TF-IDF 
calculation algorithm. The IDF values are stored by
a persistent hash table. The TF-IDF values for each
website are stored in a serialized hash table for
that website.

