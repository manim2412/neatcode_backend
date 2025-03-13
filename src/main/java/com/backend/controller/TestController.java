package com.backend.controller;

import com.backend.piston.CodeFile;
import com.backend.piston.ExecutionResult;
import com.backend.piston.Piston;
import com.backend.piston.Runtime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public ResponseEntity<Map<String, String>> registrationRequest() {
        Map<String, String> map = new HashMap<>();
        map.put("test111", "test222");

        Piston api = Piston.getDefaultApi();//get the api at https://emkc.org/api/v2/piston
        Optional<Runtime> optionalRuntime = api.getRuntime("js");//get the javascript runtime
        if (optionalRuntime.isPresent()) {//check if the runtime exists
            Runtime runtime = optionalRuntime.get();
            CodeFile codeFile = new CodeFile("main.js", "console.log(\"Hello World!\")");//create the codeFile containing the javascript code
            ExecutionResult result = runtime.execute(codeFile);//execute the codeFile
            System.out.println(result.getOutput().getOutput());//print the result
        }

        Optional<Runtime> optionalRuntime1 = api.getRuntime("python");
        if (optionalRuntime1.isPresent()) {
            Runtime runtime = optionalRuntime1.get();
            CodeFile codeFile = new CodeFile("main.py", "for i in range(10):\n\tprint(i);");//create the codeFile containing the javascript code
            ExecutionResult result = runtime.execute(codeFile);//execute the codeFile
            System.out.println(result.getOutput().getOutput());//print the result
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }
}
