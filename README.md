# Document Micro Service

This project is created by using maven quick start archetype. 
Spring Boot is used to create Rest API for uploading and downloading documents.
All the dependencies can be found in pom.xml.
I tested the REST APIs via Postman to confirm that they are working as expected. 

Queries to be used for testing:

To upload a single file (@RequestParam fileType, file):
    
        - http://localhost:8080/<username>/upload-file 

To upload multiple files  (@RequestParam fileType, files): 
  
        - http://localhost:8080/<username>/upload-multiple-files

To list all the documents of a user:
        
        - http://localhost:8080/<username>/list-all

To get the metadata of a file:

        - http://localhost:8080/<username>/<filename>

To modify the type of the uploaded file:

        - http://localhost:8080/<username>/<filename>/<fileType>/modify
        
        
        
        
The system does not persist data. HashMap is used to keep the data, so when the application restarts, the data will be lost. The test cases can be found under the test directory. 
Because of the time limitation, only happy cases are added.

