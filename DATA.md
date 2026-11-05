### Modele danych

#### Ustawienia (DataStore)
| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| default_server | `int` | FK, Null | Id domyślnego serwera do którego są uploadowane pliki |
| device_name | `string` |  | Nazwa urządzenia |
| image_preview | `boolean` |  | Podgląd obrazów przed pobraniem ich na urządzienie |
| media_preview | `boolean` |  | Podgląd wideo i audio przed pobraniem ich na urządzienie |
| default_server_password | `string` |  | Domyślne hasło, używane kiedy serwer nie ma ustawionego hasła |
| default_download_directory | `string` |  | Domyślny katalog, używany kiedy serwer nie ma ustawionego katalogu |

#### Baza danych (Room)

##### Server
Dane programu działającego na innym urządzeniu. 

| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| id | `int` | PK, Autoincrement | Id serwera |
| name | `string` |  | Nazwa serwera |
| host | `string` |  | Ip / url serwera |
| port | `int` |  | Port serwera |
| password | `string` | Null | Hasło serwera; Jeśli null używa wartości z DataStore |
| directory | `string` | Null | Katalog do którego pobierane są pliki; Jeśli null używa wartości z DataStore |
| accepts_uploads | `boolean` |  | Czy serwer przyjmuje uploadowane pliki |
| last_sync | `datetime` |  | Czas ostatniej synchronizacji |
| last_ping | `datetime` |  | Czas ostatniej dostępności serwera |

##### File
Dane pliku znajdującego się na serwerze

| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| id | `int` | PK, Autoincrement | Id pliku |
| server_id | `int` | FK | Id serwera na którym plik jest hostowany |
| relative_path | `string` |  | Względna ścieżka pliku |
| file_size | `int` |  | Rozmiar pliku w bajtach |
| chunk_n | `int` |  | Ilość fragmentów pliku |
| chunk_size | `int` |  | Rozmiar pojedyńczego fragmentu w bajtach |
| thumbnail_uri | `string` | Null | Miniaturka w postaci uri; Nie null dla wideo i audio |
| thumbnail | `string` | Null | Miniaturka obrazu zakodowana w b64 |
| is_downloaded | `boolean` | | Czy plik jest pobrany |
| upload_state | {`None`, `MetadataUploaded`, `Uploaded`} |  | Status wgrania na serwer |

##### Chunk
Dane fragmentu pliku, wiele fragmentów pliku składa się na cały plik

| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| hash | `string` | PK | Hash fragmentu pliku `MD5(DATA + file_path + str(offset))` |
| file_id | `int` | FK | Id pliku do którego należy fragment |
| offset | `int` | | Przesunięcie fragmentu pliku w bajtach |
| size | `int` | | Rozmiar fragmentu w bajtach; Niekoniecznie równy file.chunk_size |
| is_downloaded | `boolean` | | Czy fragment jest pobrany |
| is_uploaded | `boolean` | | Czy fragment jest wgrany na serwer |

##### Transfer Queue
Dane plików które są w kolejce do przeniesienia 

| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| id | `int` | PK, Autoincrement | Id pliku w kolejce |
| file_id | `int` | FK | Id pliku |
| direction | {`Upload`, `Download`} |  | Kierunek przepływu |

##### Directory Shortcut
Dane katalogów widocznych na ekranie startowym, umożliwiających szybkie przesłanie dużych ilości plików

| Klucz                 | Typ danych      | Atrybuty     | Opis     |
|-----------------------|-----------------|--------------|----------|
| id | `int` | PK, Autoincrement | Id katalogu |
| server_id | `int` | FK, Null | Id serweru do którego wysyłane są pliki; Jeśli null używa wartości z DataStore |
| directory | `string` |  | Ścieżka katalogu |




