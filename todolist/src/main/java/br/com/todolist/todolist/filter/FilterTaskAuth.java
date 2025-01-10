package br.com.todolist.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.todolist.todolist.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        System.out.println("DEBUG - Path da requisição: " + servletPath);

        if (servletPath.startsWith("/tasks/")) {
            // Pegar a autenticação (usuario e senha)
            var authorization = request.getHeader("Authorization");
            System.out.println("DEBUG - Authorization header: " + authorization);

            if (authorization == null) {
                response.sendError(401);
                return;
            }

            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecode);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("DEBUG - Username: " + username);

            // Validar usuário
            var user = this.userRepository.findByUserName(username);
            if (user == null) {
                response.sendError(401);
                return;
            }

            // Validar senha
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (!passwordVerify.verified) {
                response.sendError(401);
                return;
            }

            request.setAttribute("userId", user.getId());
            System.out.println("DEBUG - UserId setado no request: " + user.getId());
        }

        filterChain.doFilter(request, response);
    }
}