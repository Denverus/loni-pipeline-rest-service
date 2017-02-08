package edu.usc.ini.pipeline.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.usc.ini.pipeline.rest.protocol.ConnectionResponse;
import edu.usc.ini.pipeline.rest.protocol.SessionsResponse;

@RestController
public class PipelineController {
    private final ClientDispatcher clientDispatcher = new ClientDispatcher();
    
    @RequestMapping("connection/connect")
    public ConnectionResponse connect(@RequestParam(value="server") String server, @RequestParam(value="port") int port, 
    		@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        ConnectionResponse connectionResponse = clientDispatcher.connect(server, port, username, password);
		return connectionResponse;
    }     

    @RequestMapping("connection/close")
    public ConnectionResponse close(@RequestParam(value="token") String token) {
        ConnectionResponse connectionResponse = clientDispatcher.close(token);
		return connectionResponse;
    }
    
    @RequestMapping("sessions/list")
    public SessionsResponse getSessionsList(@RequestParam(value="token") String token) {
    	SessionsResponse sessionsResponse = clientDispatcher.getSessionsList(token);
		return sessionsResponse;
    }      
}
