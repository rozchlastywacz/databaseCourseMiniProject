# databaseCourseMiniProject

### English TL;DR
Miniproject made during database course.
It`s main task is to download Covid-19 data from multiple sources published by various states,
then convert them into objects in Java and map them into Derby database using Hibernate.
In addition it visualizes data gathered in database.

### Opis projektu
Projekt stworzony w ramach zajęć na studiach.
Trzeba osobno stworzyć bazę danych i wpisać ją do configa HIbernate`a.
Program wypełnia bazę danymi związany z epidemią koronawirusa, pobierając je z określonych źródeł.
Model danych zawiera:
 1. odpowiednik kraju - State
 2. odpowiednik pierszego stopnia podziału terytorialnego - Region
 3. odpowiednik drugiego stopnia podziału terytorialnego - Province
 4. wpis z datą i liczbą przypadków zarażenia - DataRecord  
Te obiekty mapowane są z użyciem HIbernate.
Narzucone są odpowiednie warunki integralnościowe, by nie powielać wpisów.
Wizualizacja zawiera wykres liczby chorych od czasu w zależności od wybranych opcji.