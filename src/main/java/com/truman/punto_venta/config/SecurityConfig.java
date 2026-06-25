package com.truman.punto_venta.config;

import com.truman.punto_venta.usuario.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth

                // ── Solo ADMIN ──────────────────────────────────
                .requestMatchers("/").hasRole("ADMIN")
                .requestMatchers("/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/clientes/**").hasRole("ADMIN")
                .requestMatchers("/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/reportes/**").hasRole("ADMIN")

                // ── ADMIN y CAJERO ───────────────────────────────
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                .requestMatchers("/productos/**").hasAnyRole("ADMIN", "CAJERO")

                // ── Público ──────────────────────────────────────
                .requestMatchers("/login", "/css/**", "/js/**", "/favicon.ico").permitAll()

                // ── Cualquier otra ruta requiere login ───────────
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/ventas/nueva", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Necesario para que el POST de ventas/nueva (fetch) no sea bloqueado
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ventas/nueva", "/api/**")
            );

        return http.build();
    }
}