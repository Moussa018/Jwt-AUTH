package com.example.auth.authentification_servive.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component /* Dis à Spring de gérer automatiquement cette classe comme un composant de sécurité */
@RequiredArgsConstructor /* Génère le constructeur pour injecter JwtService et UserDetailsService */
public class JwtAthentificationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        /* 1. Récupérer l'en-tête "Authorization" de la requête HTTP */
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        /* 2. Si l'en-tête est vide ou ne commence pas par "Bearer ", on s'arrête là */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            /* On laisse la requête continuer vers les autres filtres (ex: pour Login/Register) */
            filterChain.doFilter(request, response);
            return; /* On sort de la méthode pour ne pas exécuter la suite inutilement */
        }

        /* 3. Extraire le jeton (JWT) en supprimant le mot "Bearer " (7 caractères) */
        jwt = authHeader.substring(7);

        /* 4. Extraire l'email de l'utilisateur caché à l'intérieur du jeton */
        userEmail = jwtService.extractUsername(jwt);

        /* 5. Vérifier si on a un email et si l'utilisateur n'est pas déjà authentifié dans le système */
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /* 6. Aller chercher les infos de l'utilisateur (roles, etc.) dans ta base PostgreSQL */
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            /* 7. Vérifier si le jeton est toujours valide (pas expiré et bon utilisateur) */
            if (jwtService.isTokenValid(jwt, userDetails)) {

                /* 8. Créer un "badge d'accès" avec l'utilisateur et ses permissions (ADMIN/USER) */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                /* 9. Ajouter des détails techniques de la requête au badge (ex: adresse IP) */
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /* 10. Enregistrer ce badge dans le contexte de sécurité de Spring pour valider l'accès */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        /* 11. Très important : laisser la requête continuer son chemin vers tes contrôleurs */
        filterChain.doFilter(request, response);
    }
}