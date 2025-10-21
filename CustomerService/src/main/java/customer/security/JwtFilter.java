package customer.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        try {
            // ✅ 1. Check if Authorization header is present and starts with "Bearer "
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                // ✅ 2. Validate the JWT
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token); // optional — depends on your token

                    // ✅ 3. Build authorities list
                    List<SimpleGrantedAuthority> authorities = (role == null)
                            ? Collections.emptyList()
                            : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    // ✅ 4. Create authentication token
                    var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // ✅ 5. Store authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            // ✅ Continue filter chain
            filterChain.doFilter(request, response);
        }

        // ---------- EXCEPTION HANDLING ----------
        catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\", \"status\": 401}");
            response.getWriter().flush();
        } 
        catch (SignatureException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid JWT signature\", \"status\": 403}");
            response.getWriter().flush();
        } 
        catch (MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Malformed JWT token\", \"status\": 400}");
            response.getWriter().flush();
        } 
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or missing token\", \"status\": 403}");
            response.getWriter().flush();
        }
    }
}