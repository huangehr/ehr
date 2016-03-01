package com.yihu.ehr.api.error;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 11:04
 */
//@RestController
public class ErrorControllerImpl implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public void error() throws IOException {
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
