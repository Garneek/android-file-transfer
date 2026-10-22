## Osmium

Aplikacja służąca do bezprzewodowego transferu plików przez HTTP. Umożliwia ona połączenie w modelu klient-serwer z innym urządzeniem (komputerem stacjonarnym lub innym urządzeniem mobilnym) w sieci lokalnej, gdzie połączenie inicjuje klient. Klient może następnie pushować pliki na serwer, o ile ten na to pozwala.

#### Planowane funkcjonalności:
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

#### Planowana architektura
- Dane plików i ich fragmentów przechowywane w bazie danych
- Dane katalogów i serwerów przechowywane w bazie danych
- Miniaturki i fragmenty plików przechowywane na dysku w przestrzeni cache aplikacji

### Ekrany
Widok główny, lista serwerów. Pozwala na wybranie widoku dostępnych plików serwera oraz dodanie nowego serwera. Pozwala na szybkie wgranie plików z danego katalogu do domyślnego serwera.

![Main view](resources/mockup/Main%20view.png)

Dodawanie nowego serwera. 

![New server view](resources/mockup/New%20server%20view.png)

Widok serwera. Wyświetla pliki dostępne do pobrania i umożliwia przejście do uploadu plików i edycji ustawień

![Server view](resources/mockup/Server%20view.png)

Widok wgrywania plików. Pozwala dodać pliki do kolejki przegrywania z menu wyboru plików, oraz za pomocą aparatu. 

![Upload view](resources/mockup/Upload%20view.png)

Ustawienia serwera. Zmiana ustawień istniejącego serwera

![Server settings view](resources/mockup/Server%20settings%20view.png)

Widok transferu. Podgląd, pauza i odwołanie transferu

![Transfer view](resources/mockup/Transfer%20view.png)

Ustawienia globalne

![Settings view](resources/mockup/Settings%20view.png)