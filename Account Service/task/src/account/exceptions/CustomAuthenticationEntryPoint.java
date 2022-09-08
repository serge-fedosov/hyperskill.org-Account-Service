package account.exceptions;

import account.entities.Event;
import account.entities.Events;
import account.services.EventService;
import account.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;

import  java.util.Base64;
import java.util.LinkedHashMap;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {


    private final EventService eventService;
    private final UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public CustomAuthenticationEntryPoint(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

//    private static final long serialVersionUID = -8970718410437077606L;

//    @Autowired  // the Jackson object mapper bean we created in the config
//    private Jackson2JsonObjectMapper jackson2JsonObjectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {


        // FIX IT!
        //request.getRequestURI()
        // request.getHeader("authorization")  name:password base64

        if (request.getHeader("authorization") != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(request.getHeader("authorization").substring(6));
            String decodedString = new String(decodedBytes);
            String[] userPassword = decodedString.split(":");
            String username = userPassword[0];

            if (!e.getMessage().equals("User account is locked")) {

                Event event = new Event(LocalDateTime.now(), Events.LOGIN_FAILED.toString(), username, request.getRequestURI(), request.getRequestURI());
                eventService.save(event);

                if (userService.loginFailed(username)) { // locked user

                    LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                    data.put("timestamp", LocalDateTime.now().toString());
                    data.put("status", HttpStatus.UNAUTHORIZED.value());
                    data.put("error", "Unauthorized");
                    data.put("message", "User account is locked");
                    data.put("path", request.getRequestURI());

                    response.getOutputStream().println(objectMapper.writeValueAsString(data));

                    //                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account is locked");
                }
            } else {
                // если юзер залочен, в лог писать не нужно, но нужно вернуть не пустое тело ответа.
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                data.put("timestamp", LocalDateTime.now().toString());
                data.put("status", HttpStatus.UNAUTHORIZED.value());
                data.put("error", "Unauthorized");
                data.put("message", "User account is locked");
                data.put("path", request.getRequestURI());

                response.getOutputStream().println(objectMapper.writeValueAsString(data));

            }
        }

//        Event event = new Event(LocalDateTime.now(), Events.LOGIN_FAILED.toString(), userPassword[0], "/api/empl/payment", "/api/empl/payment");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        /*
          This is a pojo you can create to hold the repsonse code, error, and description.
          You can create a POJO to hold whatever information you want to send back.
        */
//        CustomError error = new CustomError(HttpStatus.FORBIDDEN, error, description);

        /*
          Here we're going to creat a json strong from the CustomError object we just created.
          We set the media type, encoding, and then get the write from the response object and write
      our json string to the response.
        */
//        try {
////            String json = jackson2JsonObjectMapper.toJson(error);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
////            response.getWriter().write(json);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }

    }
}


//@Component
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
//        // This is invoked when user tries to access a secured REST resource without supplying any credentials
//        // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//    }
//}