package br.com.brunosan.mytodolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final IUserRepository repository;
    
    public UserController(IUserRepository repository) {
        this.repository = repository;
    }
    
    @PostMapping
    public ResponseEntity create(@RequestBody UserModel userModel) {
        UserModel user = this.repository.findByUsername(userModel.getUsername());
        
        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe.");
        }
        
        userModel.setPassword(BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray()));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.repository.save(userModel));
    }
}
