package ir.ac.iust.dml.kg.knowledge.expert.web.services.hello;

import io.swagger.annotations.Api;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jws.WebService;

@Api("/hello")
@WebService(endpointInterface = "ir.ac.iust.dml.kg.knowledge.expert.web.services.hello.IHelloService")
public class HelloServiceImpl implements IHelloService {

    public String hello() {
        System.out.println("sayHi called");
        return "Hello " + SecurityContextHolder.getContext().getAuthentication().getName();
    }
}