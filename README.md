### Osmium

Aplikacja transferu plików przez HTTP. 

##### Planowane funkcjonalności:
- Przesył danych pomiędzy telefonem a serwerem
- Przesył zawartości katalogu z telefonu do serwera
- Wybór i pobranie plików z serwera
- Wybór i wgranie plików na serwer
- Opcja usunięcia plików z telefonu po wgraniu ich na serwer
- Wyświetlenie miniaturek obrazów, fragmentów wideo i audio, bez pobrania całego pliku.
- Możliwość zatrzymania i wznowienia przesyłu danych
- Zapis i zmiana ustawień serwera
- Obsługa kilku serwerów
- Możliwość dodania do wgrywanych plików zdjęcia zrobionego w aplikacji 

##### Planowana architektura
- Dane plików i ich fragmentów przechowywane w bazie danych
- Dane katalogów i serwerów przechowywane w bazie danych
- Miniaturki i fragmenty plików przechowywane na dysku w przestrzeni cache aplikacji
