package edu.usc.ini.pipeline.rest;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.usc.ini.pipeline.rest.protocol.RegistrationResponse;
import edu.usc.ini.pipeline.rest.protocol.UnregistrationResponse;

@RestController
public class PipelineController {
    private final ClientDispatcher clientDispatcher = new ClientDispatcher();
    
    @RequestMapping("/register")
    public RegistrationResponse register() {
        String id = clientDispatcher.register();
		return new RegistrationResponse(id);
    }    
    
    @RequestMapping("/unregister")
    public UnregistrationResponse unregister(@RequestParam(value="clientId") String clientId) {
        String id = clientDispatcher.unregister(clientId);
        if (id != null) {
        	return new UnregistrationResponse(id);
        } else {
        	return new UnregistrationResponse();
        }
    }       
}
