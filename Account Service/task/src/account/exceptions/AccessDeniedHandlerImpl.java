package account.exceptions;

import account.entities.Event;
import account.entities.Events;
import account.entities.User;
import account.services.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Autowired
    private EventService eventService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("status", HttpStatus.FORBIDDEN.value());
        data.put("error", "Forbidden");
        data.put("message", "Access Denied!");
        data.put("path",  request.getRequestURI());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Event event = new Event(LocalDateTime.now(), Events.ACCESS_DENIED.toString(), user.getUsername(), request.getRequestURI(), request.getRequestURI());
        eventService.save(event);

        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }
}