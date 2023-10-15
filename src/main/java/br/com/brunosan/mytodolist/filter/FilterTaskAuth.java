package br.com.brunosan.mytodolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.brunosan.mytodolist.user.IUserRepository;
import br.com.brunosan.mytodolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    
    private final IUserRepository repository;
    
    public FilterTaskAuth(IUserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // validar rota
        String path = request.getServletPath();
        if (path.startsWith("/tasks")) {
            // Pegar a autorização (user e pass)
            String authorization = request.getHeader("Authorization");
            String authEncoded = authorization.substring("Basic".length()).trim();
            
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
            
            // revelando as credenciais "username:password"
            String auth = new String(authDecoded);
            
            // separando as credenciais e criando um array ["username", "password"]
            String[] credentials = auth.split(":");
            String username = credentials[0];
            String password = credentials[1];
            
            // validando user
            UserModel user = this.repository.findByUsername(username);
            if (user == null) {
                // não pode seguir
                response.sendError(401);
            } else {
                BCrypt.Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    // pode seguir
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
