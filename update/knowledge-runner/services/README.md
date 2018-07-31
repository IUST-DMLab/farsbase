# Prerequisites
* JDK 1.8
* Mongodb run on localhost:27017

# Run
* first compile and install by `mvn install` in parent folder
* run `java -jar knowlede-store-services.jar` in services\target directory
* or run `mvn -pServer` in services directory

# Service Response

## Success
* Response Code is `200`
* Response Body is result of action

## Failed
* Response Body is message of error
* Response Code is `500` on internal error
* Response Code is `400` on validation error
* Response Code is `javax.ws.rs.core.Response.Status`

