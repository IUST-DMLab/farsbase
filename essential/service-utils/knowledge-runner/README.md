#How to use

##Install this dependency
just set swagger json service address in `inputSpec` section and run `mvn install`

##Using dependency on other projects
###Maven setup
add this maven dependencies to your pom.xml:
```xml
<dependencies>
    <dependency>
        <groupId>ir.ac.iust.dml.kg.services</groupId>
        <artifactId>knowledge-runner-client</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.squareup.okio</groupId>
        <artifactId>okio</artifactId>
        <version>1.11.0</version>
    </dependency>
</dependencies>
```
###Code

just create a `ApiClient` based of service address:

```
ApiClient client = new ApiClient();
client.setBasePath("http://localhost:8089/rs");
```

Then use APIs:
```java
import ir.ac.iust.dml.kg.services.client.ApiClient;
import ir.ac.iust.dml.kg.services.client.ApiException;
import ir.ac.iust.dml.kg.services.client.swagger.HelloApi;
import ir.ac.iust.dml.kg.services.client.swagger.V1triplesApi;
import ir.ac.iust.dml.kg.services.client.swagger.model.TripleData;
import ir.ac.iust.dml.kg.services.client.swagger.model.TypedValueData;

class Main {
  public static void main(String[] args){
    HelloApi api = new HelloApi(client);
    final String answer = api.hello("Majid");
    V1triplesApi api2 = new V1triplesApi(client);
    
    TripleData data = new TripleData();
    data.setContext("http://wikipedia.org/Hossein_Khaledi");
    data.setUrls(Collections.singletonList("http://wikipedia.org/Hossein_Khaledi"));
    data.setSubject("http://wikipedia.org/Hossein_Khaledi");
    data.setPredicate("http://wikipedia.org/isDunkey");
    
    TypedValueData objectData = new TypedValueData();
    objectData.setLang("fa");
    objectData.setType(TypedValueData.TypeEnum.BOOLEAN);
    objectData.setValue("true");
    data.setObject(objectData);
    
    data.setModule("web");
    data.setPrecession(0.9);
    data.setParameters(Collections.singletonMap("key1", "value1"));   
  }
}
```

