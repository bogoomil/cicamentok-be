package hu.boga.cicamentok.cicamentokbe.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "http://localhost:8080")
public class FooController {


    @GetMapping(value = "/public/get")
    public FooDto findOne(@RequestParam("id") String id) {
        return generateTestData().get(0);
    }

    @GetMapping(value = "/private/get")
    @PreAuthorize(value = "hasRole('ROLE_CICAMENTO_ADMIN')")
    public FooDto privateGet(@RequestParam("id") String id) {
        return generateTestData().get(0);
    }


    @PostMapping("/private/save")
    @PreAuthorize(value = "hasRole('ROLE_CICAMENTO_ADMIN')")
    public FooDto savePrivate(@RequestBody FooDto dto) {
        System.out.println("dto: " + dto.getId());
        return dto;
    }

    private List<FooDto> generateTestData(){
        List<FooDto> l = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            FooDto f = new FooDto();
            f.setId(UUID.randomUUID().toString());
            f.setName(UUID.randomUUID().toString());
            l.add(f);
        }
        return l;
    }

    static class FooDto{
        private String id;
        private String name;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

    }

    private String getVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        String verifier = Base64.getUrlEncoder().encodeToString(code);

        return verifier;
    }

    private String getChallenge(String verifier) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = verifier.getBytes("US-ASCII");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);

        return challenge;
    }
}