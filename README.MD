# databaseCourseMiniProject

### English TL;DR
Miniproject made during database course.
It`s main task is to download Covid-19 data from multiple sources published by various states,
then convert them into objects in Java and map them into Derby database using Hibernate.
In addition it visualizes data gathered in database.

## Opis projektu
Projekt stworzony w ramach zajęć na studiach.
Trzeba osobno stworzyć bazę danych i wpisać ją do configa Hibernate`a.
Program wypełnia bazę danymi związany z epidemią koronawirusa, pobierając je z określonych źródeł.

 - Dane z USA - [https://github.com/nytimes/covid-19-data](https://github.com/nytimes/covid-19-data)
 - Dane z Włoch - [https://github.com/pcm-dpc/COVID-19](https://github.com/pcm-dpc/COVID-19)

Model danych zawiera:
 1. odpowiednik kraju
 2. odpowiednik pierwszego stopnia podziału terytorialnego
 3. odpowiednik drugiego stopnia podziału terytorialnego 
 4. wpis z datą i liczbą przypadków zarażenia 

Te obiekty mapowane są z użyciem Hibernate.
Narzucone są odpowiednie warunki integralnościowe, by nie powielać wpisów.
Wizualizacja zawiera wykres liczby chorych od czasu w zależności od wybranych opcji.

## Opis stworzonych klas 
### model

 - **DataRecord** - klasa zawierająca datę i liczbę chorych osób w danym odpowiedniku drugiego stopnia podziału terytorialnego, z którym tworzy relację **@ManyToOne**,
 - **Province** - klasa zawierająca nazwę i kod odpowiednika drugiego stopnia podziału terytorialnego znajdującego się w danym odpowiedniku pierwszego stopnia podziału terytorialnego, z którym tworzy relację **@ManyToOne**,
 - **Region** - klasa zawierająca nazwę i kod odpowiednika pierwszego stopnia podziału terytorialnego znajdującego się w danym odpowiedniku kraju, z którym tworzy relację **@ManyToOne**,
 - **State** - klasa zawierająca nazwę odpowiednika kraju. 
### datamanage
 - **DataDownloader** - klasa pobierająca pliki ***.csv** z odpowiednich źródeł,
 - **DataParser** - klasa odpowiadająca za czytanie plików ***.csv**, zapamiętuje nazwy kolumn i iteruje po wierszach plików,
 - **DataSource** - klasa abstrakcyjna, ogniwo pośredniczące między czytaniem danych z pliku, a wpisywaniem ich do bazy, korzysta z **DataDownloader** i **DataParser** i ukrywa za metodami abstrakcyjnymi ich implementację, co pozwala na łatwe rozszerzanie aplikacji o nowe źródła danych,
 - **DataSourceItaly** - klasa dziedzicząca z **DataSource** i implementująca metody niezbędne do wydobywania danych dla Włoch,
 - **DataSourceUSA** - klasa dziedzicząca z **DataSource** i implementująca metody niezbędne do wydobywania danych dla USA,
 - **DBFiller** - klasa korzystająca z **DataSource**, która uzupełnia bazę danych, korzystając z danych zaczerpniętych z **DataSource**,
 - **DBGetter** - klasa zawierająca metody, za którymi kryją się zapytania do bazy danych.
### view
 - **OptionsPanel** - klasa zawierająca listy rozwijane elementów zgromadzonych w bazie z podziałem na kraje, regiony i prowincje, dzięki niej można wybrać interesujące nas pozycje i zaktualizować obecny wykres,
 - **PlotPanel** - klasa zawierająca panel do rysowania wykresów,
 - **Visualizer** - klasa zawierająca główne okno aplikacji.
### istotne uwagi
 - **jfreechart** - biblioteka użyta przez nas do rysowania wykresów,
 - **VM options** - wymaga uzupełnienia o linijkę **--add-modules jdk.incubator.httpclient**