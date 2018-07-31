package ir.ac.iust.dml.kg.knowledge.runner.services.hello;

import io.swagger.annotations.Api;

import javax.jws.WebService;

@Api("/hello")
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.runner.services.hello.IHelloService")
public class HelloServiceImpl implements IHelloService {

    public String hello(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }
}