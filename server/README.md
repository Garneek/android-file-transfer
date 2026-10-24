## Server endpoints

### Authentication

#### Authentication headers

Every request not marked as such is authenticated by a pre-shared key. 
There should be up to three additional headers in the HTTP header:
- `RandomString` - Random string used for the message
- `MessageSignature` - H(PSK + RandomString) or H(PSK + RandomString + Message body)

This is weak to replay attacks. 
The content of the message is unencrypted. 
The main focus of the authentication is prevention of downloading unwanted files, not ensuring privacy.
The hashing function should be SHA-512 and the digest should be in url-safe base64 format.
The server/client returns an error if the signature doesn't match.

##### Chunk ID
`MD5(DATA + FILE_NAME + str(OFFSET))`

### Endpoints

#### `/` (`GET`)

Gets metadata about the server.
Request body:
```
```

Response body:
```
{
	"name": "server name", // Server name, configured in the server conf
	"description": "some server", // Server description, configured in the server conf
	"host": "123.13.123.09", // Server ip
	"port": 12345, // Server port
	"version": "0.0.1", // Server api version
	"accepts-pushes": true // Whether or not the server accepts files pushed by other devices
	"chunk-size": 4194304 // The chunk size for pushes
}
```

#### `/files` (`GET`)

Gets the JSON list of files that can be downloaded from the server. 
The request can be filtered by a query string value.

QS:
- `addedAfter` - datetime in `YYYY-mm-dd hh:MM` format used for synchronisation of data cached by the client
- `requestThumbnails` - for requesting thumbnails. If not selected the field will be empty
- `requestReload` - for requesting the system to search for new local files. 

Request body:
```
```

Response body:
```
[
	{
		"name": "some-file.mp3", // file name 
		"size": 2133345, // size in bytes
		"chunk-n": 6, // number of chunks the file is split into
		"chunk-size": 4194304, // size of a chunk in bytes
		"thumbnail": "dasddfggasfdGVzdA==", // base64 encoding of the image. Can be empty if no thumbnail is present (128x128 in jpeg is 8kb so ~32k characters)
		"chunks": [
			{
				"id": "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY", // MD5 hash of the chunk, encoded in url safe base64. 
				"offset": 0, // offset of the chunk in the file
			},
			{
				"id": "NWExMDVlOGI5ZDQwZTEzMjk3ODBkNjJlYTIyNjVkOGE", // MD5 hash of the chunk, encoded in url safe base64. 
				"offset": 4194304, // offset of the chunk in the file
			},
			...
		] 
	}
]
```


#### `/files/{CHUNK_HASH}` (`GET`, `POST`)

##### `GET`
Gets the raw data of a chunk 

Request body:
```
```

Response body:
Raw byte data

##### `POST`
Writes a data chunk to a file.
The server first checks if the chunk was already written or if the chunk belongs to any file.
This request returns an error if the above is true. 
After receiving the chunk it is hashed and compared to the id, returning an error if it is incorrect.

Request body:
Raw byte data

Response body:
```
```

#### `/file` (`POST`)

Adds the information about a file to the server. The file chunks can only be sent after this packet is received and confirmed.

Request body:
```
{
	"name": "some-file.mp3", // file name 
	"size": 2133345, // size in bytes
	"chunk-n": 6, // number of chunks the file is split into
	"chunk-size": 4194304, // size of a chunk in bytes
	"chunks": [
		{
			"id": "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY", // MD5 hash of the chunk, encoded in url safe base64. 
			"offset": 0, // offset of the chunk in the file
		},
		{
			"id": "NWExMDVlOGI5ZDQwZTEzMjk3ODBkNjJlYTIyNjVkOGE", // MD5 hash of the chunk, encoded in url safe base64. 
			"offset": 4194304, // offset of the chunk in the file
		},
		...
	] 
}
```

Response body:
```
```