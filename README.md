#Trade App

##Description
This project allows you to process trade data from CSV and JSON files. It provides an API to upload files, process them, and return results.
##How to run the service
#####1.Run the project using Spring Boot:

```bash
./mvnw spring-boot:run
```

#####2.The service will be available at http://localhost:8080.

##How to use the API
Use the following curl request to upload a CSV file:
```bash
curl -X POST "http://localhost:8080/api/v1/enrich" -F "file=@yourPathToFile.csv"
```

- curl is a command-line tool used for making HTTP requests.

- -X POST
Specifies that this is a POST request.

- "http://localhost:8080/api/v1/enrich" - this is the URL where the request is sent. 
- localhost:8080 — The server is running locally on port 8080.
- /api/v1/enrich — The endpoint in your REST API that accepts the file.
- -F "file=@yourPathToFile.csv" - this part sends the file as a multipart/form-data request:
- F indicates form data (multipart/form-data).
- "file=@yourPathToFile.csv"
- file — The parameter name expected by the server. In your Spring Boot controller, it is likely defined as @RequestParam("file") MultipartFile file.
- @yourPathToFile.csv — The file path to upload. Example:
- @/home/user/data.csv (Linux/Mac)
- @"C:\Users\User\Documents\data.csv" (Windows, notice the quotes).

This request will send the trades.csv file to the server for processing. The server will process the data and return a formatted result in CSV format.

File processing
The uploaded CSV file should contain the following fields:

#####-date (date in yyyyMMdd format)
#####-id (identifier)
#####-currency (currency)
#####-price (price)

###Example input file:
```
date,productId,currency,price
20230107,1,USD,800.70
20230107,2,EUR,800.70
0230107,3,USD,800.70
```

###Example output file:
```
20230107,SomeProductName,USD,800.70
20230107,Missing Product Name,USD,800.70
```


##Code limitations
The maximum file size for upload is limited to 50 MB.
For proper processing, files must be in the specified format.
The server works only locally unless you configure it for deployment in another environment.

##Ideas for improvement
- Asynchronous Processing - currently trades are processed synchronously, you can add CompletableFuture (Java) or Coroutines (Kotlin).

- Data Streaming - currently data is loaded into memory, you can improve large file processing using Flux (Reactor) or Kotlin Flow.

- XML Support - currently only CSV and JSON support is implemented.

- Detailed Testing - add more unit and integration tests.
